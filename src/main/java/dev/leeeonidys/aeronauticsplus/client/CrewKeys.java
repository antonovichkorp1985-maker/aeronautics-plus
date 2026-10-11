package dev.leeeonidys.aeronauticsplus.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/**
 * Every crew command is a {@link KeyMapping}. Bindings live in Controls → Aeronautics Plus.
 * Defaults are KSP-ish; none of these keys are read as raw GLFW codes in tick.
 */
public final class CrewKeys {
    public static final String CATEGORY = "key.categories.aeronauticsplus";

    public static final KeyMapping OPEN_MAP = bind("orbit_map", InputConstants.KEY_M);
    public static final KeyMapping ENTER_STATION = bind("enter_station", InputConstants.KEY_N);
    public static final KeyMapping DROP_FAIRING = bind("drop_fairing", InputConstants.KEY_F);
    public static final KeyMapping THROTTLE_CUT = bind("throttle_cut", InputConstants.KEY_X);
    public static final KeyMapping THROTTLE_FULL = bind("throttle_full", InputConstants.KEY_Z);
    public static final KeyMapping STAGE = bind("stage", InputConstants.KEY_G);
    public static final KeyMapping THROTTLE_DOWN = bind("throttle_down", InputConstants.KEY_C);
    public static final KeyMapping THROTTLE_UP = bind("throttle_up", InputConstants.KEY_V);
    public static final KeyMapping RCS = bind("rcs", InputConstants.KEY_R);
    public static final KeyMapping WARP_UP = bind("warp_up", InputConstants.KEY_PERIOD);
    public static final KeyMapping WARP_DOWN = bind("warp_down", InputConstants.KEY_COMMA);
    public static final KeyMapping HOLD = bind("hold", InputConstants.KEY_T);
    public static final KeyMapping UNDOCK = bind("undock", InputConstants.KEY_B);

    /** Register this array. Forgetting a mapping here is how a key becomes hardcoded. */
    public static final KeyMapping[] ALL = {
        OPEN_MAP,
        ENTER_STATION,
        DROP_FAIRING,
        THROTTLE_CUT,
        THROTTLE_FULL,
        STAGE,
        THROTTLE_DOWN,
        THROTTLE_UP,
        RCS,
        WARP_UP,
        WARP_DOWN,
        HOLD,
        UNDOCK
    };

    private CrewKeys() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        for (KeyMapping mapping : ALL) {
            event.register(mapping);
        }
    }

    private static KeyMapping bind(String id, int keyCode) {
        return new KeyMapping(
                "key.aeronauticsplus." + id,
                InputConstants.Type.KEYSYM,
                keyCode,
                CATEGORY);
    }
}
