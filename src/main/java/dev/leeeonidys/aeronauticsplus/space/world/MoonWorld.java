package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * Walkable Moon, Galacticraft-style. Not the station pocket, not ChemMod air.
 * Dust block is an AP id; composition stays ChemMod when they publish it.
 */
public final class MoonWorld {
    public static final ResourceKey<Level> LEVEL = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "moon"));
    public static final ResourceKey<DimensionType> TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "moon"));

    /** g_moon / g0. Same μ and R as {@code SolarSystem.moon()}. */
    public static final double GRAVITY_FACTOR = 1.62 / 9.80665;

    private MoonWorld() {
    }

    public static boolean isMoon(Level level) {
        return level != null && level.dimension().equals(LEVEL);
    }

    public static ServerLevel get(MinecraftServer server) {
        if (server == null) {
            return null;
        }
        return server.getLevel(LEVEL);
    }
}
