package dev.leeeonidys.aeronauticsplus;

import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import dev.leeeonidys.aeronauticsplus.compat.CompatibilityManager;
import dev.leeeonidys.aeronauticsplus.content.propeller.AircraftPropellerBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.AircraftPropellerBlockEntity;
import dev.leeeonidys.aeronauticsplus.content.propeller.PropellerShaftAdapterBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.PropellerSpec;
import dev.leeeonidys.aeronauticsplus.content.propeller.PrototypePropellerBlock;
import dev.leeeonidys.aeronauticsplus.content.propeller.PrototypePropellerBlockEntity;
import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlock;
import dev.leeeonidys.aeronauticsplus.space.compile.AssemblySlice;
import dev.leeeonidys.aeronauticsplus.space.compile.OccupancySlice;
import dev.leeeonidys.aeronauticsplus.space.compile.PackingSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.PayloadSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.SpacecraftSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.DockingSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.SplitTankSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.TransporterSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.VarietySlice;
import dev.leeeonidys.aeronauticsplus.space.compile.ElectronicsSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.ControllerSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.PadSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.RadialSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.RecoverySlice;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselCompileSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartSpec;
import dev.leeeonidys.aeronauticsplus.space.core.AscentSlice;
import dev.leeeonidys.aeronauticsplus.space.core.AttitudeSlice;
import dev.leeeonidys.aeronauticsplus.space.core.BoostbackSlice;
import dev.leeeonidys.aeronauticsplus.space.core.CircularizationSlice;
import dev.leeeonidys.aeronauticsplus.space.core.DeorbitSlice;
import dev.leeeonidys.aeronauticsplus.space.core.EarthRotationSlice;
import dev.leeeonidys.aeronauticsplus.space.core.GimbalSlice;
import dev.leeeonidys.aeronauticsplus.space.core.HohmannSlice;
import dev.leeeonidys.aeronauticsplus.space.core.InclinationSlice;
import dev.leeeonidys.aeronauticsplus.space.core.LandingPadSlice;
import dev.leeeonidys.aeronauticsplus.space.core.LandingSlice;
import dev.leeeonidys.aeronauticsplus.space.core.HandoffSlice;
import dev.leeeonidys.aeronauticsplus.space.core.EntitySlice;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.PyroLoopSlice;
import dev.leeeonidys.aeronauticsplus.space.core.StrapOnSlice;
import dev.leeeonidys.aeronauticsplus.space.world.VesselEntity;
import dev.leeeonidys.aeronauticsplus.space.core.GravityTurnSlice;
import dev.leeeonidys.aeronauticsplus.space.core.StagingSlice;
import dev.leeeonidys.aeronauticsplus.space.core.FailureSlice;
import dev.leeeonidys.aeronauticsplus.space.core.MissionSlice;
import dev.leeeonidys.aeronauticsplus.space.core.MixtureSlice;
import dev.leeeonidys.aeronauticsplus.space.core.SpaceCoreSlice;
import dev.leeeonidys.aeronauticsplus.space.core.SpinSlice;
import dev.leeeonidys.aeronauticsplus.space.core.TorqueSlice;
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
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<VesselEntity>> VESSEL =
            ENTITIES.register("vessel", () -> EntityType.Builder.<VesselEntity>of(VesselEntity::new, MobCategory.MISC)
                    .sized(1.5f, 6.0f)
                    .clientTrackingRange(80)
                    .updateInterval(1)
                    .fireImmune()
                    .build("aeronauticsplus:vessel"));

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
                        output.accept(ROCKET_CONTROLLER_ITEM.get());
                    })
                    .build());

    public AeronauticsPlus(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        TABS.register(modEventBus);
        ENTITIES.register(modEventBus);
        modEventBus.addListener(AeronauticsPlus::onCommonSetup);
        LOGGER.info("Aeronautics Plus 0.2.2-test.55: in-flight pyro splits the pile; burnout coasts; booster returns as blocks.");
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
                        "Aeronautics Plus entity slice OK: afterLiftoff={}, climbed={}, alt={}, map={}, reentry={}, recovered={}, playerOff={}",
                        entity.entityAfterLiftoff(),
                        entity.heldThenClimbed(),
                        String.format(java.util.Locale.ROOT, "%.1f", entity.climbAltitude()),
                        entity.mapAtTwentyKm(),
                        entity.reentryEntity(),
                        entity.recoveredOnPad(),
                        entity.playerNotOnBoard());
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

            List<String> registeredItems = new ArrayList<>();
            registeredItems.add(BuiltInRegistries.ITEM.getKey(PROPELLER_SHAFT_ADAPTER_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(PROTOTYPE_PROPELLER_ITEM.get()).toString());
            AIRCRAFT_PROPELLERS.forEach(entry ->
                    registeredItems.add(BuiltInRegistries.ITEM.getKey(entry.item().get()).toString()));
            registeredItems.add(BuiltInRegistries.ITEM.getKey(ROCKET_MOUNT_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(LAUNCH_PAD_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(ROCKET_ENGINE_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(STAGE_SEPARATOR_ITEM.get()).toString());
            registeredItems.add(BuiltInRegistries.ITEM.getKey(ROCKET_CONTROLLER_ITEM.get()).toString());

            boolean allRegistered = registeredItems.size() == 16
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
