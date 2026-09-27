package dev.leeeonidys.aeronauticsplus.content.propeller;

import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Family propeller block for Э3: same Create Aeronautics thrust implementation,
 * different material/blade presets supplied by {@link PropellerSpec}.
 */
public class AircraftPropellerBlock extends BasePropellerBlock {
    private final PropellerSpec spec;

    public AircraftPropellerBlock(PropellerSpec spec, Properties properties) {
        super(properties);
        this.spec = spec;
    }

    public PropellerSpec spec() {
        return spec;
    }

    @Override
    public BlockEntityType<? extends BasePropellerBlockEntity> getBlockEntityType() {
        return AeronauticsPlus.AIRCRAFT_PROPELLER_BE.get();
    }
}
