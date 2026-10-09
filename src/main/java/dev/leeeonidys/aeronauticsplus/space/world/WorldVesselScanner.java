package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlock;
import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Reads connected {@link VesselPartBlock}s from a Minecraft level into a compiler grid. */
public final class WorldVesselScanner {
    private WorldVesselScanner() {
    }

    public static VesselCompilation compileAt(Level level, BlockPos origin) {
        return VesselBlockCompiler.compile(scan(level, origin)).analyze();
    }

    public static VesselBlockGrid scan(Level level, BlockPos origin) {
        BlockState start = level.getBlockState(origin);
        if (!(start.getBlock() instanceof VesselPartBlock)) {
            return new VesselBlockGrid(List.of());
        }
        List<VesselBlockOccupant> occupants = new ArrayList<>();
        ArrayDeque<BlockPos> pending = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        pending.add(origin.immutable());
        while (!pending.isEmpty() && occupants.size() < VesselBlockGrid.MAX_BLOCKS) {
            BlockPos current = pending.remove();
            if (!visited.add(current)) {
                continue;
            }
            BlockState state = level.getBlockState(current);
            if (!(state.getBlock() instanceof VesselPartBlock part)) {
                continue;
            }
            Direction facing = state.getValue(VesselPartBlock.FACING);
            occupants.add(new VesselBlockOccupant(
                    new GridPos(current.getX(), current.getY(), current.getZ()),
                    part.spec(),
                    toFace(facing)));
            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (!visited.contains(next) && level.getBlockState(next).getBlock() instanceof VesselPartBlock) {
                    pending.add(next);
                }
            }
        }
        return new VesselBlockGrid(occupants);
    }

    private static BlockFace toFace(Direction direction) {
        return switch (direction) {
            case DOWN -> BlockFace.DOWN;
            case UP -> BlockFace.UP;
            case NORTH -> BlockFace.NORTH;
            case SOUTH -> BlockFace.SOUTH;
            case WEST -> BlockFace.WEST;
            case EAST -> BlockFace.EAST;
        };
    }
}
