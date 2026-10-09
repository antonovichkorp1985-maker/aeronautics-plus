package dev.leeeonidys.aeronauticsplus.content.rocket;

import com.mojang.serialization.MapCodec;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselEnvelope;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartSpec;
import dev.leeeonidys.aeronauticsplus.space.core.CellOccupancy;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.world.WorldVesselScanner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * AP fixture on a ChemMod pile (currently the Create-train mount). Collision follows
 * {@link CellOccupancy}. Right-click compiles the grid; there is no mission-map GUI.
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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return voxelShape(spec.occupancy());
    }

    @Override
    protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return spec.occupancy().isFullBlock();
    }

    private static VoxelShape voxelShape(CellOccupancy occupancy) {
        Vector3d origin = occupancy.origin();
        Vector3d size = occupancy.size();
        return Block.box(
                origin.x() * 16.0, origin.y() * 16.0, origin.z() * 16.0,
                (origin.x() + size.x()) * 16.0,
                (origin.y() + size.y()) * 16.0,
                (origin.z() + size.z()) * 16.0);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (spec.kind() == VesselPartKind.ENGINE || spec.kind() == VesselPartKind.RCS) {
            return defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
        }
        if (spec.kind() == VesselPartKind.SEPARATOR
                || spec.kind() == VesselPartKind.MOUNT
                || spec.kind() == VesselPartKind.FAIRING) {
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
    public InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        VesselBlockGrid grid = WorldVesselScanner.scan(level, pos);
        VesselCompilation compilation = VesselBlockCompiler.analyze(grid);
        if (grid.isEmpty() && compilation.diagnostics().isEmpty()) {
            player.displayClientMessage(Component.literal("Сборка пуста."), false);
            return InteractionResult.CONSUME;
        }
        if (!grid.isEmpty()) {
            VesselEnvelope envelope = VesselEnvelope.of(grid);
            if (compilation.isLaunchable()) {
                double mass = compilation.stages().stream().mapToDouble(stage -> stage.totalMassKg()).sum();
                double deltaV = compilation.stages().stream().mapToDouble(stage -> stage.idealDeltaV()).sum();
                player.displayClientMessage(Component.literal(String.format(
                        java.util.Locale.ROOT,
                        "Сборка пригодна: ступеней %d, диаметр %.1f м, высота %.1f м, масса %d кг, Δv %d м/с.",
                        compilation.stages().size(),
                        envelope.diameter(),
                        envelope.height(),
                        Math.round(mass),
                        Math.round(deltaV))), false);
            } else if (compilation.diagnosticText().contains("ON_TRANSPORTER")) {
                player.displayClientMessage(Component.literal(String.format(
                        java.util.Locale.ROOT,
                        "На транспортёре: старт запрещён. Диаметр %.1f м, высота %.1f м.",
                        envelope.diameter(),
                        envelope.height())), false);
            } else {
                player.displayClientMessage(Component.literal(String.format(
                        java.util.Locale.ROOT,
                        "Габарит сборки: диаметр %.1f м, высота %.1f м.",
                        envelope.diameter(),
                        envelope.height())), false);
            }
        }
        String text = compilation.diagnosticText();
        if (!text.isBlank()) {
            for (String line : text.split("\n")) {
                player.displayClientMessage(Component.literal(line), false);
            }
        }
        return InteractionResult.CONSUME;
    }
}
