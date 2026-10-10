package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Temporary stand-in for ambient density/pressure.
 * World gas (the medium of the world) belongs to ChemMod — not tank contents
 * and not an AP atmosphere. Replace this when ChemMod exposes the medium.
 */
public record Atmosphere(
        String id,
        double surfaceDensityKgPerM3,
        double scaleHeightMeters,
        double surfacePressurePascals) {
    public Atmosphere {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Atmosphere id must not be blank");
        }
        if (!(surfaceDensityKgPerM3 > 0.0) || !Double.isFinite(surfaceDensityKgPerM3)) {
            throw new IllegalArgumentException("Surface density must be finite and positive");
        }
        if (!(scaleHeightMeters > 0.0) || !Double.isFinite(scaleHeightMeters)) {
            throw new IllegalArgumentException("Scale height must be finite and positive");
        }
        if (!(surfacePressurePascals > 0.0) || !Double.isFinite(surfacePressurePascals)) {
            throw new IllegalArgumentException("Surface pressure must be finite and positive");
        }
    }

    public static Atmosphere earth() {
        return new Atmosphere("earth", 1.225, 8_500.0, PropulsionMath.SEA_LEVEL_PRESSURE_PASCALS);
    }

    public double densityKgPerM3(double geometricAltitudeMeters) {
        double altitude = Math.max(0.0, geometricAltitudeMeters);
        return surfaceDensityKgPerM3 * Math.exp(-altitude / scaleHeightMeters);
    }

    public double pressurePascals(double geometricAltitudeMeters) {
        double altitude = Math.max(0.0, geometricAltitudeMeters);
        return surfacePressurePascals * Math.exp(-altitude / scaleHeightMeters);
    }

    public double densityAt(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        return densityKgPerM3(orbit.altitudeMeters());
    }

    public double pressureAt(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        return pressurePascals(orbit.altitudeMeters());
    }

    public double dynamicPressurePascals(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        return Aerodynamics.dynamicPressurePascals(
                densityAt(orbit), orbit.velocityMetersPerSecond().magnitude());
    }
}
