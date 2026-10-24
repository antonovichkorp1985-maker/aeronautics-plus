package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/**
 * Main belt annulus around the Sun. Named rocks are Kepler bodies.
 * Ore is ChemMod — this is where you would fly, not a vein table.
 */
public record AsteroidBelt(double innerMeters, double outerMeters, List<CelestialBody> named) {
    public AsteroidBelt {
        if (!(innerMeters > 0.0) || !(outerMeters > innerMeters)
                || !Double.isFinite(innerMeters) || !Double.isFinite(outerMeters)) {
            throw new IllegalArgumentException("Belt bounds must be finite, inner < outer");
        }
        named = List.copyOf(named == null ? List.of() : named);
    }

    public boolean containsRadius(double radiusMeters) {
        return Double.isFinite(radiusMeters) && radiusMeters >= innerMeters && radiusMeters <= outerMeters;
    }
}
