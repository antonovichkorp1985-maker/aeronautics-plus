package dev.leeeonidys.aeronauticsplus.space.core;

/** Small, deterministic propulsion equations used before engine/stage integration. */
public final class PropulsionMath {
    public static final double STANDARD_GRAVITY = 9.80665;
    public static final double SEA_LEVEL_PRESSURE_PASCALS = 101_325.0;

    private PropulsionMath() {
    }

    /**
     * Linear Isp(p) between sea level and vacuum. Mass flow is set by the throat,
     * so thrust scales with the same ratio.
     */
    public static double ispAtPressure(
            double vacuumIspSeconds, double seaLevelIspSeconds, double ambientPascals) {
        if (!(vacuumIspSeconds > 0.0) || !Double.isFinite(vacuumIspSeconds)
                || !(seaLevelIspSeconds > 0.0) || !Double.isFinite(seaLevelIspSeconds)) {
            throw new IllegalArgumentException("Isp values must be finite and positive");
        }
        if (seaLevelIspSeconds > vacuumIspSeconds + 1.0e-12) {
            throw new IllegalArgumentException("Sea-level Isp cannot exceed vacuum Isp");
        }
        if (!Double.isFinite(ambientPascals) || ambientPascals < 0.0) {
            throw new IllegalArgumentException("Ambient pressure must be finite and non-negative");
        }
        double fraction = ambientPascals / SEA_LEVEL_PRESSURE_PASCALS;
        if (fraction <= 0.0) {
            return vacuumIspSeconds;
        }
        if (fraction >= 1.0) {
            return seaLevelIspSeconds;
        }
        return vacuumIspSeconds + (seaLevelIspSeconds - vacuumIspSeconds) * fraction;
    }

    /** Tsiolkovsky ideal delta-v for a positive dry and wet mass. */
    public static double idealDeltaV(double exhaustVelocityMetersPerSecond,
                                     double dryMassKg,
                                     double propellantMassKg) {
        if (!(exhaustVelocityMetersPerSecond > 0.0) || !Double.isFinite(exhaustVelocityMetersPerSecond)) {
            throw new IllegalArgumentException("Exhaust velocity must be finite and positive");
        }
        if (!(dryMassKg > 0.0) || !Double.isFinite(dryMassKg)) {
            throw new IllegalArgumentException("Dry mass must be finite and positive");
        }
        if (propellantMassKg < 0.0 || !Double.isFinite(propellantMassKg)) {
            throw new IllegalArgumentException("Propellant mass must be finite and non-negative");
        }
        return exhaustVelocityMetersPerSecond * Math.log1p(propellantMassKg / dryMassKg);
    }

    public static double exhaustVelocityFromSpecificImpulse(double specificImpulseSeconds) {
        if (!(specificImpulseSeconds > 0.0) || !Double.isFinite(specificImpulseSeconds)) {
            throw new IllegalArgumentException("Specific impulse must be finite and positive");
        }
        return specificImpulseSeconds * STANDARD_GRAVITY;
    }
}
