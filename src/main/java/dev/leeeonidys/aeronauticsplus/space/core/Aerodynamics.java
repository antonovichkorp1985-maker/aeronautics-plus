package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Continuum drag. Density is the planetary column.
 * Cd is Hoerner-style rocket Cd(M), not a constant 0.5.
 */
public final class Aerodynamics {
    public static final double AIR_GAMMA = 1.4;
    /** Passed to dynamics to enable aero; the integrator then uses {@link #rocketCd(double)}. */
    public static final double ROCKET_CD = 0.5;

    /**
     * Slice fixture: a quiet 50 km coast. Drop itself is q, not this floor.
     * Not an automatic drop. Real shrouds usually go ~80–110 km, still on demand.
     */
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

    /**
     * Axial booster Cd(M): subsonic 0.20, transonic peak ~0.58 at M 1.05, hypersonic 0.18.
     * Hoerner / typical ballistic-missile curve, not a flat 0.5.
     */
    public static double rocketCd(double mach) {
        if (!Double.isFinite(mach) || mach < 0.0) {
            throw new IllegalArgumentException("Mach must be finite and non-negative");
        }
        if (mach < 0.8) {
            return 0.20;
        }
        if (mach < 1.05) {
            return lerp(0.8, 0.20, 1.05, 0.58, mach);
        }
        if (mach < 1.2) {
            return lerp(1.05, 0.58, 1.2, 0.50, mach);
        }
        if (mach < 5.0) {
            return lerp(1.2, 0.50, 5.0, 0.20, mach);
        }
        return 0.18;
    }

    public static double mach(Atmosphere atmosphere, OrbitState orbit) {
        if (atmosphere == null || orbit == null) {
            throw new IllegalArgumentException("Atmosphere and orbit are required");
        }
        double altitude = Math.max(0.0, orbit.altitudeMeters());
        double sound = atmosphere.speedOfSoundMetresPerSecond(altitude);
        if (!(sound > 1.0e-9)) {
            return 0.0;
        }
        return orbit.velocityMetersPerSecond().magnitude() / sound;
    }

    /**
     * Sutton–Graves cold-wall heat index (Rn = 1 m), W/m².
     * ChemMod still owns ablation and suit damage.
     */
    public static double stagnationWattsPerSquareMetre(double densityKgPerM3, double speedMetersPerSecond) {
        if (densityKgPerM3 < 0.0 || !Double.isFinite(densityKgPerM3)
                || speedMetersPerSecond < 0.0 || !Double.isFinite(speedMetersPerSecond)) {
            throw new IllegalArgumentException("Heat inputs must be finite and non-negative");
        }
        return 1.83e-4 * Math.sqrt(densityKgPerM3)
                * speedMetersPerSecond * speedMetersPerSecond * speedMetersPerSecond;
    }

    /**
     * When {@code dragCoefficient > 0} the force uses {@link #rocketCd(double)}.
     * Zero coefficient is a vacuum fixture (ThrottleSlice).
     */
    public static Vector3d columnDragNewtons(
            Atmosphere atmosphere, OrbitState orbit, double dragCoefficient, double areaM2) {
        if (atmosphere == null || orbit == null) {
            throw new IllegalArgumentException("Atmosphere and orbit are required");
        }
        if (!(dragCoefficient > 0.0)) {
            return Vector3d.ZERO;
        }
        double cd = rocketCd(mach(atmosphere, orbit));
        return dragForceNewtons(
                atmosphere.densityAt(orbit), orbit.velocityMetersPerSecond(), cd, areaM2);
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
        if (orbit.altitudeMeters() < 1.0) {
            return false;
        }
        return atmosphere.dynamicPressurePascals(orbit) <= FAIRING_MAX_DYNAMIC_PRESSURE_PASCALS;
    }

    private static double lerp(double x0, double y0, double x1, double y1, double x) {
        double t = (x - x0) / (x1 - x0);
        return y0 + t * (y1 - y0);
    }
}
