package dev.leeeonidys.aeronauticsplus.client;

import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapTrack;
import dev.leeeonidys.aeronauticsplus.space.world.CraneCommandPayload;
import dev.leeeonidys.aeronauticsplus.space.world.CrewCommandPayload;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapClientHooks;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapRequestPayload;
import dev.leeeonidys.aeronauticsplus.space.world.StationEnterPayload;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;

/** Crew keys are {@link CrewKeys} — remappable in Controls. Physics keeps running. */
public final class OrbitMapClient {
    public static final KeyMapping OPEN_MAP = CrewKeys.OPEN_MAP;
    public static final KeyMapping ENTER_STATION = CrewKeys.ENTER_STATION;
    public static final KeyMapping DROP_FAIRING = CrewKeys.DROP_FAIRING;
    public static final KeyMapping THROTTLE_CUT = CrewKeys.THROTTLE_CUT;
    public static final KeyMapping THROTTLE_FULL = CrewKeys.THROTTLE_FULL;
    public static final KeyMapping STAGE = CrewKeys.STAGE;
    public static final KeyMapping THROTTLE_DOWN = CrewKeys.THROTTLE_DOWN;
    public static final KeyMapping THROTTLE_UP = CrewKeys.THROTTLE_UP;
    public static final KeyMapping RCS = CrewKeys.RCS;
    public static final KeyMapping WARP_UP = CrewKeys.WARP_UP;
    public static final KeyMapping WARP_DOWN = CrewKeys.WARP_DOWN;
    public static final KeyMapping HOLD = CrewKeys.HOLD;

    private OrbitMapClient() {
    }

    public static void bind() {
        OrbitMapClientHooks.OPEN = OrbitMapClient::open;
    }

    public static void open(List<OrbitMapTrack> tracks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof OrbitMapScreen screen) {
            screen.setTracks(tracks);
            return;
        }
        minecraft.setScreen(new OrbitMapScreen(tracks));
    }

    public static void onClientTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        if (minecraft.screen == null && CrewKeys.CRANE.consumeClick()) {
            PacketDistributor.sendToServer(new CraneCommandPayload());
        }
        if (minecraft.screen == null && CrewKeys.ENTER_STATION.consumeClick()) {
            PacketDistributor.sendToServer(new StationEnterPayload());
        }
        if (minecraft.screen == null && CrewKeys.DROP_FAIRING.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.DROP_FAIRING));
        }
        if (minecraft.screen == null && CrewKeys.THROTTLE_CUT.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.THROTTLE_CUT));
        }
        if (minecraft.screen == null && CrewKeys.THROTTLE_FULL.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.THROTTLE_FULL));
        }
        if (minecraft.screen == null && CrewKeys.STAGE.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.STAGE));
        }
        if (minecraft.screen == null && CrewKeys.THROTTLE_DOWN.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.THROTTLE_DOWN));
        }
        if (minecraft.screen == null && CrewKeys.THROTTLE_UP.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.THROTTLE_UP));
        }
        if (minecraft.screen == null && CrewKeys.RCS.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.RCS));
        }
        if (minecraft.screen == null && CrewKeys.WARP_UP.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.WARP_UP));
        }
        if (minecraft.screen == null && CrewKeys.WARP_DOWN.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.WARP_DOWN));
        }
        if (minecraft.screen == null && CrewKeys.HOLD.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.HOLD));
        }
        if (!CrewKeys.OPEN_MAP.consumeClick()) {
            return;
        }
        if (minecraft.screen instanceof OrbitMapScreen) {
            minecraft.setScreen(null);
            return;
        }
        if (minecraft.screen == null) {
            PacketDistributor.sendToServer(new OrbitMapRequestPayload());
        }
    }
}
