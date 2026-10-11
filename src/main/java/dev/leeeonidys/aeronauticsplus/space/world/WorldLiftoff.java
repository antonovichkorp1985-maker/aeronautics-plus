package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Pile leaves the pad: flying parts are removed, the table stays.
 * Recovery write is {@link WorldStageRecovery#place}.
 */
public final class WorldLiftoff {
    private WorldLiftoff() {
    }

    public static FlightLoop lift(Level level, BlockPos origin) {
        if (level == null || origin == null) {
            throw new IllegalArgumentException("Liftoff needs a level and origin");
        }
        if (level.isClientSide) {
            throw new IllegalStateException("Liftoff is server-side");
        }
        VesselBlockGrid grid = WorldVesselScanner.scan(level, origin);
        FlightLoop loop = FlightLoop.sit(grid).liftoff();
        for (VesselBlockOccupant occupant : loop.flying().occupants()) {
            BlockPos at = new BlockPos(occupant.pos().x(), occupant.pos().y(), occupant.pos().z());
            level.removeBlock(at, false);
        }
        return loop;
    }

    public static int land(Level level, BlockPos padPos, FlightLoop loop) {
        if (level == null || padPos == null || loop == null) {
            throw new IllegalArgumentException("Landing place needs a level, pad and loop");
        }
        if (level.isClientSide) {
            return 0;
        }
        FlightLoop recovered = loop.presence() == FlightPresence.BLOCKS_ON_PAD && loop.recovery() != null
                ? loop
                : loop.recover();
        return WorldStageRecovery.place(level, padPos, recovered.recovery());
    }
}
