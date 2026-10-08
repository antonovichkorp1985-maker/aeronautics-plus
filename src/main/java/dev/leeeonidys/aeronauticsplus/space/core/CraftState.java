package dev.leeeonidys.aeronauticsplus.space.core;

/** Minimal logical spacecraft state; physical block compilation is a later layer. */
public record CraftState(String id, double dryMassKg, double propellantMassKg, OrbitState orbit) {
    public CraftState {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Craft id must not be blank");
        }
        if (!(dryMassKg > 0.0) || !Double.isFinite(dryMassKg)) {
            throw new IllegalArgumentException("Dry mass must be finite and positive");
        }
        if (propellantMassKg < 0.0 || !Double.isFinite(propellantMassKg)) {
            throw new IllegalArgumentException("Propellant mass must be finite and non-negative");
        }
        if (orbit == null) {
            throw new IllegalArgumentException("Craft orbit must not be null");
        }
    }

    public double totalMassKg() {
        return dryMassKg + propellantMassKg;
    }

    public CraftState consumePropellant(double amountKg) {
        if (amountKg < 0.0 || !Double.isFinite(amountKg) || amountKg > propellantMassKg) {
            throw new IllegalArgumentException("Propellant consumption exceeds available mass");
        }
        return new CraftState(id, dryMassKg, propellantMassKg - amountKg, orbit);
    }
}
