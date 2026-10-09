package dev.leeeonidys.aeronauticsplus.content.rocket;

import com.mojang.serialization.MapCodec;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartSpec;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.world.WorldVesselScanner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Physical rocket part. Right-click compiles the connected grid into a vessel blueprint
 * and prints diagnostics; there is no mission-map GUI in this slice.
 */
public final class VesselPartBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private final VesselPartSpec spec;

    public VesselPartBlock(VesselPartSpec spec, Properties properties) {
        super(properties);
        this.spec = spec;
        registerDefaultState(stateDefinition.any().setValue(
                FACING, spec.kind() == VesselPartKind.ENGINE ? Direction.DOWN : Direction.UP));
    }

    public VesselPartSpec spec() {
        return spec;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        VesselPartSpec captured = spec;
        return simpleCodec(properties -> new VesselPartBlock(captured, properties));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (spec.kind() == VesselPartKind.ENGINE) {
            return defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
        }
        if (spec.kind() == VesselPartKind.SEPARATOR) {
            return defaultBlockState().setValue(FACING, Direction.UP);
        }
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected ItemInteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        VesselCompilation compilation = WorldVesselScanner.compileAt(level, pos);
        if (compilation.stages().isEmpty() && compilation.diagnostics().isEmpty()) {
            player.displayClientMessage(Component.literal("Сборка пуста."), false);
            return ItemInteractionResult.CONSUME;
        }
        if (compilation.isLaunchable()) {
            player.displayClientMessage(Component.literal(
                    "Сборка пригодна: ступеней " + compilation.stages().size()
                            + ", масса " + Math.round(compilation.stages().stream()
                            .mapToDouble(stage -> stage.totalMassKg()).sum())
                            + " кг."), false);
        }
        String text = compilation.diagnosticText();
        if (!text.isBlank()) {
            for (String line : text.split("\n")) {
                player.displayClientMessage(Component.literal(line), false);
            }
        }
        return ItemInteractionResult.CONSUME;
    }
}
