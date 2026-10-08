package dev.leeeonidys.aeronauticsplus.space.core;

/** Logical tank; geometry and material are added by the vessel compiler later. */
public record TankState(
        String id,
        double dryMassKg,
        double propellantCapacityKg,
        PropellantState contents) {
    public TankState {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Tank id must not be blank");
        }
        if (!(dryMassKg >= 0.0) || !Double.isFinite(dryMassKg)) {
            throw new IllegalArgumentException("Tank dry mass must be finite and non-negative");
        }
        if (!(propellantCapacityKg > 0.0) || !Double.isFinite(propellantCapacityKg)) {
            throw new IllegalArgumentException("Tank capacity must be finite and positive");
        }
        if (contents == null || contents.totalMassKg() > propellantCapacityKg + 1.0e-9) {
            throw new IllegalArgumentException("Tank contents exceed capacity");
        }
    }

    public double totalMassKg() {
        return dryMassKg + contents.totalMassKg();
    }

    public TankState consume(double fuelKg, double oxidizerKg) {
        return new TankState(id, dryMassKg, propellantCapacityKg, contents.consume(fuelKg, oxidizerKg));
    }
}
