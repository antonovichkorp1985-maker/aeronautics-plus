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
}
