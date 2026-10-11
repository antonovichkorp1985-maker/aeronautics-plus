package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlocks;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.RecoveryPlan;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Writes a recovered booster back as blocks on a pad. Called from
 * {@link WorldLiftoff#land} after the world entity is back under 20 km.
 * Server-side; everyone on the Level sees it. ChemMod tanks are skipped.
 */
public final class WorldStageRecovery {
    private WorldStageRecovery() {
    }

    public static int place(Level level, BlockPos padPos, RecoveryPlan plan) {
        if (level == null || padPos == null || plan == null) {
            throw new IllegalArgumentException("Recovery place needs a level, pad and plan");
        }
        if (level.isClientSide) {
            return 0;
        }
        int placed = 0;
        for (RecoveryPlan.Placement placement : plan.placements()) {
            BlockState state = VesselPartBlocks.stateFor(placement.spec().id(), placement.facing());
            if (state == null) {
                continue;
            }
            GridPos relative = placement.relativeToPad();
            BlockPos at = padPos.offset(relative.x(), relative.y(), relative.z());
            if (!level.getBlockState(at).canBeReplaced()) {
                continue;
            }
            if (level.setBlock(at, state, 3)) {
                placed++;
            }
        }
        return placed;
    }
}
