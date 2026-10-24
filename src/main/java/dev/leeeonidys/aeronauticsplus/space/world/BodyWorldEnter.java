package dev.leeeonidys.aeronauticsplus.space.world;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
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
            int surface = dest.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, 0, 0);
            if (surface <= dest.getMinBuildHeight() + 1) {
                surface = MoonChunkGenerator.surfaceY(0, 0) + 1;
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
        return "Поверхность " + bodyId + ".";
    }
}
