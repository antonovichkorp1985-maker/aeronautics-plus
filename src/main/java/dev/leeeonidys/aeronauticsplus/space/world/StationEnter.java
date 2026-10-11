package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.compile.StationPlan;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;

/**
 * Player command: crawl the parked habitat stack. Burns stay on the Kepler map.
 * CM↔LM in lunar orbit is this, not the Moon-as-planet world.
 */
public final class StationEnter {
    private StationEnter() {
    }

    public static String toggle(ServerPlayer player) {
        if (player == null) {
            return "Нет игрока.";
        }
        if (StationPocket.isStation(player.level())) {
            return leave(player);
        }
        return enter(player);
    }

    public static String enter(ServerPlayer player) {
        if (player == null || player.level().isClientSide) {
            return "";
        }
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return "Нет оверворлда.";
        }
        ServerLevel station = StationPocket.get(player.server);
        if (station == null) {
            return "Карман станции не загружен. Зайди в мир ещё раз (новое измерение).";
        }
        FlightLoop occupied = OrbitMapData.get(overworld).takeParkedHabitat();
        if (occupied == null) {
            return "Нет круговой стоянки с жилым. Манёвр остаётся на карте. Беспилотник в карман не входит.";
        }
        StationPlan plan = occupied.station();
        BlockPos returnPos = player.blockPosition();
        StationPocketData.Island island = StationPocketData.get(overworld)
                .add(occupied, returnPos, player.level().dimension());
        BlockPos origin = StationPocket.islandOrigin(island.index());
        WorldStation.place(station, origin, plan);
        BlockPos stand = WorldStation.stand(origin, plan);
        teleport(player, station, stand.getX() + 0.5, stand.getY(), stand.getZ() + 0.5);
        return "Карман станции. Переползание. Выход: клавиша N или ПКМ по жилому.";
    }

    public static String leave(ServerPlayer player) {
        if (player == null || player.level().isClientSide) {
            return "";
        }
        if (!StationPocket.isStation(player.level())) {
            return "Ты не в кармане станции.";
        }
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return "Нет оверворлда.";
        }
        StationPocketData data = StationPocketData.get(overworld);
        StationPocketData.Island island = data.nearest(player.blockPosition());
        if (island == null) {
            return "Рядом нет острова станции.";
        }
        ServerLevel station = StationPocket.get(player.server);
        if (station != null) {
            WorldStation.clear(station, StationPocket.islandOrigin(island.index()), data.plan(island));
        }
        data.remove(island);
        FlightLoop left = island.loop().leaveStation();
        OrbitMapData.get(overworld).enter(left);
        ServerLevel dest = player.server.getLevel(island.returnDim());
        if (dest == null) {
            dest = overworld;
        }
        BlockPos ret = island.returnPos();
        teleport(player, dest, ret.getX() + 0.5, ret.getY(), ret.getZ() + 0.5);
        return "Снова карта Kepler. Стоянка на месте.";
    }

    public static void tell(ServerPlayer player, String message) {
        if (player == null || message == null || message.isBlank()) {
            return;
        }
        player.displayClientMessage(Component.literal(message), false);
    }

    private static void teleport(ServerPlayer player, ServerLevel dest, double x, double y, double z) {
        player.teleportTo(dest, x, y, z, java.util.Set.<RelativeMovement>of(), player.getYRot(), player.getXRot());
    }
}
