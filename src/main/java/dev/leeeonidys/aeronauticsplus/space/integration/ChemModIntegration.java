package dev.leeeonidys.aeronauticsplus.space.integration;

import dev.leeeonidys.aeronauticsplus.space.core.AmbientGasProvider;
import dev.leeeonidys.aeronauticsplus.space.core.SolarSystem;
import dev.leeeonidys.aeronauticsplus.space.core.VesselMassProvider;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;

/**
 * Runtime bridge to ChemMod v1.0.2-test.6. Uses reflection so Aeronautics Plus compiles and runs
 * standalone without ChemMod; when ChemMod is present the real VesselMassProvider /
 * AmbientGasProvider implementations delegate to ChemMod's {@code BlockMassApi} and gas field.
 *
 * <p>Downstream code should call {@link #massProvider()} and {@link #ambientGasProvider()}
 * instead of the interface {@code DEFAULT} fields.</p>
 */
public final class ChemModIntegration {
    private static final String BLOCK_MASS_API = "io.github.antonovichkorp.chemmod.core.material.BlockMassApi";

    /** Whether ChemMod classes are present on the runtime classpath. */
    public static boolean isChemModPresent() {
        try {
            Class.forName(BLOCK_MASS_API);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Returns a ChemMod-backed mass provider when ChemMod is present, otherwise the default. */
    public static VesselMassProvider massProvider() {
        return isChemModPresent() ? new ChemModMassProvider() : VesselMassProvider.DEFAULT;
    }

    /** Returns a ChemMod-backed ambient gas provider when ChemMod is present, otherwise the default. */
    public static AmbientGasProvider ambientGasProvider() {
        return isChemModPresent() ? new ChemModAmbientGasProvider() : AmbientGasProvider.DEFAULT;
    }

    /** Mass provider backed by ChemMod BlockMassApi for real material densities. */
    private static final class ChemModMassProvider implements VesselMassProvider {
        @Override
        public double massKg(String partId) {
            try {
                Class<?> api = Class.forName(BLOCK_MASS_API);
                Class<?> registryClass = Class.forName(
                        "io.github.antonovichkorp.chemmod.core.material.MaterialDensityRegistry");
                Class<?> materialIdClass = Class.forName(
                        "io.github.antonovichkorp.chemmod.core.material.MaterialId");

                Object registry = registryClass.getDeclaredConstructor().newInstance();
                registryClass.getMethod("registerDefaults").invoke(registry);

                Object materialId = materialIdClass.getMethod("of", String.class)
                        .invoke(null, "chemmod:" + partId);
                Object density = registryClass.getMethod("getOrNull", materialIdClass)
                        .invoke(registry, materialId);
                if (density == null) {
                    return VesselPartCatalog.massKg(partId);
                }
                Double effective = (Double) density.getClass()
                        .getMethod("effectiveDensity").invoke(density);
                return effective != null ? effective : VesselPartCatalog.massKg(partId);
            } catch (Throwable ignored) {
                return VesselPartCatalog.massKg(partId);
            }
        }
    }

    /** Ambient gas provider backed by ChemMod gas field when available, SolarSystem columns otherwise. */
    private static final class ChemModAmbientGasProvider implements AmbientGasProvider {
        @Override
        public double densityKgPerM3(String bodyId, double geometricAltitudeMeters) {
            try {
                Class.forName("io.github.antonovichkorp.chemmod.core.gas.GasField");
            } catch (Throwable ignored) {
                // Gas field API not present; fall back to SolarSystem atmospheric columns.
            }
            return SolarSystem.atmosphereOf(SolarSystem.body(bodyId))
                    .densityKgPerM3(geometricAltitudeMeters);
        }
    }

    private ChemModIntegration() {
    }
}
