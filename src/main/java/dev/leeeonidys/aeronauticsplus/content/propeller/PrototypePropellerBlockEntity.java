package dev.leeeonidys.aeronauticsplus.content.propeller;

import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Э2: prototype propeller block entity.
 * Contract from CA javap (Э0-рекон): implement getConfigThrust / getConfigAirflow / getRadius.
 * createBehavior() is already concrete in the base class and wires the
 * PropellerActorBehaviour (thrust layers + entity push) for us.
 *
 * Prototype tuning (kept as a baseline while Э3 propeller-family balancing starts):
 *  thrust-per-RPM 1.5, airflow-per-RPM 0.25, radius 2 blocks.
 */
public class PrototypePropellerBlockEntity extends BasePropellerBlockEntity {

    public PrototypePropellerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public double getConfigThrust() {
        return 1.50;
    }

    @Override
    public double getConfigAirflow() {
        return 0.25;
    }

    @Override
    public float getRadius() {
        return 2.0f;
    }
}
