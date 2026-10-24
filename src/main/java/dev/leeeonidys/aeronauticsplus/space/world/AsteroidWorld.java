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
 * Asteroid belt dimension. Floating rock islands in void — walkable, mineable,
 * and hazardous to vessels. Not a ChemMod import, not n-body.
 */
public final class AsteroidWorld {
    public static final ResourceKey<Level> LEVEL = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "asteroid_belt"));
    public static final ResourceKey<DimensionType> TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "asteroid_belt"));

    private AsteroidWorld() {
    }

    public static boolean isBelt(Level level) {
        return level != null && level.dimension().equals(LEVEL);
    }

    public static ServerLevel get(MinecraftServer server) {
        if (server == null) {
            return null;
        }
        return server.getLevel(LEVEL);
    }
}
