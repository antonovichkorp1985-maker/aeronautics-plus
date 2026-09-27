package dev.leeeonidys.aeronauticsplus.content.propeller;

import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared block entity for the aircraft propeller family.
 *
 * BasePropellerBlockEntity creates the actual PropellerActorBehaviour in CA;
 * this class only exposes per-block tuning constants from the owning block.
 */
public class AircraftPropellerBlockEntity extends BasePropellerBlockEntity {

    public AircraftPropellerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private PropellerSpec spec() {
        Block block = getBlockState().getBlock();
        if (block instanceof AircraftPropellerBlock aircraftPropellerBlock) {
            return aircraftPropellerBlock.spec();
        }

        // Defensive fallback for unexpected states during data-fix/load edge cases.
        return PropellerSpec.aluminum("fallback_aircraft_propeller", 3, 150.0, 2.5, 2.0f);
    }

    @Override
    public double getConfigThrust() {
        return spec().thrust();
    }

    @Override
    public double getConfigAirflow() {
        return spec().airflow();
    }

    @Override
    public float getRadius() {
        return spec().radius();
    }
}
