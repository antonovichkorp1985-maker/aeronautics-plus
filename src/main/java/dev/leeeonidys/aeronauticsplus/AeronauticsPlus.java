package dev.leeeonidys.aeronauticsplus;

import dev.leeeonidys.aeronauticsplus.content.propeller.AircraftPropellerBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.AircraftPropellerBlockEntity;
import dev.leeeonidys.aeronauticsplus.content.propeller.PropellerSpec;
import dev.leeeonidys.aeronauticsplus.content.propeller.PrototypePropellerBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.PrototypePropellerBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
 * Roadmap: Э1 scaffold -> Э2 prototype propeller -> Э3 propeller family
 *          -> Э4 helicopter rotors -> Э5 wings/control surfaces
 *          -> Э6 compatibility mounts/fuel shims for third-party jet engines.
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

    // Э3 старт: family of larger aircraft propellers. These are not decorative blocks;
    // they reuse CA's BasePropellerBlockEntity/PropellerActorBehaviour thrust pipeline.
    public static final PropellerEntry WOODEN_TWO_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.wooden("wooden_two_blade_propeller", 2, 95.0, 1.8, 1.75f));
    public static final PropellerEntry WOODEN_THREE_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.wooden("wooden_three_blade_propeller", 3, 120.0, 2.1, 2.0f));
    public static final PropellerEntry WOODEN_FOUR_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.wooden("wooden_four_blade_propeller", 4, 140.0, 2.3, 2.0f));

    public static final PropellerEntry ALUMINUM_TWO_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.aluminum("aluminum_two_blade_propeller", 2, 125.0, 2.2, 2.0f));
    public static final PropellerEntry ALUMINUM_THREE_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.aluminum("aluminum_three_blade_propeller", 3, 155.0, 2.6, 2.25f));
    public static final PropellerEntry ALUMINUM_FOUR_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.aluminum("aluminum_four_blade_propeller", 4, 180.0, 2.9, 2.25f));

    public static final PropellerEntry STEEL_TWO_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.steel("steel_two_blade_propeller", 2, 150.0, 2.4, 2.25f));
    public static final PropellerEntry STEEL_THREE_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.steel("steel_three_blade_propeller", 3, 185.0, 2.9, 2.5f));
    public static final PropellerEntry STEEL_FOUR_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.steel("steel_four_blade_propeller", 4, 215.0, 3.2, 2.5f));

    public static final List<PropellerEntry> AIRCRAFT_PROPELLERS = List.of(
            WOODEN_TWO_BLADE_PROPELLER,
            WOODEN_THREE_BLADE_PROPELLER,
            WOODEN_FOUR_BLADE_PROPELLER,
            ALUMINUM_TWO_BLADE_PROPELLER,
            ALUMINUM_THREE_BLADE_PROPELLER,
            ALUMINUM_FOUR_BLADE_PROPELLER,
            STEEL_TWO_BLADE_PROPELLER,
            STEEL_THREE_BLADE_PROPELLER,
            STEEL_FOUR_BLADE_PROPELLER
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PrototypePropellerBlockEntity>> PROTOTYPE_PROPELLER_BE =
            BLOCK_ENTITIES.register("prototype_propeller", () -> BlockEntityType.Builder.of(
                    AeronauticsPlus::createPrototypePropellerBE, PROTOTYPE_PROPELLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AircraftPropellerBlockEntity>> AIRCRAFT_PROPELLER_BE =
            BLOCK_ENTITIES.register("aircraft_propeller", () -> BlockEntityType.Builder.of(
                    AeronauticsPlus::createAircraftPropellerBE,
                    WOODEN_TWO_BLADE_PROPELLER.block().get(),
                    WOODEN_THREE_BLADE_PROPELLER.block().get(),
                    WOODEN_FOUR_BLADE_PROPELLER.block().get(),
                    ALUMINUM_TWO_BLADE_PROPELLER.block().get(),
                    ALUMINUM_THREE_BLADE_PROPELLER.block().get(),
                    ALUMINUM_FOUR_BLADE_PROPELLER.block().get(),
                    STEEL_TWO_BLADE_PROPELLER.block().get(),
                    STEEL_THREE_BLADE_PROPELLER.block().get(),
                    STEEL_FOUR_BLADE_PROPELLER.block().get()).build(null));

    /** Фабрика для BlockEntitySupplier: тип подставляется в момент регистрации (HOLDER уже присвоен). */
    private static PrototypePropellerBlockEntity createPrototypePropellerBE(BlockPos pos, BlockState state) {
        return new PrototypePropellerBlockEntity(PROTOTYPE_PROPELLER_BE.get(), pos, state);
    }

    private static AircraftPropellerBlockEntity createAircraftPropellerBE(BlockPos pos, BlockState state) {
        return new AircraftPropellerBlockEntity(AIRCRAFT_PROPELLER_BE.get(), pos, state);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.aeronauticsplus"))
                    .icon(() -> new ItemStack(ALUMINUM_THREE_BLADE_PROPELLER.item().get()))
                    .displayItems((params, output) -> {
                        output.accept(PROTOTYPE_PROPELLER_ITEM.get());
                        AIRCRAFT_PROPELLERS.forEach(entry -> output.accept(entry.item().get()));
                    })
                    .build());

    public AeronauticsPlus(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        TABS.register(modEventBus);
        LOGGER.info("Aeronautics Plus 0.2.0-dev: prototype and aircraft propeller family registered.");
    }

    private static PropellerEntry registerAircraftPropeller(PropellerSpec spec) {
        DeferredBlock<AircraftPropellerBlock> block = BLOCKS.register(spec.id(), () -> new AircraftPropellerBlock(
                spec,
                BlockBehaviour.Properties.of()
                        .mapColor(spec.mapColor())
                        .strength(spec.hardness(), spec.resistance())));
        DeferredItem<BlockItem> item = ITEMS.registerSimpleBlockItem(block);
        return new PropellerEntry(spec, block, item);
    }

    public record PropellerEntry(
            PropellerSpec spec,
            DeferredBlock<AircraftPropellerBlock> block,
            DeferredItem<BlockItem> item
    ) {
    }
}
