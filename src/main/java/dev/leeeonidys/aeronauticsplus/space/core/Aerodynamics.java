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

    /** Wing section chord (1 m cell). */
    public static final double WING_CHORD_METERS = 1.0;
    /** Wing section span (1 m cell). */
    public static final double WING_SPAN_METERS = 1.0;
    /** Wing section area (1 m² per cell). */
    public static final double WING_AREA_M2 = WING_CHORD_METERS * WING_SPAN_METERS;
    /** Maximum elevon deflection in radians (±25°). */
    public static final double MAX_ELEVON_DEFLECTION_RAD = Math.toRadians(25.0);
    /** Lift curve slope for 2D airfoil (2π per radian). */
    private static final double CL_ALPHA_2D = 2.0 * Math.PI;
    /** Stall angle in radians (~15°). */
    public static final double STALL_ANGLE_RAD = Math.toRadians(15.0);
    /** Post-stall lift drop factor. */
    private static final double STALL_LIFT_FACTOR = 0.25;
    /** Induced drag efficiency factor (Oswald). */
    private static final double OSWALD_E = 0.85;
    /** Elevon control effectiveness (roll moment per radian per m²). */
    private static final double ELEVON_ROLL_EFFECTIVENESS = 0.15;
    /** Elevon pitch effectiveness. */
    private static final double ELEVON_PITCH_EFFECTIVENESS = 0.12;
    /** Tip vortex reduction factor when wing_tip present. */
    private static final double TIP_VORTEX_REDUCTION = 0.85;

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

    /** Lift coefficient from AoA, aspect ratio, elevon deflection and tip presence. */
    public static double wingClAlpha(double aoaRadians, double aspectRatio, double elevonDeflectionRad,
                                     boolean hasTip) {
        if (!Double.isFinite(aoaRadians) || aspectRatio <= 0.0 || !Double.isFinite(elevonDeflectionRad)) {
            throw new IllegalArgumentException("Wing lift inputs must be finite; aspect ratio must be positive");
        }
        double clAlpha = CL_ALPHA_2D * aspectRatio / (aspectRatio + 2.0);
        double stall = Math.min(1.0, Math.abs(aoaRadians) / STALL_ANGLE_RAD);
        double lift = clAlpha * Math.sin(aoaRadians);
        if (stall > 1.0) {
            lift *= STALL_LIFT_FACTOR + (1.0 - STALL_LIFT_FACTOR) / stall;
        }
        lift += Math.sin(elevonDeflectionRad) * 0.4 * clAlpha;
        return Math.max(-1.8, Math.min(1.8, lift));
    }

    /** Induced drag coefficient: Cl² / (π · AR · e), reduced by wing_tip. */
    public static double wingInducedCd(double cl, double aspectRatio, boolean hasTip) {
        if (!Double.isFinite(cl) || aspectRatio <= 0.0 || !Double.isFinite(cl)) {
            throw new IllegalArgumentException("Induced drag inputs must be finite; aspect ratio positive");
        }
        double e = OSWALD_E * (hasTip ? TIP_VORTEX_REDUCTION : 1.0);
        return cl * cl / (Math.PI * aspectRatio * e);
    }

    /** Profile drag coefficient (skin friction + form), roughly constant for thin wing. */
    public static double wingProfileCd() {
        return 0.012;
    }

    /** Total drag coefficient: induced + profile. */
    public static double wingCd(double cl, double aspectRatio, boolean hasTip) {
        return wingInducedCd(cl, aspectRatio, hasTip) + wingProfileCd();
    }

    /** Rolling moment from elevon differential deflection, N·m per wing cell. */
    public static double elevonRollMomentNm(double deflectionRad, double spanMeters, double dynamicPressurePa) {
        if (!Double.isFinite(deflectionRad) || spanMeters <= 0.0 || dynamicPressurePa < 0.0) {
            throw new IllegalArgumentException("Elevon roll inputs must be finite; span positive, q non-negative");
        }
        return deflectionRad * ELEVON_ROLL_EFFECTIVENESS * dynamicPressurePa * spanMeters * spanMeters;
    }

    /** Pitching moment from elevon deflection, N·m per wing cell. */
    public static double elevonPitchMomentNm(double deflectionRad, double chordMeters, double dynamicPressurePa) {
        if (!Double.isFinite(deflectionRad) || chordMeters <= 0.0 || dynamicPressurePa < 0.0) {
            throw new IllegalArgumentException("Elevon pitch inputs must be finite; chord positive, q non-negative");
        }
        return deflectionRad * ELEVON_PITCH_EFFECTIVENESS * dynamicPressurePa * chordMeters * chordMeters;
    }

    /** Topological span factor: how many consecutive intact cells remain from root to tip. */
    public static double wingSpanFactor(int intactCells, int totalCells) {
        if (intactCells < 0 || totalCells <= 0 || intactCells > totalCells) {
            throw new IllegalArgumentException("Span cells must satisfy 0 <= intact <= total");
        }
        return (double) intactCells / (double) totalCells;
    }

    private static double lerp(double x0, double y0, double x1, double y1, double x) {
        double t = (x - x0) / (x1 - x0);
        return y0 + t * (y1 - y0);
    }
}
