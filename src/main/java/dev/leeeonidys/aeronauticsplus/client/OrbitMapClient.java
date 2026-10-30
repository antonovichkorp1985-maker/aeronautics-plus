package dev.leeeonidys.aeronauticsplus.client;

import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapTrack;
import dev.leeeonidys.aeronauticsplus.space.core.WorldHandoff;
import dev.leeeonidys.aeronauticsplus.space.world.AsteroidWorld;
import dev.leeeonidys.aeronauticsplus.space.world.CrewCommandPayload;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapClientHooks;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapRequestPayload;
import dev.leeeonidys.aeronauticsplus.space.world.StationEnterPayload;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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

    private static boolean forcedMap = false;
    private static boolean pendingReopen = false;

    private OrbitMapClient() {
    }

    public static void bind() {
        OrbitMapClientHooks.OPEN = OrbitMapClient::open;
    }

    /**
     * Open (or refresh) the map screen. Called from the server snapshot hook and from manual M.
     * {@code forced} means Phase 2 auto-open: Esc/M cannot close it back to the empty world.
     */
    public static void open(List<OrbitMapTrack> tracks) {
        boolean forced = updatePhase(tracks);
        open(tracks, forced);
    }

    public static void open(List<OrbitMapTrack> tracks, boolean forced) {
        if (forced) {
            forcedMap = true;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof OrbitMapScreen) {
            minecraft.setScreen(new OrbitMapScreen(tracks));
            return;
        }
        minecraft.setScreen(new OrbitMapScreen(tracks));
    }

    public static void close() {
        forcedMap = false;
        pendingReopen = false;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof OrbitMapScreen) {
            minecraft.setScreen(null);
        }
    }

    /** Call when the map screen is closed (e.g. Esc). If forced, reopen immediately next tick. */
    public static void onScreenClosed() {
        if (forcedMap) {
            pendingReopen = true;
        }
    }

    public static boolean isForced() {
        return forcedMap;
    }

    public static void onClientTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        if (pendingReopen && minecraft.screen == null) {
            pendingReopen = false;
            PacketDistributor.sendToServer(new OrbitMapRequestPayload());
            return;
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
        if (minecraft.screen == null && CrewKeys.UNDOCK.consumeClick()) {
            PacketDistributor.sendToServer(new CrewCommandPayload(CrewCommandPayload.UNDOCK));
        }

        if (!CrewKeys.OPEN_MAP.consumeClick()) {
            return;
        }

        if (minecraft.screen instanceof OrbitMapScreen) {
            if (!forcedMap) {
                minecraft.setScreen(null);
            }
            return;
        }

        if (minecraft.screen == null) {
            if (isSurfacePhase(minecraft)) {
                minecraft.player.displayClientMessage(
                        Component.translatable("message.aeronauticsplus.orbit_map.surface"), true);
                return;
            }
            PacketDistributor.sendToServer(new OrbitMapRequestPayload());
        }
    }

    /**
     * Phase 1: player is on a body surface / in atmosphere below 20 km and not in orbit.
     * M does not open the map here. Asteroid belt / Ceres are orbital zones — M is allowed.
     */
    private static boolean isSurfacePhase(Minecraft minecraft) {
        if (AsteroidWorld.isBelt(minecraft.level)) {
            return false;
        }
        return minecraft.player.getY() < WorldHandoff.MAP_ALTITUDE_METERS;
    }

    /**
     * Server snapshot received: decide Phase 2 forced-map state from track data.
     * Returns true when the map should be forced open (ascent / sub-orbital, not yet stable).
     */
    public static boolean updatePhase(List<OrbitMapTrack> tracks) {
        boolean anyInOrbit = tracks.stream()
                .filter(track -> track.presence() == FlightPresence.ORBIT_MAP)
                .findFirst().isPresent();
        if (!anyInOrbit) {
            forcedMap = false;
            return false;
        }
        boolean anyStable = tracks.stream()
                .filter(track -> track.presence() == FlightPresence.ORBIT_MAP)
                .anyMatch(track -> track.periapsisAltitudeMeters() >= WorldHandoff.MAP_ALTITUDE_METERS);
        if (anyStable) {
            forcedMap = false;
            return false; // Phase 3: stable orbit / belt, M freely toggles
        }
        forcedMap = true; // Phase 2: forced open, M/Esc cannot close
        return true;
    }
}
