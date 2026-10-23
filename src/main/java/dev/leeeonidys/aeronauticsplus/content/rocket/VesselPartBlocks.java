package dev.leeeonidys.aeronauticsplus.content.rocket;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Lookup AP vessel-part blocks by compiler spec id. ChemMod tanks are absent. */
public final class VesselPartBlocks {
    private VesselPartBlocks() {
    }

    public static Block blockFor(String specId) {
        if (specId == null || specId.isBlank()) {
            return null;
        }
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof VesselPartBlock part && part.spec().id().equals(specId)) {
                return block;
            }
        }
        return null;
    }

    public static BlockState stateFor(String specId, BlockFace facing) {
        Block block = blockFor(specId);
        if (block == null) {
            return null;
        }
        BlockState state = block.defaultBlockState();
        if (facing != null && state.hasProperty(VesselPartBlock.FACING)) {
            state = state.setValue(VesselPartBlock.FACING, toDirection(facing));
        }
        return state;
    }

    public static Direction toDirection(BlockFace face) {
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
