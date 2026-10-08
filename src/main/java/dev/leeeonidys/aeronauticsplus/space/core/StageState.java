package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/** A physically meaningful stage assembled from tanks and engines, not a recipe template. */
public record StageState(
        String id,
        List<MassElement> structure,
        List<TankState> tanks,
        List<EngineState> engines,
        boolean separable) {
    public StageState {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Stage id must not be blank");
        }
        structure = List.copyOf(structure == null ? List.of() : structure);
        tanks = List.copyOf(tanks == null ? List.of() : tanks);
        engines = List.copyOf(engines == null ? List.of() : engines);
        if (tanks.isEmpty()) {
            throw new IllegalArgumentException("A stage must contain at least one tank");
        }
        if (engines.isEmpty()) {
            throw new IllegalArgumentException("A stage must contain at least one engine");
        }
    }

    public double structuralDryMassKg() {
        return structure.stream().mapToDouble(MassElement::massKg).sum();
    }

    public double propellantMassKg() {
        return tanks.stream().mapToDouble(tank -> tank.contents().totalMassKg()).sum();
    }

    public double dryMassKg() {
        return structuralDryMassKg()
                + tanks.stream().mapToDouble(TankState::dryMassKg).sum()
                + engines.stream().mapToDouble(EngineState::dryMassKg).sum();
    }

    public double totalMassKg() {
        return dryMassKg() + propellantMassKg();
    }

    public double thrustNewtons() {
        return engines.stream().mapToDouble(EngineState::activeThrustNewtons).sum();
    }

    public Vector3d netThrustNewtons() {
        Vector3d sum = Vector3d.ZERO;
        for (EngineState engine : engines) {
            sum = sum.add(engine.activeThrustVectorNewtons());
        }
        return sum;
    }

    public double effectiveExhaustVelocityMetersPerSecond() {
        double thrust = thrustNewtons();
        if (!(thrust > 0.0)) {
            return 0.0;
        }
        return engines.stream()
                .mapToDouble(engine -> engine.activeThrustNewtons() * engine.exhaustVelocityMetersPerSecond())
                .sum() / thrust;
    }

    /**
     * Ideal stage delta-v using thrust-weighted exhaust velocity.
     * Mixture compatibility and feed losses are validated by the later vessel compiler.
     */
    public double idealDeltaV() {
        double thrust = thrustNewtons();
        double dryMass = dryMassKg();
        if (!(thrust > 0.0) || !(dryMass > 0.0) || !(totalMassKg() > dryMass)) {
            return 0.0;
        }
        return effectiveExhaustVelocityMetersPerSecond() * Math.log(totalMassKg() / dryMass);
    }

    public Vector3d centerOfMassMeters() {
        double mass = 0.0;
        Vector3d moment = Vector3d.ZERO;
        for (MassElement element : structure) {
            mass += element.massKg();
            moment = moment.add(element.localPositionMeters().multiply(element.massKg()));
        }
        for (TankState tank : tanks) {
            double tankMass = tank.totalMassKg();
            mass += tankMass;
            moment = moment.add(tank.localPositionMeters().multiply(tankMass));
        }
        for (EngineState engine : engines) {
            mass += engine.dryMassKg();
            moment = moment.add(engine.localPositionMeters().multiply(engine.dryMassKg()));
        }
        if (!(mass > 0.0)) {
            throw new IllegalStateException("Stage " + id + " has no mass");
        }
        return moment.multiply(1.0 / mass);
    }

    public Vector3d centerOfThrustMeters() {
        double thrust = 0.0;
        Vector3d moment = Vector3d.ZERO;
        for (EngineState engine : engines) {
            double engineThrust = engine.activeThrustNewtons();
            if (engineThrust > 0.0) {
                thrust += engineThrust;
                moment = moment.add(engine.localPositionMeters().multiply(engineThrust));
            }
        }
        if (!(thrust > 0.0)) {
            return centerOfMassMeters();
        }
        return moment.multiply(1.0 / thrust);
    }

    public ThrustGeometry thrustGeometry() {
        Vector3d centerOfMass = centerOfMassMeters();
        Vector3d netThrust = netThrustNewtons();
        Vector3d centerOfThrust = centerOfThrustMeters();
        Vector3d momentArm = centerOfThrust.subtract(centerOfMass);
        double offset = netThrust.magnitudeSquared() > 0.0
                ? momentArm.cross(netThrust).magnitude() / netThrust.magnitude()
                : 0.0;
        return new ThrustGeometry(centerOfMass, centerOfThrust, netThrust, momentArm, offset);
    }

    /** Burns propellant deterministically, stopping exactly when either resource is exhausted. */
    public StageBurnResult burn(double requestedSeconds) {
        if (requestedSeconds < 0.0 || !Double.isFinite(requestedSeconds)) {
            throw new IllegalArgumentException("Burn duration must be finite and non-negative");
        }
        double fuelRate = 0.0;
        double oxidizerRate = 0.0;
        for (EngineState engine : engines) {
            double flow = engine.propellantFlowKgPerSecond();
            double ratio = matchingMixtureRatio(engine);
            fuelRate += flow / (1.0 + ratio);
            oxidizerRate += flow * ratio / (1.0 + ratio);
        }
        double availableFuel = tanks.stream().mapToDouble(t -> t.contents().fuelMassKg()).sum();
        double availableOxidizer = tanks.stream().mapToDouble(t -> t.contents().oxidizerMassKg()).sum();
        double elapsed = requestedSeconds;
        if (fuelRate > 0.0) elapsed = Math.min(elapsed, availableFuel / fuelRate);
        if (oxidizerRate > 0.0) elapsed = Math.min(elapsed, availableOxidizer / oxidizerRate);
        double fuelUsed = fuelRate * elapsed;
        double oxidizerUsed = oxidizerRate * elapsed;
        List<TankState> nextTanks = tanks.stream().map(tank -> {
            double fuelShare = availableFuel > 0.0 ? fuelUsed * tank.contents().fuelMassKg() / availableFuel : 0.0;
            double oxidizerShare = availableOxidizer > 0.0
                    ? oxidizerUsed * tank.contents().oxidizerMassKg() / availableOxidizer : 0.0;
            return tank.consume(fuelShare, oxidizerShare);
        }).toList();
        StageState next = new StageState(id, structure, nextTanks, engines, separable);
        boolean depleted = elapsed + 1.0e-9 < requestedSeconds;
        return new StageBurnResult(next, elapsed, fuelUsed, oxidizerUsed, depleted);
    }

    private double matchingMixtureRatio(EngineState engine) {
        return tanks.stream()
                .filter(tank -> tank.contents().fuelId().equals(engine.fuelId())
                        && tank.contents().oxidizerId().equals(engine.oxidizerId()))
                .filter(tank -> tank.contents().fuelMassKg() > 0.0 && tank.contents().oxidizerMassKg() > 0.0)
                .mapToDouble(tank -> tank.contents().mixtureRatio())
                .findFirst()
                .orElse(1.0);
    }
}
