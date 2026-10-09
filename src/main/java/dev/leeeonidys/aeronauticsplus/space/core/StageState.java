package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    /** Game-scale CMG torque; ISS CMG class is ~250 N·m. */
    public static final double GYRO_TORQUE_NEWTON_METERS = 250.0;

    public List<EngineState> mainEngines() {
        return engines.stream().filter(engine -> !engine.rcs()).toList();
    }

    public List<EngineState> rcsThrusters() {
        return engines.stream().filter(EngineState::rcs).toList();
    }

    public boolean hasGyro() {
        return structure.stream().anyMatch(element -> element.role() == MassElement.Role.GYRO);
    }

    public boolean hasSolar() {
        return structure.stream().anyMatch(element -> element.role() == MassElement.Role.SOLAR);
    }

    public boolean hasHabitat() {
        return structure.stream().anyMatch(element -> element.role() == MassElement.Role.HABITAT);
    }

    public boolean hasDocking() {
        return structure.stream().anyMatch(element -> element.role() == MassElement.Role.DOCKING);
    }

    public boolean hasBattery() {
        return structure.stream().anyMatch(element -> element.role() == MassElement.Role.BATTERY);
    }

    public boolean hasRadiator() {
        return structure.stream().anyMatch(element -> element.role() == MassElement.Role.RADIATOR);
    }

    public boolean hasAntenna() {
        return structure.stream().anyMatch(element -> element.role() == MassElement.Role.ANTENNA);
    }

    public boolean hasLab() {
        return structure.stream().anyMatch(element -> element.role() == MassElement.Role.LAB);
    }

    public int gyroCount() {
        return (int) structure.stream().filter(element -> element.role() == MassElement.Role.GYRO).count();
    }

    public double thrustNewtons() {
        return mainEngines().stream().mapToDouble(EngineState::activeThrustNewtons).sum();
    }

    public Vector3d netThrustNewtons() {
        Vector3d sum = Vector3d.ZERO;
        for (EngineState engine : mainEngines()) {
            sum = sum.add(engine.activeThrustVectorNewtons());
        }
        return sum;
    }

    public Vector3d netRcsThrustNewtons() {
        Vector3d sum = Vector3d.ZERO;
        for (EngineState engine : rcsThrusters()) {
            sum = sum.add(engine.activeThrustVectorNewtons());
        }
        return sum;
    }

    public double effectiveExhaustVelocityMetersPerSecond() {
        double thrust = thrustNewtons();
        if (!(thrust > 0.0)) {
            return 0.0;
        }
        return mainEngines().stream()
                .mapToDouble(engine -> engine.activeThrustNewtons() * engine.exhaustVelocityMetersPerSecond())
                .sum() / thrust;
    }

    /**
     * Ideal stage delta-v using thrust-weighted exhaust velocity.
     * Burn consumption uses each engine's declared oxidizer/fuel mass ratio.
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
        for (EngineState engine : mainEngines()) {
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

    /**
     * Point-mass inertia about {@code pivot} for rotation around {@code axis}.
     * Occupancy centroids are already written into the mass positions.
     */
    public double momentOfInertiaKgM2(Vector3d pivot, Vector3d axis) {
        if (pivot == null || axis == null || !(axis.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Inertia pivot and axis are required");
        }
        Vector3d n = axis.normalized();
        double inertia = 0.0;
        for (MassElement element : structure) {
            inertia += inertiaOf(element.massKg(), element.localPositionMeters(), pivot, n);
        }
        for (TankState tank : tanks) {
            inertia += inertiaOf(tank.totalMassKg(), tank.localPositionMeters(), pivot, n);
        }
        for (EngineState engine : engines) {
            inertia += inertiaOf(engine.dryMassKg(), engine.localPositionMeters(), pivot, n);
        }
        return inertia;
    }

    private static double inertiaOf(double massKg, Vector3d position, Vector3d pivot, Vector3d axis) {
        return massKg * position.subtract(pivot).cross(axis).magnitudeSquared();
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

    public double fairingMassKg() {
        return structure.stream()
                .filter(element -> element.role() == MassElement.Role.FAIRING)
                .mapToDouble(MassElement::massKg)
                .sum();
    }

    public double payloadMassKg() {
        return structure.stream()
                .filter(element -> element.role() == MassElement.Role.PAYLOAD)
                .mapToDouble(MassElement::massKg)
                .sum();
    }

    public boolean hasFairing() {
        return fairingMassKg() > 0.0;
    }

    public boolean hasPayload() {
        return payloadMassKg() > 0.0;
    }

    /**
     * Drops jettisonable fairing mass. Payload and propulsion stay on the stage,
     * matching Soyuz / Falcon 9 / Saturn V after leaving dense atmosphere.
     */
    public StageState withoutFairings() {
        List<MassElement> kept = structure.stream()
                .filter(element -> element.role() != MassElement.Role.FAIRING)
                .toList();
        if (kept.size() == structure.size()) {
            return this;
        }
        return new StageState(id, kept, tanks, engines, separable);
    }

    public StageState withEngines(List<EngineState> nextEngines) {
        return new StageState(id, structure, tanks, nextEngines, separable);
    }

    public StageState withDisabledEngines() {
        return withEngines(engines.stream().map(engine -> engine.withEnabled(false)).toList());
    }

    /** True when every firing main engine has a tank with matching fuel and oxidizer ids. */
    public boolean hasCompatibleFeed() {
        return hasCompatibleFeed(mainEngines());
    }

    public boolean hasCompatibleRcsFeed() {
        return hasCompatibleFeed(rcsThrusters());
    }

    private boolean hasCompatibleFeed(List<EngineState> group) {
        for (EngineState engine : group) {
            if (!(engine.activeThrustNewtons() > 0.0)) {
                continue;
            }
            boolean matched = false;
            for (TankState tank : tanks) {
                if (feeds(engine, tank)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }

    /**
     * Burns propellant at each main engine's declared mixture ratio, drawing only from tanks
     * whose fuel and oxidizer ids match. Stops when either species of a firing pair is gone.
     */
    public StageBurnResult burn(double requestedSeconds) {
        return burnEngines(mainEngines(), requestedSeconds);
    }

    /** RCS cluster burn. Does not fire the main engines. */
    public StageBurnResult burnRcs(double requestedSeconds) {
        return burnEngines(rcsThrusters(), requestedSeconds);
    }

    private StageBurnResult burnEngines(List<EngineState> group, double requestedSeconds) {
        if (requestedSeconds < 0.0 || !Double.isFinite(requestedSeconds)) {
            throw new IllegalArgumentException("Burn duration must be finite and non-negative");
        }
        Map<PropellantSpecies, double[]> rates = new LinkedHashMap<>();
        for (EngineState engine : group) {
            if (!(engine.activeThrustNewtons() > 0.0)) {
                continue;
            }
            PropellantSpecies species = new PropellantSpecies(engine.fuelId(), engine.oxidizerId());
            double[] rate = rates.computeIfAbsent(species, ignored -> new double[2]);
            rate[0] += engine.fuelFlowKgPerSecond();
            rate[1] += engine.oxidizerFlowKgPerSecond();
        }
        Map<PropellantSpecies, double[]> available = new LinkedHashMap<>();
        for (TankState tank : tanks) {
            PropellantSpecies species = new PropellantSpecies(
                    tank.contents().fuelId(), tank.contents().oxidizerId());
            if (!rates.containsKey(species)) {
                continue;
            }
            double[] stock = available.computeIfAbsent(species, ignored -> new double[2]);
            stock[0] += tank.contents().fuelMassKg();
            stock[1] += tank.contents().oxidizerMassKg();
        }
        double elapsed = requestedSeconds;
        for (Map.Entry<PropellantSpecies, double[]> entry : rates.entrySet()) {
            double[] rate = entry.getValue();
            double[] stock = available.getOrDefault(entry.getKey(), new double[2]);
            if (rate[0] > 0.0) {
                elapsed = Math.min(elapsed, stock[0] / rate[0]);
            }
            if (rate[1] > 0.0) {
                elapsed = Math.min(elapsed, stock[1] / rate[1]);
            }
        }
        double fuelUsed = 0.0;
        double oxidizerUsed = 0.0;
        Map<PropellantSpecies, double[]> consumed = new LinkedHashMap<>();
        for (Map.Entry<PropellantSpecies, double[]> entry : rates.entrySet()) {
            double[] rate = entry.getValue();
            double pairFuel = rate[0] * elapsed;
            double pairOxidizer = rate[1] * elapsed;
            consumed.put(entry.getKey(), new double[] {pairFuel, pairOxidizer});
            fuelUsed += pairFuel;
            oxidizerUsed += pairOxidizer;
        }
        List<TankState> nextTanks = tanks.stream().map(tank -> {
            PropellantSpecies species = new PropellantSpecies(
                    tank.contents().fuelId(), tank.contents().oxidizerId());
            double[] pairUsed = consumed.get(species);
            double[] stock = available.get(species);
            if (pairUsed == null || stock == null) {
                return tank;
            }
            double fuelShare = stock[0] > 0.0 ? pairUsed[0] * tank.contents().fuelMassKg() / stock[0] : 0.0;
            double oxidizerShare = stock[1] > 0.0
                    ? pairUsed[1] * tank.contents().oxidizerMassKg() / stock[1] : 0.0;
            return tank.consume(fuelShare, oxidizerShare);
        }).toList();
        StageState next = new StageState(id, structure, nextTanks, engines, separable);
        boolean depleted = elapsed + 1.0e-9 < requestedSeconds;
        return new StageBurnResult(next, elapsed, fuelUsed, oxidizerUsed, depleted);
    }

    private static boolean feeds(EngineState engine, TankState tank) {
        return tank.contents().fuelId().equals(engine.fuelId())
                && tank.contents().oxidizerId().equals(engine.oxidizerId());
    }

    private record PropellantSpecies(String fuelId, String oxidizerId) {
    }
}
