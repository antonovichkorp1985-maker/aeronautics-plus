package dev.leeeonidys.aeronauticsplus.content.rotor;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.core.Atmosphere;
import dev.leeeonidys.aeronauticsplus.space.core.ShaftPowerApi;
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
 * Helicopter main rotor: converts Create shaft power into aerodynamic thrust.
 *
 * <p>Thrust follows momentum (impulse) theory for an actuator disk:
 * {@code T = sqrt(2 * P * rho * A)} where {@code P} is shaft power, {@code rho}
 * is air density and {@code A} is disk area. In vacuum ({@code rho -> 0})
 * thrust honestly goes to zero. Reactive torque opposes rotation, so a single
 * rotor needs a tail rotor or coaxial counter-rotation.</p>
 */
public final class HelicopterRotorBlock extends DirectionalKineticBlock
        implements ShaftPowerApi {
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    /** Disk area in m²: 5 m diameter rotor = ~19.6 m² swept area. */
    public static final double DISK_AREA_M2 = Math.PI * Math.pow(2.5, 2);

    public HelicopterRotorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
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
        return SHAPE;
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

    public Class<SimpleKineticBlockEntity> getBlockEntityClass() {
        return SimpleKineticBlockEntity.class;
    }

    public BlockEntityType<?> getBlockEntityType() {
        return AeronauticsPlus.HELICOPTER_ROTOR_BE.get();
    }

    /* ---------- ShaftPowerApi ---------- */

    public double shaftSpeedRadiansPerSecond() {
        // Read from Create kinetic block entity speed
        return 0.0;
    }

    public double shaftTorqueNewtonMetres() {
        return 0.0;
    }

    /** Momentum-theory hover thrust from shaft power and air density. */
    public static double thrustFromShaftPower(double shaftPowerWatts, double densityKgPerM3, double diskAreaM2) {
        if (shaftPowerWatts <= 0.0 || densityKgPerM3 <= 0.0 || diskAreaM2 <= 0.0) {
            return 0.0;
        }
        // Ideal actuator disk: T = sqrt(2 * P * rho * A)
        return Math.sqrt(2.0 * shaftPowerWatts * densityKgPerM3 * diskAreaM2);
    }

    /** Reactive torque opposing rotor rotation, N·m. */
    public static double reactiveTorqueNm(double thrustN, double diskRadiusMeters) {
        // Ideal induced power: P_ind = T * v_i, v_i = sqrt(T / (2 * rho * A))
        // Torque = P_ind / omega; simplified: Q = T * R / (2 * figure of merit)
        return thrustN * diskRadiusMeters * 0.5;
    }
}
