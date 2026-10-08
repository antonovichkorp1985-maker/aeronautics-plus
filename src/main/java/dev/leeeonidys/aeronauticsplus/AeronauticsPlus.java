package dev.leeeonidys.aeronauticsplus;

import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import dev.leeeonidys.aeronauticsplus.compat.CompatibilityManager;
import dev.leeeonidys.aeronauticsplus.content.propeller.AircraftPropellerBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.AircraftPropellerBlockEntity;
import dev.leeeonidys.aeronauticsplus.content.propeller.PropellerShaftAdapterBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.PropellerSpec;
import dev.leeeonidys.aeronauticsplus.content.propeller.PrototypePropellerBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.PrototypePropellerBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(2.0f, 6.0f)
                            .noOcclusion()));

    public static final DeferredItem<BlockItem> PROTOTYPE_PROPELLER_ITEM =
            ITEMS.registerSimpleBlockItem(PROTOTYPE_PROPELLER);

    // Compact inline reducer: Create shaft diameter at the drive end and the
    // slimmer Aeronautics Plus propeller shaft at the output end.
    public static final DeferredBlock<PropellerShaftAdapterBlock> PROPELLER_SHAFT_ADAPTER =
            BLOCKS.register("propeller_shaft_adapter", () -> new PropellerShaftAdapterBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(2.5f, 6.0f)
                            .noOcclusion()));

    public static final DeferredItem<BlockItem> PROPELLER_SHAFT_ADAPTER_ITEM =
            ITEMS.registerSimpleBlockItem(PROPELLER_SHAFT_ADAPTER);

    // Э3 старт: family of larger aircraft propellers. These are not decorative blocks;
    // they reuse CA's BasePropellerBlockEntity/PropellerActorBehaviour thrust pipeline.
    public static final PropellerEntry WOODEN_TWO_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.wooden("wooden_two_blade_propeller", 2, 0.95, 0.18, 1.75f));
    public static final PropellerEntry WOODEN_THREE_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.wooden("wooden_three_blade_propeller", 3, 1.20, 0.21, 2.0f));
    public static final PropellerEntry WOODEN_FOUR_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.wooden("wooden_four_blade_propeller", 4, 1.40, 0.23, 2.0f));

    public static final PropellerEntry ALUMINUM_TWO_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.aluminum("aluminum_two_blade_propeller", 2, 1.25, 0.22, 2.0f));
    public static final PropellerEntry ALUMINUM_THREE_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.aluminum("aluminum_three_blade_propeller", 3, 1.55, 0.26, 2.25f));
    public static final PropellerEntry ALUMINUM_FOUR_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.aluminum("aluminum_four_blade_propeller", 4, 1.80, 0.29, 2.25f));

    public static final PropellerEntry STEEL_TWO_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.steel("steel_two_blade_propeller", 2, 1.50, 0.24, 2.25f));
    public static final PropellerEntry STEEL_THREE_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.steel("steel_three_blade_propeller", 3, 1.85, 0.29, 2.5f));
    public static final PropellerEntry STEEL_FOUR_BLADE_PROPELLER = registerAircraftPropeller(
            PropellerSpec.steel("steel_four_blade_propeller", 4, 2.15, 0.32, 2.5f));

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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleKineticBlockEntity>> PROPELLER_SHAFT_ADAPTER_BE =
            BLOCK_ENTITIES.register("propeller_shaft_adapter", () -> BlockEntityType.Builder.of(
                    AeronauticsPlus::createPropellerShaftAdapterBE,
                    PROPELLER_SHAFT_ADAPTER.get()).build(null));

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

    private static SimpleKineticBlockEntity createPropellerShaftAdapterBE(
            BlockPos pos,
            BlockState state
    ) {
        return new SimpleKineticBlockEntity(PROPELLER_SHAFT_ADAPTER_BE.get(), pos, state);
    }

    private static AircraftPropellerBlockEntity createAircraftPropellerBE(BlockPos pos, BlockState state) {
        return new AircraftPropellerBlockEntity(AIRCRAFT_PROPELLER_BE.get(), pos, state);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.aeronauticsplus"))
                    .icon(() -> new ItemStack(ALUMINUM_THREE_BLADE_PROPELLER.item().get()))
                    .displayItems((params, output) -> {
                        output.accept(PROPELLER_SHAFT_ADAPTER_ITEM.get());
                        output.accept(PROTOTYPE_PROPELLER_ITEM.get());
                        AIRCRAFT_PROPELLERS.forEach(entry -> output.accept(entry.item().get()));
                    })
                    .build());

    public AeronauticsPlus(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        TABS.register(modEventBus);
        modEventBus.addListener(AeronauticsPlus::onCommonSetup);
        LOGGER.info("Aeronautics Plus 0.2.2-test.10: RU-pack auto-updater and propeller registration queued.");
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CompatibilityManager.initialize();

            List<String> registeredItems = new ArrayList<>();
            registeredItems.add(BuiltInRegistries.ITEM.getKey(PROPELLER_SHAFT_ADAPTER_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(PROTOTYPE_PROPELLER_ITEM.get()).toString());
            AIRCRAFT_PROPELLERS.forEach(entry ->
                    registeredItems.add(BuiltInRegistries.ITEM.getKey(entry.item().get()).toString()));

            boolean allRegistered = registeredItems.size() == 11
                    && registeredItems.stream().allMatch(id -> id.startsWith(MODID + ":"));
            if (allRegistered) {
                LOGGER.info(
                        "Aeronautics Plus registry ready: {} items; creative tab {}:main; ids={}",
                        registeredItems.size(), MODID, registeredItems);
            } else {
                LOGGER.error("Aeronautics Plus registry verification failed: ids={}", registeredItems);
            }
        });
    }

    private static PropellerEntry registerAircraftPropeller(PropellerSpec spec) {
        DeferredBlock<AircraftPropellerBlock> block = BLOCKS.register(spec.id(), () -> new AircraftPropellerBlock(
                spec,
                BlockBehaviour.Properties.of()
                        .mapColor(spec.mapColor())
                        .strength(spec.hardness(), spec.resistance())
                        .noOcclusion()));
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
