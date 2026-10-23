package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.content.rocket.SeatEntity;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Controller ignition: pad blocks leave, everyone sees the entity.
 * Players sitting in the stack's seats ride it. Unmanned still goes to the map at 20 km.
 */
public final class VesselLaunch {
    private VesselLaunch() {
    }

    /**
     * @return empty on success, otherwise a player-facing reason
     */
    public static String ignite(Level level, BlockPos origin) {
        if (level == null || origin == null) {
            return "Нет мира для старта.";
        }
        if (level.isClientSide) {
            return "";
        }
        try {
            VesselBlockGrid grid = WorldVesselScanner.scan(level, origin);
            List<Player> crew = seatedCrew(level, grid);
            FlightLoop loop = WorldLiftoff.lift(level, origin);
            VesselEntity entity = spawn(level, loop);
            if (entity == null) {
                GridPos pad = loop.padLeft().origin();
                WorldLiftoff.land(level, new BlockPos(pad.x(), pad.y(), pad.z()), loop);
                return "Сущность не создалась, сборка возвращена на стол.";
            }
            for (Player rider : crew) {
                rider.stopRiding();
                rider.startRiding(entity, true);
            }
            return "";
        } catch (IllegalStateException | IllegalArgumentException exception) {
            String message = exception.getMessage();
            return message == null || message.isBlank() ? "Старт не удался." : message;
        }
    }

    static List<Player> seatedCrew(Level level, VesselBlockGrid grid) {
        List<Player> crew = new ArrayList<>();
        if (!(level instanceof ServerLevel server) || grid == null) {
            return crew;
        }
        Set<BlockPos> seats = new HashSet<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().kind() == VesselPartKind.CONTROLLER) {
                seats.add(new BlockPos(occupant.pos().x(), occupant.pos().y(), occupant.pos().z()));
            }
        }
        if (seats.isEmpty()) {
            return crew;
        }
        for (ServerPlayer player : server.players()) {
            if (player.getVehicle() instanceof SeatEntity seat) {
                BlockPos at = seat.blockPosition();
                if (seats.contains(at) || seats.contains(player.blockPosition())) {
                    crew.add(player);
                }
            }
        }
        return crew;
    }

    /** Visible world entity under 20 km. Crew from the pad seats may be mounted after this. */
    public static VesselEntity spawn(Level level, FlightLoop loop) {
        if (level == null || level.isClientSide || loop == null
                || loop.presence() != FlightPresence.WORLD_ENTITY) {
            return null;
        }
        VesselEntity entity = new VesselEntity(AeronauticsPlus.VESSEL.get(), level);
        entity.bind(loop);
        GridPos pad = loop.padLeft().origin();
        double altitude = Math.max(0.0, loop.vessel().orbit().altitudeMeters());
        entity.setPos(pad.x() + 0.5, pad.y() + altitude, pad.z() + 0.5);
        if (!level.addFreshEntity(entity)) {
            return null;
        }
        return entity;
    }
}
