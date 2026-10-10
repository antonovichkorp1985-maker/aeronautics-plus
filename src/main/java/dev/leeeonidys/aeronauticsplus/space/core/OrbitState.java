package dev.leeeonidys.aeronauticsplus.space.core;

/** Position and velocity in a body-centred inertial frame. */
public record OrbitState(CelestialBody centralBody, Vector3d positionMeters, Vector3d velocityMetersPerSecond, double epochSeconds) {
    public OrbitState {
        if (centralBody == null || positionMeters == null || velocityMetersPerSecond == null) {
            throw new IllegalArgumentException("Orbit state fields must not be null");
        }
        if (!Double.isFinite(epochSeconds)) {
            throw new IllegalArgumentException("Epoch must be finite");
        }
    }

    public double specificOrbitalEnergy() {
        return 0.5 * velocityMetersPerSecond().magnitudeSquared()
                - centralBody.gravitationalParameter() / positionMeters().magnitude();
    }

    /** Geometric altitude above the spherical body. Negative means inside the radius. */
    public double altitudeMeters() {
        return positionMeters.magnitude() - centralBody.radiusMeters();
    }

    public Vector3d radialUnit() {
        return positionMeters.normalized();
    }

    public double radialSpeedMetersPerSecond() {
        return velocityMetersPerSecond.dot(radialUnit());
    }

    /** Speed tangent to the local horizon. Zero on a purely vertical hop. */
    public double horizontalSpeedMetersPerSecond() {
        Vector3d radial = radialUnit();
        return velocityMetersPerSecond.subtract(radial.multiply(velocityMetersPerSecond.dot(radial))).magnitude();
    }

    /** 0 = local horizon, π/2 = straight up. */
    public double flightPathAngleRadians() {
        double horizontal = horizontalSpeedMetersPerSecond();
        if (!(horizontal > 1.0e-12)) {
            return velocityMetersPerSecond.dot(radialUnit()) >= 0.0 ? Math.PI / 2.0 : -Math.PI / 2.0;
        }
        return Math.atan2(radialSpeedMetersPerSecond(), horizontal);
    }
}
