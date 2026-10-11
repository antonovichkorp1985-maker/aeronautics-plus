package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** Controller ignition: pad blocks leave, everyone sees the entity. Player stays on the ground. */
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
            FlightLoop loop = WorldLiftoff.lift(level, origin);
            if (!spawn(level, loop)) {
                GridPos pad = loop.padLeft().origin();
                WorldLiftoff.land(level, new BlockPos(pad.x(), pad.y(), pad.z()), loop);
                return "Сущность не создалась, сборка возвращена на стол.";
            }
            return "";
        } catch (IllegalStateException | IllegalArgumentException exception) {
            String message = exception.getMessage();
            return message == null || message.isBlank() ? "Старт не удался." : message;
        }
    }

    /** Visible world entity under 20 km. Player is not mounted. */
    public static boolean spawn(Level level, FlightLoop loop) {
        if (level == null || level.isClientSide || loop == null
                || loop.presence() != FlightPresence.WORLD_ENTITY) {
            return false;
        }
        VesselEntity entity = new VesselEntity(AeronauticsPlus.VESSEL.get(), level);
        entity.bind(loop);
        GridPos pad = loop.padLeft().origin();
        double altitude = Math.max(0.0, loop.vessel().orbit().altitudeMeters());
        entity.setPos(pad.x() + 0.5, pad.y() + altitude, pad.z() + 0.5);
        return level.addFreshEntity(entity);
    }
}
