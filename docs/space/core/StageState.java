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
        if (!(thrust > 0.0)) {
            return 0.0;
        }
        double effectiveExhaustVelocity = engines.stream()
                .mapToDouble(engine -> engine.activeThrustNewtons() * engine.exhaustVelocityMetersPerSecond())
                .sum() / thrust;
        return effectiveExhaustVelocity * Math.log(totalMassKg() / dryMassKg());
    }
}
