package dev.leeeonidys.aeronauticsplus.client;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Polls the map key. Bus.GAME matches the rest of AP client/world ticks. */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class OrbitMapTicks {
    private OrbitMapTicks() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        OrbitMapClient.onClientTick();
    }
}
