package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Earth–Moon patched conics. Two-body, one SOI at a time. Not n-body, not a Moon world.
 */
public final class PatchedConics {
    /** Mean Earth–Moon distance. */
    public static final double MOON_SEMI_MAJOR_METERS = 384_400_000.0;

    private PatchedConics() {
    }

    /** Laplace SOI ≈ a (m/M)^{2/5}. */
    public static double sphereOfInfluenceMeters(CelestialBody primary, CelestialBody secondary, double semiMajorMeters) {
        if (primary == null || secondary == null) {
            throw new IllegalArgumentException("Bodies are required");
        }
        if (!(semiMajorMeters > 0.0) || !Double.isFinite(semiMajorMeters)) {
            throw new IllegalArgumentException("Semi-major axis must be finite and positive");
        }
        double ratio = secondary.gravitationalParameter() / primary.gravitationalParameter();
        return semiMajorMeters * Math.pow(ratio, 0.4);
    }

    public static double moonSphereOfInfluenceMeters() {
        return sphereOfInfluenceMeters(SolarSystem.earth(), SolarSystem.moon(), MOON_SEMI_MAJOR_METERS);
    }

    public static Vector3d moonPositionInertial(double epochSeconds) {
        return secondaryPosition(SolarSystem.earth(), MOON_SEMI_MAJOR_METERS, epochSeconds);
    }

    public static Vector3d moonVelocityInertial(double epochSeconds) {
        return secondaryVelocity(SolarSystem.earth(), MOON_SEMI_MAJOR_METERS, epochSeconds);
    }

    public static Vector3d secondaryPosition(CelestialBody primary, double semiMajorMeters, double epochSeconds) {
        double n = meanMotion(primary, semiMajorMeters);
        double theta = n * epochSeconds;
        return new Vector3d(semiMajorMeters * Math.cos(theta), 0.0, semiMajorMeters * Math.sin(theta));
    }

    public static Vector3d secondaryVelocity(CelestialBody primary, double semiMajorMeters, double epochSeconds) {
        double n = meanMotion(primary, semiMajorMeters);
        double theta = n * epochSeconds;
        double speed = n * semiMajorMeters;
        return new Vector3d(-speed * Math.sin(theta), 0.0, speed * Math.cos(theta));
    }

    /**
     * Switch Earth ↔ Moon when the vessel crosses the Moon SOI.
     * Other bodies are left as-is.
     */
    public static OrbitState retarget(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        String id = orbit.centralBody().id();
        double soi = moonSphereOfInfluenceMeters();
        double epoch = orbit.epochSeconds();
        Vector3d moonR = moonPositionInertial(epoch);
        Vector3d moonV = moonVelocityInertial(epoch);
        if ("earth".equals(id)) {
            Vector3d rel = orbit.positionMeters().subtract(moonR);
            double range = rel.magnitude();
            if (range < soi && range > SolarSystem.moon().radiusMeters()) {
                return new OrbitState(
                        SolarSystem.moon(),
                        rel,
                        orbit.velocityMetersPerSecond().subtract(moonV),
                        epoch);
            }
            return orbit;
        }
        if ("moon".equals(id)) {
            if (orbit.positionMeters().magnitude() > soi) {
                return new OrbitState(
                        SolarSystem.earth(),
                        orbit.positionMeters().add(moonR),
                        orbit.velocityMetersPerSecond().add(moonV),
                        epoch);
            }
            return orbit;
        }
        return orbit;
    }

    private static double meanMotion(CelestialBody primary, double semiMajorMeters) {
        return Math.sqrt(primary.gravitationalParameter()
                / (semiMajorMeters * semiMajorMeters * semiMajorMeters));
    }
}
