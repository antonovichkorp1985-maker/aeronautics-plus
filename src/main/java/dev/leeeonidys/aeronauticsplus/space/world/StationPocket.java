package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.core.StationHandoff;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * Void pocket where a parked habitat stack is blocks you can crawl.
 * Not the Moon-as-planet world. Not Overworld Y.
 */
public final class StationPocket {
    public static final ResourceKey<Level> LEVEL = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "station"));
    public static final ResourceKey<DimensionType> TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "station"));

    private StationPocket() {
    }

    public static boolean isStation(Level level) {
        return level != null && level.dimension().equals(LEVEL);
    }

    public static ServerLevel get(MinecraftServer server) {
        if (server == null) {
            return null;
        }
        return server.getLevel(LEVEL);
    }

    public static BlockPos islandOrigin(int index) {
        return new BlockPos(StationHandoff.islandX(index), StationHandoff.ISLAND_Y, 0);
    }
}
