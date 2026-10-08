package dev.leeeonidys.aeronauticsplus.space.core;

/** Logical engine with a stage-local position and installed thrust axis. */
public record EngineState(
        String id,
        String fuelId,
        String oxidizerId,
        double dryMassKg,
        double thrustNewtons,
        double specificImpulseSeconds,
        double throttle,
        double gimbalDegrees,
        boolean enabled,
        Vector3d localPositionMeters,
        Vector3d thrustAxis) {
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
        if (localPositionMeters == null) {
            throw new IllegalArgumentException("Engine position must not be null");
        }
        if (thrustAxis == null || !(thrustAxis.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Engine thrust axis must be finite and non-zero");
        }
        thrustAxis = thrustAxis.normalized();
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

    public Vector3d activeThrustVectorNewtons() {
        return thrustAxis.multiply(activeThrustNewtons());
    }

    private static void requireName(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " id must not be blank");
        }
    }
}
