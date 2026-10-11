package dev.leeeonidys.aeronauticsplus.compat;

import dev.leeeonidys.aeronauticsplus.space.core.Atmosphere;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitState;
import net.neoforged.fml.ModList;

/**
 * Optional ChemMod air column. No ChemMod class is imported — reflection only
 * when {@code chemmod} is loaded and {@code ChemAtmosphere} exists.
 * Missing class (test.10 tank, no column) falls back to AP ISA.
 */
public final class ChemAtmosphereHooks {
    private static final String CLASS = "io.github.antonovichkorp.chemmod.gas.ChemAtmosphere";

    private ChemAtmosphereHooks() {
    }

    public static boolean columnPresent() {
        if (!ModList.get().isLoaded(CompatibilityManager.CHEMMOD_MOD_ID)) {
            return false;
        }
        try {
            Class.forName(CLASS);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static Atmosphere ambient() {
        return Atmosphere.earth();
    }

    public static double pressurePascals(double geometricAltitudeMetres) {
        Double sampled = invoke("pressurePascalsAtAltitudeMetres", geometricAltitudeMetres);
        return sampled != null ? sampled : Atmosphere.earth().pressurePascals(geometricAltitudeMetres);
    }

    public static double densityKgPerM3(double geometricAltitudeMetres) {
        Double sampled = invoke("densityKilogramsPerCubicMetreAtAltitudeMetres", geometricAltitudeMetres);
        return sampled != null ? sampled : Atmosphere.earth().densityKgPerM3(geometricAltitudeMetres);
    }

    public static double temperatureKelvin(double geometricAltitudeMetres) {
        Double sampled = invoke("temperatureKelvinAtAltitudeMetres", geometricAltitudeMetres);
        return sampled != null ? sampled : Atmosphere.earth().temperatureKelvin(geometricAltitudeMetres);
    }

    public static double dynamicPressurePascals(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        double density = densityKgPerM3(Math.max(0.0, orbit.altitudeMeters()));
        return 0.5 * density * orbit.velocityMetersPerSecond().magnitudeSquared();
    }

    private static Double invoke(String method, double altitudeMetres) {
        if (!columnPresent()) {
            return null;
        }
        try {
            Object value = Class.forName(CLASS)
                    .getMethod(method, double.class)
                    .invoke(null, altitudeMetres);
            if (value instanceof Number number
                    && Double.isFinite(number.doubleValue())
                    && number.doubleValue() >= 0.0) {
                return number.doubleValue();
            }
        } catch (ReflectiveOperationException ignored) {
            // ChemMod present but this build has no column edge
        }
        return null;
    }
}
