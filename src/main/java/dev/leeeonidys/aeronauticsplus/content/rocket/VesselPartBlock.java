package dev.leeeonidys.aeronauticsplus.content.rocket;

import com.mojang.serialization.MapCodec;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.compat.TfcLatitude;
import dev.leeeonidys.aeronauticsplus.space.compile.TrainMount;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselEnvelope;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartSpec;
import dev.leeeonidys.aeronauticsplus.space.core.CellOccupancy;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.world.StationEnter;
import dev.leeeonidys.aeronauticsplus.space.world.StationPocket;
import dev.leeeonidys.aeronauticsplus.space.world.VesselEntity;
import dev.leeeonidys.aeronauticsplus.space.world.VesselLaunch;
import dev.leeeonidys.aeronauticsplus.space.world.WorldVesselScanner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerPlayer;
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
 * {@link CellOccupancy}. Right-click compiles the grid. Kepler map is key M, not this click.
 */
public final class VesselPartBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private final VesselPartSpec spec;

    public VesselPartBlock(VesselPartSpec spec, Properties properties) {
        super(properties);
        this.spec = spec;
        Direction facing = spec.kind() == VesselPartKind.ENGINE
                ? Direction.DOWN
                : spec.kind() == VesselPartKind.MOUNT || spec.kind() == VesselPartKind.PAD
                        || spec.kind() == VesselPartKind.LEGS
                ? Direction.NORTH
                : Direction.UP;
        registerDefaultState(stateDefinition.any().setValue(FACING, facing));
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
        if (spec.kind() == VesselPartKind.SEPARATOR || spec.kind() == VesselPartKind.DOCKING) {
            Direction clicked = context.getClickedFace();
            if (clicked.getAxis().isHorizontal()) {
                return defaultBlockState().setValue(FACING, clicked);
            }
            return defaultBlockState().setValue(FACING, Direction.UP);
        }
        if (spec.kind() == VesselPartKind.MOUNT
                || spec.kind() == VesselPartKind.PAD
                || spec.kind() == VesselPartKind.LEGS) {
            return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
        }
        if (spec.kind() == VesselPartKind.FAIRING) {
            return defaultBlockState().setValue(FACING, Direction.UP);
        }
        if (spec.kind() == VesselPartKind.CONTROLLER) {
            // A seat faces where the builder looked, so sitting down keeps that heading.
            // getNearestLookingDirection().getOpposite() would turn the chair to face the player.
            return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
        }
        if (spec.kind() == VesselPartKind.WING) {
            // Wing cells attach side-by-side; face where the builder looked horizontally.
            return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
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
        if (spec.kind() == VesselPartKind.CONTROLLER) {
            if (StationPocket.isStation(level) && player.isShiftKeyDown()
                    && player instanceof ServerPlayer serverPlayer) {
                StationEnter.tell(serverPlayer, StationEnter.leave(serverPlayer));
                return InteractionResult.CONSUME;
            }
            boolean sittingHere = player.getVehicle() instanceof SeatEntity
                    && player.getVehicle().blockPosition().equals(pos);
            if (sittingHere || player.isShiftKeyDown()) {
                VesselBlockGrid padGrid = WorldVesselScanner.scan(level, pos);
                VesselCompilation padCompilation = VesselBlockCompiler.analyze(padGrid);
                if (padCompilation.isLaunchable() && padCompilation.diagnosticText().contains("ON_PAD")) {
                    String fail = VesselLaunch.ignite(level, pos);
                    if (fail.isBlank()) {
                        if (player.getVehicle() instanceof VesselEntity) {
                            player.displayClientMessage(
                                    Component.literal("Старт. Ты на борту. Карта M с 20 км, если слезешь."), false);
                        } else {
                            player.displayClientMessage(
                                    Component.literal("Старт. Беспилотник. Карта: клавиша M."), false);
                        }
                    } else {
                        player.displayClientMessage(Component.literal(fail), false);
                    }
                    return InteractionResult.CONSUME;
                }
            }
            if (!sittingHere) {
                sit(level, state, pos, player);
                return InteractionResult.CONSUME;
            }
        }
        if (spec.kind() == VesselPartKind.MOUNT && player.isShiftKeyDown()) {
            String fail = unclampMount(level, pos);
            player.displayClientMessage(
                    Component.literal(fail.isBlank() ? "Снято с крепления поезда." : fail), false);
            return InteractionResult.CONSUME;
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
                String latitude = TfcLatitude.describe(level, pos);
                if (!latitude.isBlank()) {
                    player.displayClientMessage(Component.literal(latitude), false);
                }
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

    private static String unclampMount(Level level, BlockPos pos) {
        VesselBlockGrid grid = WorldVesselScanner.scan(level, pos);
        if (!TrainMount.hasCargo(grid) || TrainMount.mounts(grid).isEmpty()) {
            return "Нечего снимать с крепления.";
        }
        int removed = 0;
        for (VesselBlockOccupant occupant : TrainMount.mounts(grid)) {
            BlockPos at = new BlockPos(occupant.pos().x(), occupant.pos().y(), occupant.pos().z());
            BlockState state = level.getBlockState(at);
            if (state.getBlock() instanceof VesselPartBlock part && part.spec().kind() == VesselPartKind.MOUNT) {
                level.destroyBlock(at, true);
                removed++;
            }
        }
        return removed == 0 ? "Нечего снимать с крепления." : "";
    }

    /**
     * Put the player in the bucket seat. The seat entity sits at the cell base and
     * {@link SeatEntity#getPassengerAttachmentPoint} drops the rider so that the hips land on the
     * pan at y = 0.40 in the model. Mounting also sets the heading to the chair's own facing,
     * because sitting sideways in a seat that was placed facing north looks broken.
     */
    private static void sit(Level level, BlockState state, BlockPos pos, Player player) {
        if (player.isPassenger()) {
            return;
        }
        Direction facing = state.getValue(FACING);
        float yaw = facing.getAxis().isHorizontal() ? facing.toYRot() : 180.0f;
        SeatEntity seat = new SeatEntity(AeronauticsPlus.SEAT.get(), level);
        seat.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, 0.0f);
        level.addFreshEntity(seat);
        player.startRiding(seat);
        player.setYRot(yaw);
        player.setYHeadRot(yaw);
    }
}
