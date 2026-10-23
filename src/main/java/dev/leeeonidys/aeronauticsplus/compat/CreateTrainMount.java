package dev.leeeonidys.aeronauticsplus.compat;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.neoforged.fml.ModList;

/**
 * Optional Create hook. No Create class is imported here — {@link CreateTrainMountHooks}
 * loads only when {@code create} is present. AP does not register treads or extra tracks.
 */
public final class CreateTrainMount {
    public static final String CREATE_MOD_ID = "create";

    private CreateTrainMount() {
    }

    public static boolean present() {
        return ModList.get().isLoaded(CREATE_MOD_ID);
    }

    public static void register() {
        if (!present()) {
            AeronauticsPlus.LOGGER.debug(
                    "Create not detected; rocket_mount stays a world block until a train exists.");
            return;
        }
        CreateTrainMountHooks.register();
        AeronauticsPlus.LOGGER.info(
                "Create detected: rocket_mount rides trains. Pack tracks stay pack tracks — AP adds no treads.");
    }
}
