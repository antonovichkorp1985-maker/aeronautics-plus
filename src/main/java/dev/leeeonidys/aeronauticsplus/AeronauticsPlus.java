package dev.leeeonidys.aeronauticsplus;

import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import dev.leeeonidys.aeronauticsplus.compat.CompatibilityManager;
import dev.leeeonidys.aeronauticsplus.content.propeller.AircraftPropellerBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.AircraftPropellerBlockEntity;
import dev.leeeonidys.aeronauticsplus.content.propeller.PropellerShaftAdapterBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.PropellerSpec;
import dev.leeeonidys.aeronauticsplus.content.propeller.PrototypePropellerBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.PrototypePropellerBlockEntity;
import dev.leeeonidys.aeronauticsplus.content.rocket.SeatEntity;
import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlock;
import dev.leeeonidys.aeronauticsplus.content.space.CryoCapsuleBlock;
import dev.leeeonidys.aeronauticsplus.space.compile.AssemblySlice;
import dev.leeeonidys.aeronauticsplus.space.compile.DockCaptureSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.OccupancySlice;
import dev.leeeonidys.aeronauticsplus.space.compile.PackingSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.PayloadSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.SpacecraftSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.DockingSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.SplitTankSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.TrainMountSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.TransporterSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.VarietySlice;
import dev.leeeonidys.aeronauticsplus.space.compile.ElectronicsSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.ControllerSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.IntegralFeedSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.PadSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.RadialSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.RecoverySlice;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselCompileSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartSpec;
import dev.leeeonidys.aeronauticsplus.space.core.AscentSlice;
import dev.leeeonidys.aeronauticsplus.space.core.AttitudeSlice;
import dev.leeeonidys.aeronauticsplus.space.core.BoostbackLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.BoostbackSlice;
import dev.leeeonidys.aeronauticsplus.space.core.CircularizationSlice;
import dev.leeeonidys.aeronauticsplus.space.core.DeorbitSlice;
import dev.leeeonidys.aeronauticsplus.space.core.EarthRotationSlice;
import dev.leeeonidys.aeronauticsplus.space.core.GimbalSlice;
import dev.leeeonidys.aeronauticsplus.space.core.HohmannSlice;
import dev.leeeonidys.aeronauticsplus.space.core.InclinationSlice;
import dev.leeeonidys.aeronauticsplus.space.core.LandingLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.FairingLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.ServiceLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.DockCaptureLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.DockingLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.StationLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.MapViewSlice;
import dev.leeeonidys.aeronauticsplus.space.core.SystemMapSlice;
import dev.leeeonidys.aeronauticsplus.space.core.MapAirSlice;
import dev.leeeonidys.aeronauticsplus.space.core.ManeuverPreviewSlice;
import dev.leeeonidys.aeronauticsplus.space.core.AeroSlice;
import dev.leeeonidys.aeronauticsplus.space.core.LunarTimeSlice;
import dev.leeeonidys.aeronauticsplus.space.core.TerrainSlice;
import dev.leeeonidys.aeronauticsplus.space.core.SolarSystemSlice;
import dev.leeeonidys.aeronauticsplus.space.core.BodyWorldSlice;
import dev.leeeonidys.aeronauticsplus.space.core.AsteroidBeltSlice;
import dev.leeeonidys.aeronauticsplus.space.core.PatchedConicsSlice;
import dev.leeeonidys.aeronauticsplus.space.core.LandingPadSlice;
import dev.leeeonidys.aeronauticsplus.space.core.LandingSlice;
import dev.leeeonidys.aeronauticsplus.space.core.LookSteerSlice;
import dev.leeeonidys.aeronauticsplus.space.core.LatitudeSlice;
import dev.leeeonidys.aeronauticsplus.space.core.ThrottleSlice;
import dev.leeeonidys.aeronauticsplus.space.core.FlightReadoutSlice;
import dev.leeeonidys.aeronauticsplus.space.core.HoldWarpSlice;
import dev.leeeonidys.aeronauticsplus.space.core.CrewStageSlice;
import dev.leeeonidys.aeronauticsplus.space.core.HandoffSlice;
import dev.leeeonidys.aeronauticsplus.space.core.EntitySlice;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.PyroLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.RadialLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.StrapOnSlice;
import dev.leeeonidys.aeronauticsplus.space.world.CrewCommandPayload;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapControlPayload;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapRequestPayload;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapSnapshotPayload;
import dev.leeeonidys.aeronauticsplus.space.world.StationEnterPayload;
import dev.leeeonidys.aeronauticsplus.space.world.MarsChunkGenerator;
import dev.leeeonidys.aeronauticsplus.space.world.MoonChunkGenerator;
import dev.leeeonidys.aeronauticsplus.space.world.AsteroidChunkGenerator;
import dev.leeeonidys.aeronauticsplus.space.world.AsteroidCollisionEvents;
import dev.leeeonidys.aeronauticsplus.space.world.AsteroidWorld;
import dev.leeeonidys.aeronauticsplus.space.world.VesselEntity;
import dev.leeeonidys.aeronauticsplus.space.core.GravityTurnSlice;
import dev.leeeonidys.aeronauticsplus.space.core.StagingSlice;
import dev.leeeonidys.aeronauticsplus.space.core.FailureSlice;
import dev.leeeonidys.aeronauticsplus.space.core.MissionSlice;
import dev.leeeonidys.aeronauticsplus.space.core.MixtureSlice;
import dev.leeeonidys.aeronauticsplus.space.core.SpaceCoreSlice;
import dev.leeeonidys.aeronauticsplus.space.core.SpinSlice;
import dev.leeeonidys.aeronauticsplus.space.core.TorqueSlice;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
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
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, MODID);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<MoonChunkGenerator>> MOON_CHUNK_GENERATOR =
            CHUNK_GENERATORS.register("moon", () -> MoonChunkGenerator.CODEC);
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<AsteroidChunkGenerator>> ASTEROID_BELT_CHUNK_GENERATOR =
            CHUNK_GENERATORS.register("asteroid_belt", () -> AsteroidChunkGenerator.CODEC);
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<MarsChunkGenerator>> MARS_CHUNK_GENERATOR =
            CHUNK_GENERATORS.register("mars", () -> MarsChunkGenerator.CODEC);

    public static final DeferredHolder<EntityType<?>, EntityType<VesselEntity>> VESSEL =
            ENTITIES.register("vessel", () -> EntityType.Builder.<VesselEntity>of(VesselEntity::new, MobCategory.MISC)
                    .sized(1.5f, 6.0f)
                    .clientTrackingRange(256)
                    .updateInterval(1)
                    .fireImmune()
                    .build("aeronauticsplus:vessel"));

    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT =
            ENTITIES.register("control_seat", () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.6f, 0.6f)
                    .clientTrackingRange(8)
                    .updateInterval(20)
                    .noSave()
                    .fireImmune()
                    .build("aeronauticsplus:control_seat"));

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

    /** Create-train fixture. Hull and tanks come from ChemMod. */
    public static final DeferredBlock<VesselPartBlock> ROCKET_MOUNT =
            registerVesselPart(VesselPartCatalog.MOUNT, MapColor.COLOR_YELLOW);
    public static final DeferredItem<BlockItem> ROCKET_MOUNT_ITEM =
            ITEMS.registerSimpleBlockItem(ROCKET_MOUNT);
    /** Dynamics. Not a ChemMod tank. */
    public static final DeferredBlock<VesselPartBlock> ROCKET_ENGINE =
            registerVesselPart(VesselPartCatalog.ENGINE, MapColor.COLOR_ORANGE);
    public static final DeferredItem<BlockItem> ROCKET_ENGINE_ITEM =
            ITEMS.registerSimpleBlockItem(ROCKET_ENGINE);
    /** Seat that marks the pile as a rocket. */
    public static final DeferredBlock<VesselPartBlock> ROCKET_CONTROLLER =
            registerVesselPart(VesselPartCatalog.CONTROLLER, MapColor.COLOR_BLACK);
    public static final DeferredItem<BlockItem> ROCKET_CONTROLLER_ITEM =
            ITEMS.registerSimpleBlockItem(ROCKET_CONTROLLER);
    /** Ground table with hold-down clamps. Does not fly. */
    public static final DeferredBlock<VesselPartBlock> LAUNCH_PAD =
            registerVesselPart(VesselPartCatalog.PAD, MapColor.STONE);
    public static final DeferredItem<BlockItem> LAUNCH_PAD_ITEM =
            ITEMS.registerSimpleBlockItem(LAUNCH_PAD);
    /** KSP-class pyro clamp ring. Cuts stages; stays on the booster. */
    public static final DeferredBlock<VesselPartBlock> STAGE_SEPARATOR =
            registerVesselPart(VesselPartCatalog.SEPARATOR, MapColor.METAL);
    public static final DeferredItem<BlockItem> STAGE_SEPARATOR_ITEM =
            ITEMS.registerSimpleBlockItem(STAGE_SEPARATOR);
    /** Falcon landing legs. Soft contact without them is NO_LEGS. */
    public static final DeferredBlock<VesselPartBlock> LANDING_LEGS =
            registerVesselPart(VesselPartCatalog.LEGS, MapColor.COLOR_LIGHT_GRAY);
    public static final DeferredItem<BlockItem> LANDING_LEGS_ITEM =
            ITEMS.registerSimpleBlockItem(LANDING_LEGS);
    /** Ogive covering the payload. Drops at 50 km by altitude, not ChemMod q. */
    public static final DeferredBlock<VesselPartBlock> ROCKET_FAIRING =
            registerVesselPart(VesselPartCatalog.FAIRING, MapColor.WOOL);
    public static final DeferredItem<BlockItem> ROCKET_FAIRING_ITEM =
            ITEMS.registerSimpleBlockItem(ROCKET_FAIRING);
    /** RCS cluster. ACS, not the main engine. */
    public static final DeferredBlock<VesselPartBlock> RCS_THRUSTER =
            registerVesselPart(VesselPartCatalog.RCS, MapColor.COLOR_RED);
    public static final DeferredItem<BlockItem> RCS_THRUSTER_ITEM =
            ITEMS.registerSimpleBlockItem(RCS_THRUSTER);
    /** APAS/IDSS capture ring. Two ports looking at each other share one link. */
    public static final DeferredBlock<VesselPartBlock> DOCKING_PORT =
            registerVesselPart(VesselPartCatalog.DOCKING, MapColor.METAL);
    public static final DeferredItem<BlockItem> DOCKING_PORT_ITEM =
            ITEMS.registerSimpleBlockItem(DOCKING_PORT);

    /** Walkable Moon dust. AP id; composition waits on ChemMod. Not a ChemMod class. */
    public static final DeferredBlock<Block> MOON_REGOLITH =
            BLOCKS.register("moon_regolith", () -> new Block(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.SNOW)
                            .strength(0.5f)
                            .sound(SoundType.SAND)));
    public static final DeferredItem<BlockItem> MOON_REGOLITH_ITEM =
            ITEMS.registerSimpleBlockItem(MOON_REGOLITH);

    /** Walkable Mars dust. AP id; the thin-air column stays {@code SolarSystem.marsAir()}. */
    public static final DeferredBlock<Block> MARS_REGOLITH =
            BLOCKS.register("mars_regolith", () -> new Block(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_ORANGE)
                            .strength(0.5f)
                            .sound(SoundType.SAND)));
    public static final DeferredItem<BlockItem> MARS_REGOLITH_ITEM =
            ITEMS.registerSimpleBlockItem(MARS_REGOLITH);

    /** Cryo capsule: skips a lunar night and anchors respawn. No life support, that is ChemMod. */
    public static final DeferredBlock<CryoCapsuleBlock> CRYO_CAPSULE =
            BLOCKS.register("cryo_capsule", () -> new CryoCapsuleBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(2.5f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .noOcclusion()));
    public static final DeferredItem<BlockItem> CRYO_CAPSULE_ITEM =
            ITEMS.registerSimpleBlockItem(CRYO_CAPSULE);

    /** Mineable asteroid rock. AP id; not ChemMod ore. */
    public static final DeferredBlock<Block> ASTEROID_ROCK =
            BLOCKS.register("asteroid_rock", () -> new Block(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(1.5f, 6.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredItem<BlockItem> ASTEROID_ROCK_ITEM =
            ITEMS.registerSimpleBlockItem(ASTEROID_ROCK);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.aeronauticsplus"))
                    .icon(() -> new ItemStack(ALUMINUM_THREE_BLADE_PROPELLER.item().get()))
                    .displayItems((params, output) -> {
                        output.accept(PROPELLER_SHAFT_ADAPTER_ITEM.get());
                        output.accept(PROTOTYPE_PROPELLER_ITEM.get());
                        AIRCRAFT_PROPELLERS.forEach(entry -> output.accept(entry.item().get()));
                        output.accept(ROCKET_MOUNT_ITEM.get());
                        output.accept(LAUNCH_PAD_ITEM.get());
                        output.accept(ROCKET_ENGINE_ITEM.get());
                        output.accept(STAGE_SEPARATOR_ITEM.get());
                        output.accept(LANDING_LEGS_ITEM.get());
                        output.accept(ROCKET_FAIRING_ITEM.get());
                        output.accept(RCS_THRUSTER_ITEM.get());
                        output.accept(DOCKING_PORT_ITEM.get());
                        output.accept(ROCKET_CONTROLLER_ITEM.get());
                        output.accept(MOON_REGOLITH_ITEM.get());
                        output.accept(MARS_REGOLITH_ITEM.get());
                        output.accept(CRYO_CAPSULE_ITEM.get());
                        output.accept(ASTEROID_ROCK_ITEM.get());
                    })
                    .build());

    public AeronauticsPlus(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        TABS.register(modEventBus);
        ENTITIES.register(modEventBus);
        CHUNK_GENERATORS.register(modEventBus);
        modEventBus.addListener(AeronauticsPlus::onCommonSetup);
        modEventBus.addListener(AeronauticsPlus::registerPayloads);
        LOGGER.info("Aeronautics Plus 0.2.2-test.114: global solar-system map with body-local views.");
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("6");
        registrar.playToServer(
                OrbitMapControlPayload.TYPE,
                OrbitMapControlPayload.STREAM_CODEC,
                OrbitMapControlPayload::handle);
        registrar.playToServer(
                OrbitMapRequestPayload.TYPE,
                OrbitMapRequestPayload.STREAM_CODEC,
                OrbitMapRequestPayload::handle);
        registrar.playToServer(
                StationEnterPayload.TYPE,
                StationEnterPayload.STREAM_CODEC,
                StationEnterPayload::handle);
        registrar.playToServer(
                CrewCommandPayload.TYPE,
                CrewCommandPayload.STREAM_CODEC,
                CrewCommandPayload::handle);
        registrar.playToClient(
                OrbitMapSnapshotPayload.TYPE,
                OrbitMapSnapshotPayload.STREAM_CODEC,
                OrbitMapSnapshotPayload::handle);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CompatibilityManager.initialize();
            try {
                SpaceCoreSlice.Result slice = SpaceCoreSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus space core slice OK: boosterDeltaV={} m/s, remainingStages={}, diagnosticsPreview={}",
                        String.format(java.util.Locale.ROOT, "%.1f", slice.boosterDeltaVMetersPerSecond()),
                        slice.afterSeparation().stages().size(),
                        slice.brokenDiagnosticText().replace("\n", " | "));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus space core slice failed", exception);
            }
            try {
                VesselCompileSlice.Result compiled = VesselCompileSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus vessel compiler slice OK: components={}, stages={}, structural={}, fuel={}, separation={}",
                        compiled.componentCount(),
                        compiled.compilation().stages().size(),
                        compiled.structuralLinks(),
                        compiled.fuelLinks(),
                        compiled.separationLinks());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus vessel compiler slice failed", exception);
            }
            try {
                SplitTankSlice.Result split = SplitTankSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus split-tank slice OK: sideBySide={}, common={}, fuel={}, oxidizer={}, dropped={}",
                        split.sideBySideLaunchable(),
                        split.commonBulkheadLaunchable(),
                        split.fuelLinks(),
                        split.oxidizerLinks(),
                        String.format(java.util.Locale.ROOT, "%.3f", split.massDroppedKg()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus split-tank slice failed", exception);
            }
            try {
                MissionSlice.Result mission = MissionSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus mission map OK: plan={}, remainingStages={}, energyGain={}",
                        mission.planText(),
                        mission.remainingStages(),
                        String.format(java.util.Locale.ROOT, "%.3e", mission.energyGain()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus mission map slice failed", exception);
            }
            try {
                FailureSlice.Result faults = FailureSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus failure slice OK: dryTank={}, zeroThrust={}, feedBreakElapsed={}",
                        faults.dryTank().split(":", 2)[0],
                        faults.zeroThrust().split(":", 2)[0],
                        String.format(java.util.Locale.ROOT, "%.3f", faults.feedBreakElapsed()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus failure slice failed", exception);
            }
            try {
                AscentSlice.Result ascent = AscentSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus ascent slice OK: padTW={}, alt={}, dragLoss={}, maxQ={}, heldDown={}, fairingBlocked={}",
                        String.format(java.util.Locale.ROOT, "%.3f", ascent.padThrustToWeight()),
                        String.format(java.util.Locale.ROOT, "%.1f", ascent.altitudeMeters()),
                        String.format(java.util.Locale.ROOT, "%.1f", ascent.dragAltitudeLossMeters()),
                        String.format(java.util.Locale.ROOT, "%.1f", ascent.peakDynamicPressurePascals()),
                        ascent.heldDown(),
                        ascent.fairingBlockedOnPad());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus ascent slice failed", exception);
            }
            try {
                GravityTurnSlice.Result turn = GravityTurnSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus gravity-turn slice OK: verticalHoriz={}, turnedHoriz={}, fpa={}, alt={}",
                        String.format(java.util.Locale.ROOT, "%.1f", turn.verticalHorizontal()),
                        String.format(java.util.Locale.ROOT, "%.1f", turn.turnedHorizontal()),
                        String.format(java.util.Locale.ROOT, "%.1f", turn.turnedFlightPathDegrees()),
                        String.format(java.util.Locale.ROOT, "%.1f", turn.turnedAltitude()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus gravity-turn slice failed", exception);
            }
            try {
                LookSteerSlice.Result look = LookSteerSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus look-steer slice OK: skyRadial={}, horizonOff={}, verticalHoriz={}, pitchedHoriz={}",
                        look.skyIsRadial(),
                        look.horizonIsOffRadial(),
                        String.format(java.util.Locale.ROOT, "%.1f", look.verticalHorizontal()),
                        String.format(java.util.Locale.ROOT, "%.1f", look.pitchedHorizontal()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus look-steer slice failed", exception);
            }
            try {
                LatitudeSlice.Result latitude = LatitudeSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus latitude slice OK: equatorEast={}, midEast={}, poleEast={}, spawnDeg={}, equatorDeg={}",
                        String.format(java.util.Locale.ROOT, "%.1f", latitude.equatorEast()),
                        String.format(java.util.Locale.ROOT, "%.1f", latitude.midEast()),
                        String.format(java.util.Locale.ROOT, "%.1f", latitude.poleEast()),
                        String.format(java.util.Locale.ROOT, "%.1f", latitude.spawnDegrees()),
                        String.format(java.util.Locale.ROOT, "%.1f", latitude.equatorDegrees()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus latitude slice failed", exception);
            }
            try {
                ThrottleSlice.Result throttle = ThrottleSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus throttle slice OK: risenAlt={}, cutProp={}, fullProp={}, cutAlt={}, fullAlt={}",
                        String.format(java.util.Locale.ROOT, "%.1f", throttle.risenAltitude()),
                        String.format(java.util.Locale.ROOT, "%.1f", throttle.cutPropellantKg()),
                        String.format(java.util.Locale.ROOT, "%.1f", throttle.fullPropellantKg()),
                        String.format(java.util.Locale.ROOT, "%.1f", throttle.cutAltitude()),
                        String.format(java.util.Locale.ROOT, "%.1f", throttle.fullAltitude()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus throttle slice failed", exception);
            }
            try {
                CrewStageSlice.Result crewStage = CrewStageSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus crew-stage slice OK: boosterProp={}, upperProp={}, upperStages={}",
                        String.format(java.util.Locale.ROOT, "%.1f", crewStage.boosterPropellantKg()),
                        String.format(java.util.Locale.ROOT, "%.1f", crewStage.upperPropellantKg()),
                        crewStage.upperStages());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus crew-stage slice failed", exception);
            }
            try {
                FlightReadoutSlice.Result readout = FlightReadoutSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus flight-readout slice OK: altitude={}, throttle={}",
                        readout.altitude(),
                        readout.throttle());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus flight-readout slice failed", exception);
            }
            try {
                HoldWarpSlice.Result holdWarp = HoldWarpSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus hold-warp slice OK: progradeDot={}, retroDot={}, coastWarp={}, thrustWarp={}",
                        String.format(java.util.Locale.ROOT, "%.3f", holdWarp.progradeDot()),
                        String.format(java.util.Locale.ROOT, "%.3f", holdWarp.retroDot()),
                        holdWarp.coastWarp(),
                        holdWarp.thrustWarp());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus hold-warp slice failed", exception);
            }
            try {
                StagingSlice.Result staging = StagingSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus staging slice OK: stagesLeft={}, stagedAlt={}, stuckAlt={}, stuckDry={}",
                        staging.stagedStagesLeft(),
                        String.format(java.util.Locale.ROOT, "%.1f", staging.stagedAltitude()),
                        String.format(java.util.Locale.ROOT, "%.1f", staging.stuckAltitude()),
                        staging.stuckDry());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus staging slice failed", exception);
            }
            try {
                CircularizationSlice.Result circ = CircularizationSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus circularization slice OK: startE={}, apoAlt={}, circE={}, circAlt={}",
                        String.format(java.util.Locale.ROOT, "%.4f", circ.startEccentricity()),
                        String.format(java.util.Locale.ROOT, "%.1f", circ.apoapsisAltitude()),
                        String.format(java.util.Locale.ROOT, "%.4f", circ.circularEccentricity()),
                        String.format(java.util.Locale.ROOT, "%.1f", circ.circularAltitude()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus circularization slice failed", exception);
            }
            try {
                HohmannSlice.Result hohmann = HohmannSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus Hohmann slice OK: parkAlt={}, transferE={}, apoAlt={}, circAlt={}, circE={}",
                        String.format(java.util.Locale.ROOT, "%.1f", hohmann.parkingAltitude()),
                        String.format(java.util.Locale.ROOT, "%.4f", hohmann.transferEccentricity()),
                        String.format(java.util.Locale.ROOT, "%.1f", hohmann.transferApoapsisAltitude()),
                        String.format(java.util.Locale.ROOT, "%.1f", hohmann.circularAltitude()),
                        String.format(java.util.Locale.ROOT, "%.4f", hohmann.circularEccentricity()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus Hohmann slice failed", exception);
            }
            try {
                DeorbitSlice.Result deorbit = DeorbitSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus deorbit slice OK: parkAlt={}, e={}, periAlt={}, coastAlt={}",
                        String.format(java.util.Locale.ROOT, "%.1f", deorbit.parkingAltitude()),
                        String.format(java.util.Locale.ROOT, "%.4f", deorbit.deorbitEccentricity()),
                        String.format(java.util.Locale.ROOT, "%.1f", deorbit.periapsisAltitude()),
                        String.format(java.util.Locale.ROOT, "%.1f", deorbit.coastAltitude()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus deorbit slice failed", exception);
            }
            try {
                InclinationSlice.Result inclination = InclinationSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus inclination slice OK: startI={}, turnedI={}, alt={}, dVmag={}",
                        String.format(java.util.Locale.ROOT, "%.2f", inclination.startInclinationDegrees()),
                        String.format(java.util.Locale.ROOT, "%.2f", inclination.turnedInclinationDegrees()),
                        String.format(java.util.Locale.ROOT, "%.1f", inclination.altitudeMeters()),
                        String.format(java.util.Locale.ROOT, "%.4f", inclination.speedChange()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus inclination slice failed", exception);
            }
            try {
                EarthRotationSlice.Result spin = EarthRotationSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus Earth-rotation slice OK: inertialHoriz={}, padHoriz={}, flownHoriz={}, alt={}",
                        String.format(java.util.Locale.ROOT, "%.2f", spin.inertialHorizontal()),
                        String.format(java.util.Locale.ROOT, "%.1f", spin.rotatingHorizontal()),
                        String.format(java.util.Locale.ROOT, "%.1f", spin.flownHorizontal()),
                        String.format(java.util.Locale.ROOT, "%.1f", spin.flownAltitude()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus Earth-rotation slice failed", exception);
            }
            try {
                LandingSlice.Result landing = LandingSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus landing slice OK: hopper={}, deadImpact={}, noLegs={}, booster={}, v={}, upperStages={}",
                        landing.hopperLanded(),
                        landing.deadImpact(),
                        landing.noLegs(),
                        landing.boosterLanded(),
                        String.format(java.util.Locale.ROOT, "%.2f", landing.touchdownSpeed()),
                        landing.upperStages());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus landing slice failed", exception);
            }
            try {
                BoostbackSlice.Result boostback = BoostbackSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus boostback slice OK: startHoriz={}, after={}, directImpact={}, recovered={}",
                        String.format(java.util.Locale.ROOT, "%.1f", boostback.startHorizontal()),
                        String.format(java.util.Locale.ROOT, "%.1f", boostback.afterBoostback()),
                        boostback.directImpact(),
                        boostback.recovered());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus boostback slice failed", exception);
            }
            try {
                LandingPadSlice.Result padLand = LandingPadSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus landing-pad slice OK: lz={}, missedAsds={}, missM={}",
                        padLand.hitLz(),
                        padLand.missedAsds(),
                        String.format(java.util.Locale.ROOT, "%.0f", padLand.missMeters()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus landing-pad slice failed", exception);
            }
            try {
                HandoffSlice.Result handoff = HandoffSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus handoff slice OK: padWorld={}, hopperWorld={}, edgeMap={}, parkingMap={}, reentryWorld={}",
                        handoff.padInWorld(),
                        handoff.hopperInWorld(),
                        handoff.edgeOnMap(),
                        handoff.parkingOnMap(),
                        handoff.reentryInWorld());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus handoff slice failed", exception);
            }
            try {
                StrapOnSlice.Result straps = StrapOnSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus strap-on slice OK: clusterT={}, coreT={}, sides={}, upperWait={}, sameOrbit={}",
                        String.format(java.util.Locale.ROOT, "%.0f", straps.clusterThrust()),
                        String.format(java.util.Locale.ROOT, "%.0f", straps.coreThrust()),
                        straps.sides(),
                        straps.upperUntouched(),
                        straps.sidesShareOrbit());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus strap-on slice failed", exception);
            }
            try {
                FlightLoopSlice.Result flight = FlightLoopSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus flight-loop slice OK: leftPad={}, padStayed={}, edgeMap={}, reentryEntity={}, recovered={}, pyroOnMap={}, parts={}",
                        flight.leftPad(),
                        flight.padStayed(),
                        flight.edgeOnMap(),
                        flight.reentryEntity(),
                        flight.recoveredOnPad(),
                        flight.pyroOnMap(),
                        flight.recoveredParts());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus flight-loop slice failed", exception);
            }
            try {
                EntitySlice.Result entity = EntitySlice.execute();
                LOGGER.info(
                        "Aeronautics Plus entity slice OK: afterLiftoff={}, climbed={}, alt={}, map={}, reentry={}, recovered={}, unmannedMaps={}",
                        entity.entityAfterLiftoff(),
                        entity.heldThenClimbed(),
                        String.format(java.util.Locale.ROOT, "%.1f", entity.climbAltitude()),
                        entity.mapAtTwentyKm(),
                        entity.reentryEntity(),
                        entity.recoveredOnPad(),
                        entity.unmannedMapsAtTwenty());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus entity slice failed", exception);
            }
            try {
                PyroLoopSlice.Result pyro = PyroLoopSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus pyro-loop slice OK: world={}, map={}, boosterEngine={}, upperSeat={}, recovered={}, parts={}",
                        pyro.pyroInWorld(),
                        pyro.pyroOnMap(),
                        pyro.boosterHasEngine(),
                        pyro.upperHasSeat(),
                        pyro.recoveredOnPad(),
                        pyro.recoveredParts());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus pyro-loop slice failed", exception);
            }
            try {
                RadialLoopSlice.Result radialFlight = RadialLoopSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus radial-loop slice OK: sides={}, world={}, map={}, seat={}, boosters={}, recovered={}",
                        radialFlight.sideCount(),
                        radialFlight.radialInWorld(),
                        radialFlight.radialOnMap(),
                        radialFlight.coreKeepsSeat(),
                        radialFlight.sidesAreBoosters(),
                        radialFlight.recoveredOnPad());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus radial-loop slice failed", exception);
            }
            try {
                LandingLoopSlice.Result legs = LandingLoopSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus landing-loop slice OK: compilerLegs={}, hopper={}, legless={}, recovered={}",
                        legs.compilerLegs(),
                        legs.hopperLanded(),
                        legs.leglessNotLanding(),
                        legs.recoveredOnPad());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus landing-loop slice failed", exception);
            }
            try {
                BoostbackLoopSlice.Result boostLoop = BoostbackLoopSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus boostback-loop slice OK: legs={}, skipImpact={}, killed={}, hopper={}, recovered={}",
                        boostLoop.compilerLegs(),
                        boostLoop.skippedImpact(),
                        boostLoop.killedDownrange(),
                        boostLoop.hopperLanded(),
                        boostLoop.recoveredOnPad());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus boostback-loop slice failed", exception);
            }
            try {
                FairingLoopSlice.Result fairing = FairingLoopSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus fairing-loop slice OK: compiler={}, handoff={}, held50={}, cmd={}, orbit={}, payload={}, recovered={}, mapKeeps={}",
                        fairing.compilerShroud(),
                        fairing.heldAtHandoff(),
                        fairing.heldAtFifty(),
                        fairing.droppedOnCommand(),
                        fairing.droppedOnOrbit(),
                        fairing.payloadStayed(),
                        fairing.recoveredWithoutFairing(),
                        fairing.mapKeepsFairing());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus fairing-loop slice failed", exception);
            }
            try {
                ServiceLoopSlice.Result service = ServiceLoopSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus service-loop slice OK: compiler={}, rcs={}, refused={}, habitat={}, recovered={}",
                        service.compilerBus(),
                        service.rcsFired(),
                        service.noRcsRefused(),
                        service.habitatStayed(),
                        service.recoveredBus());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus service-loop slice failed", exception);
            }
            try {
                DockingLoopSlice.Result dock = DockingLoopSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus docking-loop slice OK: compiler={}, ports={}, stayed={}, recovered={}",
                        dock.compilerBus(),
                        dock.facingPorts(),
                        dock.stayedAfterFairing(),
                        dock.recovered());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus docking-loop slice failed", exception);
            }
            try {
                StationLoopSlice.Result station = StationLoopSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus station-loop slice OK: compiler={}, staysMap={}, occupy={}, transfer={}, hopper={}, booster={}, left={}, noPad={}, habitat={}, islands={}",
                        station.compilerBus(),
                        station.parkedStaysOnMap(),
                        station.parkedOccupies(),
                        station.transferRefused(),
                        station.hopperRefused(),
                        station.boosterRefused(),
                        station.leftToMap(),
                        station.noPad(),
                        station.habitatWalkable(),
                        station.islandStride());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus station-loop slice failed", exception);
            }
            try {
                MapViewSlice.Result mapView = MapViewSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus map-view slice OK: padR={}, hopperR={}, handoffR={}, parkingR={}, hopperWorld={}, parkingMap={}",
                        String.format(java.util.Locale.ROOT, "%.3f", mapView.padRadius()),
                        String.format(java.util.Locale.ROOT, "%.3f", mapView.hopperRadius()),
                        String.format(java.util.Locale.ROOT, "%.3f", mapView.handoffRadius()),
                        String.format(java.util.Locale.ROOT, "%.3f", mapView.parkingRadius()),
                        mapView.hopperInWorld(),
                        mapView.parkingOnMap());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus map-view slice failed", exception);
            }
            try {
                SystemMapSlice.Result systemMap = SystemMapSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus system-map slice OK: Earth={} AU, Neptune={} AU, Moon={} km, outerRadius={}",
                        String.format(java.util.Locale.ROOT, "%.3f", systemMap.earthDistanceAu()),
                        String.format(java.util.Locale.ROOT, "%.2f", systemMap.neptuneDistanceAu()),
                        String.format(java.util.Locale.ROOT, "%.0f", systemMap.moonDistanceKm()),
                        String.format(java.util.Locale.ROOT, "%.3f", systemMap.outerProjectionRadius()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus system-map slice failed", exception);
            }
            try {
                MapAirSlice.Result mapAir = MapAirSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus map-air slice OK: air={}, vac={}, parkingdV={}, fuelHeld={}",
                        String.format(java.util.Locale.ROOT, "%.1f", mapAir.airSpeed()),
                        String.format(java.util.Locale.ROOT, "%.1f", mapAir.vacuumSpeed()),
                        String.format(java.util.Locale.ROOT, "%.4f", mapAir.parkingDelta()),
                        mapAir.fuelHeld());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus map-air slice failed", exception);
            }
            try {
                ManeuverPreviewSlice.Result preview = ManeuverPreviewSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus maneuver-preview slice OK: circularError={}m, raisedAp={}m, escape={}",
                        String.format(java.util.Locale.ROOT, "%.6f", preview.circularErrorMeters()),
                        String.format(java.util.Locale.ROOT, "%.1f", preview.raisedApoapsisMeters()),
                        preview.escapePredicted());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus maneuver-preview slice failed", exception);
            }
            try {
                AeroSlice.Result aero = AeroSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus aero slice OK: Cd0={}, Cdtrans={}, Cdhyp={}, T11={}, T25={}",
                        String.format(java.util.Locale.ROOT, "%.2f", aero.subsonicCd()),
                        String.format(java.util.Locale.ROOT, "%.2f", aero.transonicCd()),
                        String.format(java.util.Locale.ROOT, "%.2f", aero.hypersonicCd()),
                        String.format(java.util.Locale.ROOT, "%.1f", aero.t11()),
                        String.format(java.util.Locale.ROOT, "%.1f", aero.t25()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus aero slice failed", exception);
            }
            try {
                SolarSystemSlice.Result system = SolarSystemSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus solar-system slice OK: planets={}, majors={}, minors={}, belt={}, moonP={}h, marsQ={}, venusRetro={}",
                        system.planets(),
                        system.majorMoons(),
                        system.minorMoons(),
                        system.beltNamed(),
                        String.format(java.util.Locale.ROOT, "%.2f", system.moonPeriodHours()),
                        String.format(java.util.Locale.ROOT, "%.1f", system.marsQ()),
                        system.venusRetro());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus solar-system slice failed", exception);
            }
            try {
                BodyWorldSlice.Result worlds = BodyWorldSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus body-world slice OK: moonWorld={}, deimosMap={}, moonG={}, marsWorld={}, marsG={}",
                        worlds.moonWorld(),
                        worlds.deimosMap(),
                        String.format(java.util.Locale.ROOT, "%.2f", worlds.moonG()),
                        worlds.marsWorld(),
                        String.format(java.util.Locale.ROOT, "%.2f", worlds.marsG()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus body-world slice failed", exception);
            }
            try {
                LunarTimeSlice.Result lunar = LunarTimeSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus lunar clock slice OK: day={} ticks ({} h, {} vanilla days), compression={}, dayAtNoon={}",
                        lunar.lunarDayTicks(),
                        String.format(java.util.Locale.ROOT, "%.2f", lunar.hours()),
                        String.format(java.util.Locale.ROOT, "%.0f", lunar.days()),
                        String.format(java.util.Locale.ROOT, "%.2f", lunar.compression()),
                        lunar.dayAtNoon());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus lunar clock slice failed", exception);
            }
            try {
                TerrainSlice.Result terrain = TerrainSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus terrain slice OK: samples={}, moonRelief={}, marsRelief={}, repeatRatio={}, canyon={}, volcano={}",
                        terrain.samples(),
                        String.format(java.util.Locale.ROOT, "%.1f", terrain.moonRelief()),
                        String.format(java.util.Locale.ROOT, "%.1f", terrain.marsRelief()),
                        String.format(java.util.Locale.ROOT, "%.2f", terrain.repeatRatio()),
                        String.format(java.util.Locale.ROOT, "%.1f", terrain.canyonDepth()),
                        String.format(java.util.Locale.ROOT, "%.1f", terrain.volcanoRise()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus terrain slice failed", exception);
            }
            try {
                AsteroidBeltSlice.Result belt = AsteroidBeltSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus asteroid belt slice OK: bodies={}, belt={}, rock={}",
                        belt.walkableBodies(),
                        belt.beltAnnulus(),
                        belt.rockBlock());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus asteroid belt slice failed", exception);
            }
            try {
                PatchedConicsSlice.Result patched = PatchedConicsSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus patched-conics slice OK: soi={}km, captured={}, escaped={}, parked={}",
                        String.format(java.util.Locale.ROOT, "%.0f", patched.soiKm()),
                        patched.captured(),
                        patched.escaped(),
                        patched.parkedStays());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus patched-conics slice failed", exception);
            }
            try {
                GimbalSlice.Result gimbal = GimbalSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus gimbal slice OK: limited={}, frozen={}, steered={}, fixed={}",
                        String.format(java.util.Locale.ROOT, "%.2f", gimbal.limitedDegrees()),
                        String.format(java.util.Locale.ROOT, "%.3f", gimbal.frozenDegrees()),
                        String.format(java.util.Locale.ROOT, "%.4f", gimbal.steeredAngle()),
                        String.format(java.util.Locale.ROOT, "%.4f", gimbal.fixedAngle()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus gimbal slice failed", exception);
            }
            try {
                AttitudeSlice.Result attitude = AttitudeSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus attitude slice OK: identityDeltaY={}, rolledDeltaX={}, progradeGain={}, identityGain={}",
                        String.format(java.util.Locale.ROOT, "%.3f", attitude.identityDeltaY()),
                        String.format(java.util.Locale.ROOT, "%.3f", attitude.rolledDeltaX()),
                        String.format(java.util.Locale.ROOT, "%.3f", attitude.progradeSpeedGain()),
                        String.format(java.util.Locale.ROOT, "%.3f", attitude.identitySpeedGain()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus attitude slice failed", exception);
            }
            try {
                MixtureSlice.Result mixture = MixtureSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus mixture slice OK: designFuel={}, designOxidizer={}, leftoverFuel={}, wrong={}",
                        String.format(java.util.Locale.ROOT, "%.3f", mixture.designFuelKg()),
                        String.format(java.util.Locale.ROOT, "%.3f", mixture.designOxidizerKg()),
                        String.format(java.util.Locale.ROOT, "%.3f", mixture.leftoverFuelKg()),
                        mixture.wrongPropellant().split(":", 2)[0]);
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus mixture slice failed", exception);
            }
            try {
                OccupancySlice.Result occupancy = OccupancySlice.execute();
                LOGGER.info(
                        "Aeronautics Plus occupancy slice OK: nozzleVolume={}, halfEngineX={}",
                        String.format(java.util.Locale.ROOT, "%.3f", occupancy.nozzleVolume()),
                        String.format(java.util.Locale.ROOT, "%.3f", occupancy.halfEngineX()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus occupancy slice failed", exception);
            }
            try {
                TorqueSlice.Result torque = TorqueSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus torque slice OK: centeredAngle={}, offsetAngle={}, offset={}",
                        String.format(java.util.Locale.ROOT, "%.4f", torque.centeredAngle()),
                        String.format(java.util.Locale.ROOT, "%.4f", torque.offsetAngle()),
                        String.format(java.util.Locale.ROOT, "%.3f", torque.offsetMeters()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus torque slice failed", exception);
            }
            try {
                PackingSlice.Result packing = PackingSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus packing slice OK: packedEngines={}, westX={}, eastX={}",
                        packing.packedEngines(),
                        String.format(java.util.Locale.ROOT, "%.3f", packing.westX()),
                        String.format(java.util.Locale.ROOT, "%.3f", packing.eastX()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus packing slice failed", exception);
            }
            try {
                AssemblySlice.Result assembly = AssemblySlice.execute();
                LOGGER.info(
                        "Aeronautics Plus assembly slice OK: planted={}, craneSeconds={}, midTransitEmpty={}, launchable={}",
                        assembly.plantedComponents(),
                        String.format(java.util.Locale.ROOT, "%.3f", assembly.craneSeconds()),
                        assembly.midTransitEmpty(),
                        assembly.launchable());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus assembly slice failed", exception);
            }
            try {
                DockCaptureSlice.Result capture = DockCaptureSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus dock-capture slice OK: captured={}, links={}, tooFar={}, noPort={}, notFacing={}, closing={}, overlap={}",
                        capture.captured(),
                        capture.dockingLinks(),
                        capture.tooFar(),
                        capture.noPort(),
                        capture.notFacing(),
                        capture.closing(),
                        capture.overlap());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus dock-capture slice failed", exception);
            }
            try {
                DockCaptureLoopSlice.Result docked = DockCaptureLoopSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus dock-capture-loop slice OK: compiler={}, entities={}, parts={}, stillWorld={}",
                        docked.compiler(),
                        docked.entities(),
                        docked.parts(),
                        docked.stillWorld());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus dock-capture-loop slice failed", exception);
            }
            try {
                SpinSlice.Result spin = SpinSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus spin slice OK: burnRate={}, coastRate={}, extraCoastAngle={}, thrustDot={}",
                        String.format(java.util.Locale.ROOT, "%.4f", spin.burnRate()),
                        String.format(java.util.Locale.ROOT, "%.4f", spin.coastRate()),
                        String.format(java.util.Locale.ROOT, "%.4f", spin.extraCoastAngle()),
                        String.format(java.util.Locale.ROOT, "%.3f", spin.thrustDot()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus spin slice failed", exception);
            }
            try {
                SpacecraftSlice.Result craft = SpacecraftSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus spacecraft slice OK: habitat={}, gyro={}, solar={}, spinAfter={}, rcsDrop={}",
                        craft.habitat(),
                        craft.gyro(),
                        craft.solar(),
                        String.format(java.util.Locale.ROOT, "%.4f", craft.spinAfter()),
                        String.format(java.util.Locale.ROOT, "%.3f", craft.rcsMassDropped()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus spacecraft slice failed", exception);
            }
            try {
                DockingSlice.Result dock = DockingSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus docking slice OK: docking={}, battery={}, radiator={}, links={}",
                        dock.docking(),
                        dock.battery(),
                        dock.radiator(),
                        dock.dockingLinks());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus docking slice failed", exception);
            }
            try {
                VarietySlice.Result variety = VarietySlice.execute();
                LOGGER.info(
                        "Aeronautics Plus variety slice OK: small={}, large={}, hydrolox={}, dropped={}",
                        variety.smallLaunchable(),
                        variety.largeLaunchable(),
                        variety.hydroloxLaunchable(),
                        String.format(java.util.Locale.ROOT, "%.3f", variety.hydroloxDroppedKg()));
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus variety slice failed", exception);
            }
            try {
                ElectronicsSlice.Result electronics = ElectronicsSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus electronics slice OK: oxygen={}, transponder={}, complete={}",
                        electronics.oxygen(),
                        electronics.transponder(),
                        electronics.complete());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus electronics slice failed", exception);
            }
            try {
                PayloadSlice.Result payload = PayloadSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus payload slice OK: fairing={}, payload={}, dropped={}, launchable={}",
                        String.format(java.util.Locale.ROOT, "%.1f", payload.fairingMassKg()),
                        String.format(java.util.Locale.ROOT, "%.1f", payload.payloadMassKg()),
                        String.format(java.util.Locale.ROOT, "%.1f", payload.massDroppedKg()),
                        payload.launchable());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus payload slice failed", exception);
            }
            try {
                ControllerSlice.Result control = ControllerSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus controller slice OK: designated={}, unmarked={}",
                        control.designated(),
                        control.unmarked());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus controller slice failed", exception);
            }
            try {
                IntegralFeedSlice.Result feed = IntegralFeedSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus integral-feed slice OK: padStack={}, tankWins={}, disconnected={}",
                        feed.padStackFlies(),
                        feed.tankStillWins(),
                        feed.disconnectedStillFails());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus integral-feed slice failed", exception);
            }
            try {
                PadSlice.Result pad = PadSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus pad slice OK: onPad={}, notOnPad={}, padMassExcluded={}",
                        pad.onPad(),
                        pad.notOnPad(),
                        pad.padMassExcluded());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus pad slice failed", exception);
            }
            try {
                RecoverySlice.Result recovery = RecoverySlice.execute();
                LOGGER.info(
                        "Aeronautics Plus recovery slice OK: pyro={}, boosterEngine={}, upperEngine={}, padGround={}, onPad={}, parts={}",
                        recovery.pyroCut(),
                        recovery.boosterHasEngine(),
                        recovery.upperHasEngine(),
                        recovery.padStayedOnGround(),
                        recovery.planOnPad(),
                        recovery.recoveredParts());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus recovery slice failed", exception);
            }
            try {
                RadialSlice.Result radial = RadialSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus radial slice OK: sides={}, coreSeat={}, sideEngines={}, axial={}, notRadial={}, strapOn={}",
                        radial.sideCount(),
                        radial.coreKeepsController(),
                        radial.sidesHaveEngines(),
                        radial.axialStillCuts(),
                        radial.axialNotRadial(),
                        radial.compilerMarksStrapOns());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus radial slice failed", exception);
            }
            try {
                TransporterSlice.Result haul = TransporterSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus transporter slice OK: diameter={}, height={}, haulSeconds={}, padX={}, launchable={}",
                        String.format(java.util.Locale.ROOT, "%.3f", haul.diameterMeters()),
                        String.format(java.util.Locale.ROOT, "%.3f", haul.heightMeters()),
                        String.format(java.util.Locale.ROOT, "%.3f", haul.haulSeconds()),
                        haul.padOriginX(),
                        haul.launchableAtPad());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus transporter slice failed", exception);
            }
            try {
                TrainMountSlice.Result train = TrainMountSlice.execute();
                LOGGER.info(
                        "Aeronautics Plus train-mount slice OK: bay={}, onTransporter={}, launchable={}, haulSeconds={}, mounts={}, noTreads={}",
                        train.horizontalBay(),
                        train.onTransporter(),
                        train.launchableAfterUnclamp(),
                        String.format(java.util.Locale.ROOT, "%.1f", train.haulSeconds()),
                        train.mountCells(),
                        train.noTreads());
            } catch (RuntimeException exception) {
                LOGGER.error("Aeronautics Plus train-mount slice failed", exception);
            }

            List<String> registeredItems = new ArrayList<>();
            registeredItems.add(BuiltInRegistries.ITEM.getKey(PROPELLER_SHAFT_ADAPTER_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(PROTOTYPE_PROPELLER_ITEM.get()).toString());
            AIRCRAFT_PROPELLERS.forEach(entry ->
                    registeredItems.add(BuiltInRegistries.ITEM.getKey(entry.item().get()).toString()));
            registeredItems.add(BuiltInRegistries.ITEM.getKey(ROCKET_MOUNT_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(LAUNCH_PAD_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(ROCKET_ENGINE_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(STAGE_SEPARATOR_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(LANDING_LEGS_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(ROCKET_FAIRING_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(RCS_THRUSTER_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(DOCKING_PORT_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(ROCKET_CONTROLLER_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(MOON_REGOLITH_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(ASTEROID_ROCK_ITEM.get()).toString());

            boolean allRegistered = registeredItems.size() == 22
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

    private static DeferredBlock<VesselPartBlock> registerVesselPart(VesselPartSpec spec, MapColor color) {
        return BLOCKS.register(spec.id(), () -> new VesselPartBlock(
                spec,
                BlockBehaviour.Properties.of()
                        .mapColor(color)
                        .strength(3.0f, 6.0f)
                        .noOcclusion()));
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
