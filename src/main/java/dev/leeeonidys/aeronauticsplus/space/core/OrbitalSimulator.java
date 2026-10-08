package dev.leeeonidys.aeronauticsplus.space.core;

/** Deterministic fixed-step two-body propagator for the first space-core slice. */
public final class OrbitalSimulator {
    private OrbitalSimulator() {
    }

    public static OrbitState propagate(OrbitState initial, double durationSeconds, double stepSeconds) {
        if (durationSeconds < 0.0 || !Double.isFinite(durationSeconds)) {
            throw new IllegalArgumentException("Duration must be finite and non-negative");
        }
        if (!(stepSeconds > 0.0) || !Double.isFinite(stepSeconds)) {
            throw new IllegalArgumentException("Step must be finite and positive");
        }

        OrbitState state = initial;
        double remaining = durationSeconds;
        while (remaining > 0.0) {
            double dt = Math.min(stepSeconds, remaining);
            state = velocityVerlet(state, dt);
            remaining -= dt;
        }
        return state;
    }

    public static OrbitState applyImpulse(OrbitState state, Maneuver maneuver) {
        if (Math.abs(state.epochSeconds() - maneuver.epochSeconds()) > 1.0e-9) {
            throw new IllegalArgumentException("Maneuver epoch does not match orbit state epoch");
        }
        return new OrbitState(
                state.centralBody(),
                state.positionMeters(),
                state.velocityMetersPerSecond().add(maneuver.deltaVelocityMetersPerSecond()),
                state.epochSeconds());
    }

    public static Vector3d gravitationalAcceleration(CelestialBody body, Vector3d positionMeters) {
        double radiusSquared = positionMeters.magnitudeSquared();
        if (!(radiusSquared > 0.0) || !Double.isFinite(radiusSquared)) {
            throw new IllegalArgumentException("Position must be finite and non-zero");
        }
        double factor = -body.gravitationalParameter() / (radiusSquared * Math.sqrt(radiusSquared));
        return positionMeters.multiply(factor);
    }

    private static OrbitState velocityVerlet(OrbitState state, double dt) {
        Vector3d acceleration = gravitationalAcceleration(state.centralBody(), state.positionMeters());
        Vector3d nextPosition = state.positionMeters()
                .add(state.velocityMetersPerSecond().multiply(dt))
                .add(acceleration.multiply(0.5 * dt * dt));
        Vector3d nextAcceleration = gravitationalAcceleration(state.centralBody(), nextPosition);
        Vector3d nextVelocity = state.velocityMetersPerSecond()
                .add(acceleration.add(nextAcceleration).multiply(0.5 * dt));
        return new OrbitState(state.centralBody(), nextPosition, nextVelocity, state.epochSeconds() + dt);
    }
}
