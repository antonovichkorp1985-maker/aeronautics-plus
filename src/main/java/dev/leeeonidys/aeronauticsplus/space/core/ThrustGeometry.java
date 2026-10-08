package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Stage-local thrust and mass geometry.
 * Attitude is identity until a later orientation layer: the net thrust axis is also
 * the inertial burn direction for the current slice.
 */
public record ThrustGeometry(
        Vector3d centerOfMassMeters,
        Vector3d centerOfThrustMeters,
        Vector3d netThrustNewtons,
        Vector3d momentArmMeters,
        double perpendicularOffsetMeters) {
    public static final double OFFSET_WARNING_METERS = 0.25;

    public ThrustGeometry {
        if (centerOfMassMeters == null || centerOfThrustMeters == null
                || netThrustNewtons == null || momentArmMeters == null) {
            throw new IllegalArgumentException("Thrust geometry vectors must not be null");
        }
        if (perpendicularOffsetMeters < 0.0 || !Double.isFinite(perpendicularOffsetMeters)) {
            throw new IllegalArgumentException("Perpendicular offset must be finite and non-negative");
        }
    }

    public boolean hasNetThrust() {
        return netThrustNewtons.magnitudeSquared() > 0.0;
    }

    public boolean hasMaterialOffset() {
        return hasNetThrust() && perpendicularOffsetMeters > OFFSET_WARNING_METERS;
    }

    public Vector3d thrustDirection() {
        return netThrustNewtons.normalized();
    }
}
