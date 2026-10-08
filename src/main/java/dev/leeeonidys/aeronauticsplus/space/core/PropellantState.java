package dev.leeeonidys.aeronauticsplus.space.core;

/** Immutable fuel/oxidizer inventory for a tank or a stage. */
public record PropellantState(
        String fuelId,
        String oxidizerId,
        double fuelMassKg,
        double oxidizerMassKg) {
    public PropellantState {
        requireName(fuelId, "Fuel");
        requireName(oxidizerId, "Oxidizer");
        requireMass(fuelMassKg, "Fuel");
        requireMass(oxidizerMassKg, "Oxidizer");
    }

    public double totalMassKg() {
        return fuelMassKg + oxidizerMassKg;
    }

    public double mixtureRatio() {
        if (!(fuelMassKg > 0.0)) {
            return Double.POSITIVE_INFINITY;
        }
        return oxidizerMassKg / fuelMassKg;
    }

    public PropellantState consume(double fuelKg, double oxidizerKg) {
        if (fuelKg < 0.0 || oxidizerKg < 0.0 || fuelKg > fuelMassKg || oxidizerKg > oxidizerMassKg) {
            throw new IllegalArgumentException("Propellant consumption exceeds tank contents");
        }
        return new PropellantState(fuelId, oxidizerId, fuelMassKg - fuelKg, oxidizerMassKg - oxidizerKg);
    }

    private static void requireName(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " id must not be blank");
        }
    }

    private static void requireMass(double value, String label) {
        if (value < 0.0 || !Double.isFinite(value)) {
            throw new IllegalArgumentException(label + " mass must be finite and non-negative");
        }
    }
}
