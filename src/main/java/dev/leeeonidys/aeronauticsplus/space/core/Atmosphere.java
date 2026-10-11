package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Planetary air column: geometric altitude in metres above a sphere.
 * Same ISA ChemMod v1.0.0 publishes as {@code ChemAtmosphere} / {@code Atmosphere.EARTH}.
 * Not the leak field. Minecraft Y is not this axis.
 */
public record Atmosphere(
        String id,
        double surfacePressurePascals,
        double surfaceTemperatureKelvin,
        double lapseRateKelvinPerMetre,
        double molarMassKilogramsPerMole,
        double surfaceGravityMetresPerSecondSquared,
        double planetRadiusMetres,
        double tropopauseGeopotentialMetres) {
    /** Universal gas constant, J/(mol·K). Same value ChemMod uses. */
    public static final double GAS_CONSTANT = 8.314462618;

    public Atmosphere {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Atmosphere id must not be blank");
        }
        if (!(surfacePressurePascals > 0.0) || !Double.isFinite(surfacePressurePascals)) {
            throw new IllegalArgumentException("Surface pressure must be finite and positive");
        }
        if (!(surfaceTemperatureKelvin > 0.0) || !Double.isFinite(surfaceTemperatureKelvin)) {
            throw new IllegalArgumentException("Surface temperature must be finite and positive");
        }
        if (!(lapseRateKelvinPerMetre > 0.0) || !Double.isFinite(lapseRateKelvinPerMetre)) {
            throw new IllegalArgumentException("Lapse rate must be finite and positive");
        }
        if (!(molarMassKilogramsPerMole > 0.0) || !Double.isFinite(molarMassKilogramsPerMole)) {
            throw new IllegalArgumentException("Molar mass must be finite and positive");
        }
        if (!(planetRadiusMetres > 0.0) || !Double.isFinite(planetRadiusMetres)) {
            throw new IllegalArgumentException("Planet radius must be finite and positive");
        }
        if (!(tropopauseGeopotentialMetres > 0.0) || !Double.isFinite(tropopauseGeopotentialMetres)) {
            throw new IllegalArgumentException("Tropopause altitude must be finite and positive");
        }
    }

    /** Earth's ISA. Sea level 101325 Pa / 288.15 K / 1.225 kg/m³. */
    public static Atmosphere earth() {
        return new Atmosphere(
                "earth",
                PropulsionMath.SEA_LEVEL_PRESSURE_PASCALS,
                288.15,
                0.0065,
                0.0289647,
                9.80665,
                6_371_000.0,
                11_000.0);
    }

    /** Geometric altitude → geopotential, the axis the ISA is defined on. */
    public double geopotentialMetres(double geometricAltitudeMetres) {
        double altitude = Math.max(0.0, geometricAltitudeMetres);
        return planetRadiusMetres * altitude / (planetRadiusMetres + altitude);
    }

    public double temperatureKelvin(double geometricAltitudeMetres) {
        double geopotential = geopotentialMetres(geometricAltitudeMetres);
        if (geopotential <= tropopauseGeopotentialMetres) {
            return surfaceTemperatureKelvin - lapseRateKelvinPerMetre * geopotential;
        }
        return tropopauseTemperatureKelvin();
    }

    public double pressurePascals(double geometricAltitudeMetres) {
        double geopotential = geopotentialMetres(geometricAltitudeMetres);
        double exponent = surfaceGravityMetresPerSecondSquared * molarMassKilogramsPerMole
                / (GAS_CONSTANT * lapseRateKelvinPerMetre);
        if (geopotential <= tropopauseGeopotentialMetres) {
            double ratio = temperatureKelvin(geometricAltitudeMetres) / surfaceTemperatureKelvin;
            return surfacePressurePascals * Math.pow(ratio, exponent);
        }
        double tropopausePressure = surfacePressurePascals
                * Math.pow(tropopauseTemperatureKelvin() / surfaceTemperatureKelvin, exponent);
        return tropopausePressure * Math.exp(
                -surfaceGravityMetresPerSecondSquared * molarMassKilogramsPerMole
                        * (geopotential - tropopauseGeopotentialMetres)
                        / (GAS_CONSTANT * tropopauseTemperatureKelvin()));
    }

    public double densityKgPerM3(double geometricAltitudeMetres) {
        double pressure = pressurePascals(geometricAltitudeMetres);
        double temperature = temperatureKelvin(geometricAltitudeMetres);
        return pressure * molarMassKilogramsPerMole / (GAS_CONSTANT * temperature);
    }

    public double densityAt(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        return densityKgPerM3(Math.max(0.0, orbit.altitudeMeters()));
    }

    public double pressureAt(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        return pressurePascals(Math.max(0.0, orbit.altitudeMeters()));
    }

    public double dynamicPressurePascals(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        return Aerodynamics.dynamicPressurePascals(
                densityAt(orbit), orbit.velocityMetersPerSecond().magnitude());
    }

    private double tropopauseTemperatureKelvin() {
        return surfaceTemperatureKelvin - lapseRateKelvinPerMetre * tropopauseGeopotentialMetres;
    }
}
