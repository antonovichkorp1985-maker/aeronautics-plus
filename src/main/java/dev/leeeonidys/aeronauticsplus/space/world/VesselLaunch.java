package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
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
            VesselEntity entity = new VesselEntity(AeronauticsPlus.VESSEL.get(), level);
            entity.bind(loop);
            GridPos pad = loop.padLeft().origin();
            entity.setPos(pad.x() + 0.5, pad.y(), pad.z() + 0.5);
            if (!level.addFreshEntity(entity)) {
                WorldLiftoff.land(level, new BlockPos(pad.x(), pad.y(), pad.z()), loop);
                return "Сущность не создалась, сборка возвращена на стол.";
            }
            return "";
        } catch (IllegalStateException | IllegalArgumentException exception) {
            String message = exception.getMessage();
            return message == null || message.isBlank() ? "Старт не удался." : message;
        }
    }
}
