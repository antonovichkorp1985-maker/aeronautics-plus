package dev.leeeonidys.aeronauticsplus;

import dev.leeeonidys.aeronauticsplus.content.propeller.PrototypePropellerBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.PrototypePropellerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aeronautics Plus — aircraft parts for Create Aeronautics.
 *
 * Roadmap: Э1 scaffold (CI green 27.09) -> Э2 prototype propeller (this commit)
 *          -> Э3 family sizes/materials -> Э4 helicopter rotors -> Э5 wings/control surfaces
 *          -> Э6 jet nozzles (TFMG fuel via shim).
 *
 * Code license: MIT. Assets: own artwork only (CA/VW assets are All Rights Reserved).
 */
@Mod(AeronauticsPlus.MODID)
public final class AeronauticsPlus {
    public static final String MODID = "aeronauticsplus";
    public static final Logger LOGGER = LoggerFactory.getLogger("AeronauticsPlus");

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Э2: prototype aircraft propeller, built on CA's MIT-licensed propeller classes.
    public static final DeferredBlock<PrototypePropellerBlock> PROTOTYPE_PROPELLER =
            BLOCKS.register("prototype_propeller", () -> new PrototypePropellerBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0f, 6.0f)));

    public static final DeferredItem<BlockItem> PROTOTYPE_PROPELLER_ITEM =
            ITEMS.registerSimpleBlockItem(PROTOTYPE_PROPELLER);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PrototypePropellerBlockEntity>> PROTOTYPE_PROPELLER_BE =
            BLOCK_ENTITIES.register("prototype_propeller", () -> BlockEntityType.Builder.of(
                    PrototypePropellerBlockEntity::new, PROTOTYPE_PROPELLER.get()).build(null));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.aeronauticsplus"))
                    .icon(() -> new ItemStack(PROTOTYPE_PROPELLER_ITEM.get()))
                    .displayItems((params, output) -> output.accept(PROTOTYPE_PROPELLER_ITEM.get()))
                    .build());

    public AeronauticsPlus(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        TABS.register(modEventBus);
        LOGGER.info("Aeronautics Plus 0.1.0: Э2 prototype propeller registered.");
    }
}
