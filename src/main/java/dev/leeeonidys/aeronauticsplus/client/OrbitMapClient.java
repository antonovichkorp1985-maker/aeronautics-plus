package dev.leeeonidys.aeronauticsplus.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapTrack;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapClientHooks;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapRequestPayload;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;

/** Key M opens the Kepler map. Physics keeps running. */
public final class OrbitMapClient {
    public static final KeyMapping OPEN_MAP = new KeyMapping(
            "key.aeronauticsplus.orbit_map",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_M,
            "key.categories.aeronauticsplus");

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
        if (minecraft.player == null || !OPEN_MAP.consumeClick()) {
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
