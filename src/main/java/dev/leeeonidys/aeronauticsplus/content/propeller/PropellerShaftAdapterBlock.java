package dev.leeeonidys.aeronauticsplus.content.propeller;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Inline reducer between Create's six-pixel shaft and the slim propeller hub.
 *
 * <p>{@link #FACING} points toward the narrow propeller end. Both axial faces
 * remain kinetic connections, so the adapter is a real transmission part and
 * not merely a decorative sleeve. Clicking an end face with a Create wrench
 * flips the reducer without changing its axis.</p>
 */
public final class PropellerShaftAdapterBlock extends DirectionalKineticBlock
        implements IBE<SimpleKineticBlockEntity> {
    private static final VoxelShape X_SHAPE = Block.box(0, 4.5, 4.5, 16, 11.5, 11.5);
    private static final VoxelShape Y_SHAPE = Block.box(4.5, 0, 4.5, 11.5, 16, 11.5);
    private static final VoxelShape Z_SHAPE = Block.box(4.5, 4.5, 0, 11.5, 11.5, 16);

    public PropellerShaftAdapterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(
            LevelReader level,
            BlockPos pos,
            BlockState state,
            Direction face
    ) {
        return face.getAxis() == getRotationAxis(state);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return switch (getRotationAxis(state)) {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getRotatedBlockState(BlockState state, Direction targetedFace) {
        Direction facing = state.getValue(FACING);
        if (targetedFace.getAxis() == facing.getAxis()) {
            return state.setValue(FACING, facing.getOpposite());
        }
        return state.setValue(FACING, facing.getClockWise(targetedFace.getAxis()));
    }

    @Override
    public Class<SimpleKineticBlockEntity> getBlockEntityClass() {
        return SimpleKineticBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SimpleKineticBlockEntity> getBlockEntityType() {
        return AeronauticsPlus.PROPELLER_SHAFT_ADAPTER_BE.get();
    }
}
