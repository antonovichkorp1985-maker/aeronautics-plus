package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/** A physically meaningful stage assembled from tanks and engines, not a recipe template. */
public record StageState(
        String id,
        double structuralDryMassKg,
        List<TankState> tanks,
        List<EngineState> engines,
        boolean separable) {
    public StageState {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Stage id must not be blank");
        }
        if (structuralDryMassKg < 0.0 || !Double.isFinite(structuralDryMassKg)) {
            throw new IllegalArgumentException("Structural dry mass must be finite and non-negative");
        }
        tanks = List.copyOf(tanks == null ? List.of() : tanks);
        engines = List.copyOf(engines == null ? List.of() : engines);
        if (tanks.isEmpty()) {
            throw new IllegalArgumentException("A stage must contain at least one tank");
        }
        if (engines.isEmpty()) {
            throw new IllegalArgumentException("A stage must contain at least one engine");
        }
    }

    public double propellantMassKg() {
        return tanks.stream().mapToDouble(tank -> tank.contents().totalMassKg()).sum();
    }

    public double dryMassKg() {
        return structuralDryMassKg
                + tanks.stream().mapToDouble(TankState::dryMassKg).sum()
                + engines.stream().mapToDouble(EngineState::dryMassKg).sum();
    }

    public double totalMassKg() {
        return dryMassKg() + propellantMassKg();
    }

    public double thrustNewtons() {
        return engines.stream().mapToDouble(EngineState::activeThrustNewtons).sum();
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
        double effectiveExhaustVelocity = engines.stream()
                .mapToDouble(engine -> engine.activeThrustNewtons() * engine.exhaustVelocityMetersPerSecond())
                .sum() / thrust;
        return effectiveExhaustVelocity * Math.log(totalMassKg() / dryMass);
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
        StageState next = new StageState(id, structuralDryMassKg, nextTanks, engines, separable);
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
