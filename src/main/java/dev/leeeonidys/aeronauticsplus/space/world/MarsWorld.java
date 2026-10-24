package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.core.SolarSystem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * Walkable Mars. Thin air is ChemMod's catalogue entry ({@code SolarSystem.marsAir()}), not a
 * second atmosphere: this class only owns the dimension key and the surface gravity.
 */
public final class MarsWorld {
    public static final ResourceKey<Level> LEVEL = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "mars"));
    public static final ResourceKey<DimensionType> TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "mars"));

    /** g_mars / g0 from the same μ and R the orbit slice uses: 3.73 m/s², 0.38 g0. */
    public static final double GRAVITY_FACTOR =
            SolarSystem.mars().surfaceGravityMetersPerSecond2() / 9.80665;

    private MarsWorld() {
    }

    public static boolean isMars(Level level) {
        return level != null && level.dimension().equals(LEVEL);
    }

    public static ServerLevel get(MinecraftServer server) {
        if (server == null) {
            return null;
        }
        return server.getLevel(LEVEL);
    }
}
