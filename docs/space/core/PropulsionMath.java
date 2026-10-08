package dev.leeeonidys.aeronauticsplus.space.core;

/** Small, deterministic propulsion equations used before engine/stage integration. */
public final class PropulsionMath {
    private PropulsionMath() {
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
        return specificImpulseSeconds * 9.80665;
    }
}
