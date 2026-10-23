package dev.leeeonidys.aeronauticsplus.space.core;

/** Central body for the first two-body orbital model. */
public record CelestialBody(
        String id,
        double gravitationalParameter,
        double radiusMeters,
        double siderealAngularVelocityRadiansPerSecond) {
    /** Earth sidereal rotation; pole is +Y, east is r × Ŷ. */
    public static final double EARTH_SIDEREAL_RADIANS_PER_SECOND = 7.292115e-5;

    public CelestialBody {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Celestial body id must not be blank");
        }
        if (!(gravitationalParameter > 0.0) || !Double.isFinite(gravitationalParameter)) {
            throw new IllegalArgumentException("Gravitational parameter must be finite and positive");
        }
        if (!(radiusMeters > 0.0) || !Double.isFinite(radiusMeters)) {
            throw new IllegalArgumentException("Radius must be finite and positive");
        }
        if (siderealAngularVelocityRadiansPerSecond < 0.0
                || !Double.isFinite(siderealAngularVelocityRadiansPerSecond)) {
            throw new IllegalArgumentException("Sidereal spin must be finite and non-negative");
        }
    }

    public CelestialBody(String id, double gravitationalParameter, double radiusMeters) {
        this(id, gravitationalParameter, radiusMeters, 0.0);
    }

    public double surfaceGravityMetersPerSecond2() {
        return gravitationalParameter / (radiusMeters * radiusMeters);
    }

    /** Inertial surface velocity from sidereal spin. Zero at the poles. */
    public Vector3d surfaceVelocityMetersPerSecond(Vector3d positionMeters) {
        if (positionMeters == null) {
            throw new IllegalArgumentException("Position is required");
        }
        if (siderealAngularVelocityRadiansPerSecond == 0.0) {
            return Vector3d.ZERO;
        }
        return positionMeters.cross(Vector3d.UNIT_Y).multiply(siderealAngularVelocityRadiansPerSecond);
    }
}
