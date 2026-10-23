package dev.leeeonidys.aeronauticsplus.space.core;

/** An ideal impulsive burn in the current inertial frame. */
public record Maneuver(double epochSeconds, Vector3d deltaVelocityMetersPerSecond, double propellantMassKg) {
    public Maneuver {
        if (!Double.isFinite(epochSeconds) || deltaVelocityMetersPerSecond == null) {
            throw new IllegalArgumentException("Maneuver fields are invalid");
        }
        if (propellantMassKg < 0.0 || !Double.isFinite(propellantMassKg)) {
            throw new IllegalArgumentException("Propellant mass must be finite and non-negative");
        }
    }
}
