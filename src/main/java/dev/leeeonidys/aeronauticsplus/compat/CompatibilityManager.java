package dev.leeeonidys.aeronauticsplus.compat;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;

/**
 * Entry point for optional cross-mod integrations.
 *
 * <p>The Aeronautics Plus core must never load classes from an optional mod before that
 * mod has been detected. Shared item tags are the first compatibility boundary: recipes
 * depend on Aeronautics Plus tags, while optional modules and modpacks can extend those
 * tags without replacing the recipes.</p>
 */
public final class CompatibilityManager {
    public static final String TERRAFIRMACRAFT_MOD_ID = "tfc";

    public static final TagKey<Item> WOOD_BLADE_MATERIALS = bladeMaterialTag("wood");
    public static final TagKey<Item> IRON_BLADE_MATERIALS = bladeMaterialTag("iron");
    public static final TagKey<Item> ALUMINUM_BLADE_MATERIALS = bladeMaterialTag("aluminum");
    public static final TagKey<Item> STEEL_BLADE_MATERIALS = bladeMaterialTag("steel");

    private CompatibilityManager() {
    }

    /**
     * Detect integrations whose resources are safe to activate without hard API links.
     * Dedicated API adapters will be dispatched from here as they are implemented.
     */
    public static void initialize() {
        if (ModList.get().isLoaded(TERRAFIRMACRAFT_MOD_ID)) {
            AeronauticsPlus.LOGGER.info(
                    "TerraFirmaCraft detected: Aeronautics Plus blade-material tag bridge is active "
                            + "(wood={}, steel={}).",
                    WOOD_BLADE_MATERIALS.location(),
                    STEEL_BLADE_MATERIALS.location());
        } else {
            AeronauticsPlus.LOGGER.debug(
                    "TerraFirmaCraft not detected; standard Aeronautics Plus material tags remain active.");
        }
    }

    private static TagKey<Item> bladeMaterialTag(String material) {
        return TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(
                        AeronauticsPlus.MODID,
                        "blade_materials/" + material));
    }
}
