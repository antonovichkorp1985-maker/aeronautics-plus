package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Provider of per-part dry masses. Defaults to the current VesselPartCatalog
 * values so a base vessel compiles with known masses. ChemMod may override
 * this to return real material densities per block.
 */
@FunctionalInterface
public interface VesselMassProvider {
    /** Default provider instance: delegates to VesselPartCatalog. */
    VesselMassProvider DEFAULT = new DefaultProvider();

    /** Dry mass of one block instance by its catalog id, kg. */
    double massKg(String partId);

    final class DefaultProvider implements VesselMassProvider {
        @Override
        public double massKg(String partId) {
            try {
                return (double) Class.forName(
                        "dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog")
                        .getMethod("massKg", String.class)
                        .invoke(null, partId);
            } catch (Throwable ignored) {
                return 0.0;
            }
        }
    }
}
