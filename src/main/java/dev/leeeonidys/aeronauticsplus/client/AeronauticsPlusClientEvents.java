package dev.leeeonidys.aeronauticsplus.client;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/** Client-only registrations for animated propeller block entities. */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class AeronauticsPlusClientEvents {
    private AeronauticsPlusClientEvents() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RuPackUpdater.start();
            OrbitMapClient.bind();
        });
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OrbitMapClient.OPEN_MAP);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        PropellerPartialModels.init();
        event.registerBlockEntityRenderer(
                AeronauticsPlus.PROTOTYPE_PROPELLER_BE.get(),
                AircraftPropellerRenderer::new
        );
        event.registerBlockEntityRenderer(
                AeronauticsPlus.AIRCRAFT_PROPELLER_BE.get(),
                AircraftPropellerRenderer::new
        );
        event.registerBlockEntityRenderer(
                AeronauticsPlus.PROPELLER_SHAFT_ADAPTER_BE.get(),
                PropellerShaftAdapterRenderer::new
        );
        event.registerEntityRenderer(AeronauticsPlus.VESSEL.get(), VesselEntityRenderer::new);
    }
}
