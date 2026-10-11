package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlocks;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.StationPlan;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Writes a parked station as blocks in the AP pocket. Same idea as
 * {@link WorldStageRecovery}: server-side, everyone on the Level sees it.
 */
public final class WorldStation {
    private WorldStation() {
    }

    public static int place(Level level, BlockPos island, StationPlan plan) {
        if (level == null || island == null || plan == null) {
            throw new IllegalArgumentException("Station place needs a level, island and plan");
        }
        if (level.isClientSide) {
            return 0;
        }
        GridPos origin = plan.origin();
        int placed = 0;
        for (StationPlan.Placement placement : plan.placements()) {
            BlockState state = VesselPartBlocks.stateFor(placement.spec().id(), placement.facing());
            if (state == null) {
                continue;
            }
            BlockPos at = island.offset(
                    placement.pos().x() - origin.x(),
                    placement.pos().y() - origin.y(),
                    placement.pos().z() - origin.z());
            if (!level.getBlockState(at).canBeReplaced()) {
                continue;
            }
            if (level.setBlock(at, state, 3)) {
                placed++;
            }
        }
        return placed;
    }

    public static int clear(Level level, BlockPos island, StationPlan plan) {
        if (level == null || island == null || plan == null) {
            throw new IllegalArgumentException("Station clear needs a level, island and plan");
        }
        if (level.isClientSide) {
            return 0;
        }
        GridPos origin = plan.origin();
        int cleared = 0;
        for (StationPlan.Placement placement : plan.placements()) {
            BlockPos at = island.offset(
                    placement.pos().x() - origin.x(),
                    placement.pos().y() - origin.y(),
                    placement.pos().z() - origin.z());
            if (level.getBlockState(at).isAir()) {
                continue;
            }
            if (level.setBlock(at, Blocks.AIR.defaultBlockState(), 3)) {
                cleared++;
            }
        }
        return cleared;
    }

    public static BlockPos stand(BlockPos island, StationPlan plan) {
        GridPos stand = plan.habitatStand();
        return island.offset(stand.x(), stand.y(), stand.z());
    }
}
