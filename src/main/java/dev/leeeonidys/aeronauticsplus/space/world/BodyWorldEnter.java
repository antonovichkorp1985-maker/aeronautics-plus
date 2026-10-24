package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Creative TP onto a walkable body. Not N, not {@code /ap state} orbit.
 */
public final class BodyWorldEnter {
    private BodyWorldEnter() {
    }

    public static String go(ServerPlayer player, String bodyId) {
        if (player == null || player.level().isClientSide) {
            return "";
        }
        if (!BodyWorlds.hasWorld(bodyId)) {
            return "Нет поверхности для " + bodyId + ". Орбита: /ap state. Станция: N или /ap station.";
        }
        ResourceKey<Level> key = BodyWorlds.worldOf(bodyId);
        ServerLevel dest = player.server.getLevel(key);
        if (dest == null) {
            return "Измерение " + bodyId + " не загружено. Зайди в мир ещё раз.";
        }
        if (StationPocket.isStation(player.level())) {
            StationEnter.leave(player);
        }
        double x;
        double y;
        double z;
        if (Level.OVERWORLD.equals(key)) {
            BlockPos spawn = dest.getSharedSpawnPos();
            x = spawn.getX() + 0.5;
            y = spawn.getY();
            z = spawn.getZ() + 0.5;
        } else {
            int surface = findSurface(dest, 0, 0);
            if (surface <= dest.getMinBuildHeight() + 1) {
                // The belt is void all the way down now, so an empty arrival column means a fall
                // into the void. Rock under the arrival point is cheaper than a death screen.
                surface = landingRock(dest, 0, 0);
            }
            x = 0.5;
            y = surface + 1.0;
            z = 0.5;
        }
        player.teleportTo(
                dest,
                x,
                y,
                z,
                java.util.Set.<RelativeMovement>of(),
                player.getYRot(),
                player.getXRot());
        if ("moon".equals(bodyId)) {
            return "Луна. Пыль AP, g лунная. Химия — ChemMod, когда будет. Станция на орбите — N.";
        }
        if ("mars".equals(bodyId)) {
            return "Марс. Пыль AP, g марсианская (0.38 g0). Воздух тонкий — ChemMod, когда будет.";
        }
        if ("ceres".equals(bodyId) || "vesta".equals(bodyId)
                || "pallas".equals(bodyId) || "hygiea".equals(bodyId)) {
            return "Астероид " + bodyId.toUpperCase() + ". Порода AP, вакуум. Берегись collisions с кораблём.";
        }
        return "Поверхность " + bodyId + ".";
    }

    /** First solid block under (x, z), scanning down from sky limit. */
    private static int findSurface(ServerLevel level, int x, int z) {
        int from = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        for (int y = Math.min(from, level.getMaxBuildHeight() - 1); y > level.getMinBuildHeight(); y--) {
            BlockState state = level.getBlockState(new BlockPos(x, y, z));
            if (!state.isAir()) {
                return y;
            }
        }
        return level.getMinBuildHeight() + 1;
    }

    /**
     * The asteroid belt has no bedrock floor any more: below the belt band it is open space.
     * When the arrival column is empty all the way down, put a 5×5 rock under the player and
     * stand them on it instead of letting the void eat them.
     */
    private static int landingRock(ServerLevel level, int x, int z) {
        int y = AsteroidChunkGenerator.BELT_MIN_Y + 32;
        BlockState rock = AeronauticsPlus.ASTEROID_ROCK.get().defaultBlockState();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                level.setBlockAndUpdate(new BlockPos(x + dx, y, z + dz), rock);
            }
        }
        return y;
    }
}
