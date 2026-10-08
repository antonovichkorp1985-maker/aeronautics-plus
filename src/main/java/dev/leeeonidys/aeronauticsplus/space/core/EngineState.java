package dev.leeeonidys.aeronauticsplus.space.core;

/** Logical engine specification; engine placement/vector is a later vessel layer. */
public record EngineState(
        String id,
        String fuelId,
        String oxidizerId,
        double dryMassKg,
        double thrustNewtons,
        double specificImpulseSeconds,
        double throttle,
        double gimbalDegrees,
        boolean enabled) {
    public EngineState {
        requireName(id, "Engine");
        requireName(fuelId, "Fuel");
        requireName(oxidizerId, "Oxidizer");
        if (dryMassKg < 0.0 || !Double.isFinite(dryMassKg)) {
            throw new IllegalArgumentException("Engine dry mass must be finite and non-negative");
        }
        if (!(thrustNewtons > 0.0) || !Double.isFinite(thrustNewtons)) {
            throw new IllegalArgumentException("Thrust must be finite and positive");
        }
        if (!(specificImpulseSeconds > 0.0) || !Double.isFinite(specificImpulseSeconds)) {
            throw new IllegalArgumentException("Specific impulse must be finite and positive");
        }
        if (throttle < 0.0 || throttle > 1.0 || !Double.isFinite(throttle)) {
            throw new IllegalArgumentException("Throttle must be between zero and one");
        }
        if (gimbalDegrees < 0.0 || gimbalDegrees > 90.0 || !Double.isFinite(gimbalDegrees)) {
            throw new IllegalArgumentException("Gimbal range must be between zero and ninety degrees");
        }
    }

    public double activeThrustNewtons() {
        return enabled ? thrustNewtons * throttle : 0.0;
    }

    public double exhaustVelocityMetersPerSecond() {
        return PropulsionMath.exhaustVelocityFromSpecificImpulse(specificImpulseSeconds);
    }

    public double propellantFlowKgPerSecond() {
        return activeThrustNewtons() / exhaustVelocityMetersPerSecond();
    }

    private static void requireName(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " id must not be blank");
        }
    }
}
