package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Continuum drag given a density. Density itself comes from ChemMod world gas
 * later; until then {@link Atmosphere} is a stand-in. Cd ~ 0.5 is order of magnitude.
 */
public final class Aerodynamics {
    public static final double ROCKET_CD = 0.5;

    /** Fairing drop after leaving dense air: ~80–110 km historically, q already small. */
    public static final double FAIRING_MIN_ALTITUDE_METERS = 50_000.0;
    public static final double FAIRING_MAX_DYNAMIC_PRESSURE_PASCALS = 250.0;

    private Aerodynamics() {
    }

    public static double dynamicPressurePascals(double densityKgPerM3, double speedMetersPerSecond) {
        if (densityKgPerM3 < 0.0 || !Double.isFinite(densityKgPerM3)
                || speedMetersPerSecond < 0.0 || !Double.isFinite(speedMetersPerSecond)) {
            throw new IllegalArgumentException("Dynamic pressure inputs must be finite and non-negative");
        }
        return 0.5 * densityKgPerM3 * speedMetersPerSecond * speedMetersPerSecond;
    }

    public static Vector3d dragForceNewtons(
            double densityKgPerM3, Vector3d velocityMetersPerSecond, double dragCoefficient, double areaM2) {
        if (velocityMetersPerSecond == null) {
            throw new IllegalArgumentException("Velocity is required");
        }
        if (densityKgPerM3 < 0.0 || !Double.isFinite(densityKgPerM3)
                || dragCoefficient < 0.0 || !Double.isFinite(dragCoefficient)
                || areaM2 < 0.0 || !Double.isFinite(areaM2)) {
            throw new IllegalArgumentException("Drag inputs must be finite and non-negative");
        }
        double speed = velocityMetersPerSecond.magnitude();
        if (!(speed > 1.0e-12) || !(densityKgPerM3 > 0.0) || !(dragCoefficient > 0.0) || !(areaM2 > 0.0)) {
            return Vector3d.ZERO;
        }
        double magnitude = 0.5 * densityKgPerM3 * speed * speed * dragCoefficient * areaM2;
        return velocityMetersPerSecond.multiply(-magnitude / speed);
    }

    public static boolean fairingSafe(OrbitState orbit, Atmosphere atmosphere) {
        if (orbit == null || atmosphere == null) {
            throw new IllegalArgumentException("Orbit and atmosphere are required");
        }
        return orbit.altitudeMeters() >= FAIRING_MIN_ALTITUDE_METERS
                && atmosphere.dynamicPressurePascals(orbit) <= FAIRING_MAX_DYNAMIC_PRESSURE_PASCALS;
    }
}
