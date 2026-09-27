package dev.leeeonidys.aeronauticsplus.content.propeller;

import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Э2: our own propeller block on top of CA's public propeller framework (MIT code).
 * All kinetics, wrench interaction and thrust behaviour are inherited from
 * {@link BasePropellerBlock}; we only bind our own BlockEntityType.
 */
public class PrototypePropellerBlock extends BasePropellerBlock {

    public PrototypePropellerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends BasePropellerBlockEntity> getBlockEntityType() {
        return AeronauticsPlus.PROTOTYPE_PROPELLER_BE.get();
    }
}
