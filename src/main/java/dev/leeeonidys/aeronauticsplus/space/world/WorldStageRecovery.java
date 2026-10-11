package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlock;
import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.RecoveryPlan;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
            Block block = blockFor(placement.spec().id());
            if (block == null) {
                continue;
            }
            GridPos relative = placement.relativeToPad();
            BlockPos at = padPos.offset(relative.x(), relative.y(), relative.z());
            if (!level.getBlockState(at).canBeReplaced()) {
                continue;
            }
            BlockState state = block.defaultBlockState();
            if (state.hasProperty(VesselPartBlock.FACING)) {
                state = state.setValue(VesselPartBlock.FACING, toDirection(placement.facing()));
            }
            if (level.setBlock(at, state, 3)) {
                placed++;
            }
        }
        return placed;
    }

    private static Block blockFor(String specId) {
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof VesselPartBlock part && part.spec().id().equals(specId)) {
                return block;
            }
        }
        return null;
    }

    private static Direction toDirection(BlockFace face) {
        return switch (face) {
            case DOWN -> Direction.DOWN;
            case UP -> Direction.UP;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case EAST -> Direction.EAST;
        };
    }
}
