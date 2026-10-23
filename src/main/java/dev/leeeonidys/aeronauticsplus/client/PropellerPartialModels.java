package dev.leeeonidys.aeronauticsplus.client;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Rotating OBJ models used by the block-entity renderer.
 *
 * <p>The corresponding blockstate models are particle-only shells. Keeping the
 * complete OBJ here prevents Minecraft's chunk renderer from drawing a static
 * copy underneath the animated propeller.</p>
 */
public final class PropellerPartialModels {
    private static final PartialModel PROTOTYPE = block("prototype_propeller");
    private static final Map<String, PartialModel> SERIAL = Map.ofEntries(
            entry("wooden_two_blade_propeller"),
            entry("wooden_three_blade_propeller"),
            entry("wooden_four_blade_propeller"),
            entry("aluminum_two_blade_propeller"),
            entry("aluminum_three_blade_propeller"),
            entry("aluminum_four_blade_propeller"),
            entry("steel_two_blade_propeller"),
            entry("steel_three_blade_propeller"),
            entry("steel_four_blade_propeller")
    );

    private PropellerPartialModels() {
    }

    public static void init() {
        // Loading this class registers every PartialModel before model baking.
    }

    public static PartialModel forState(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (id == null || !AeronauticsPlus.MODID.equals(id.getNamespace())) {
            return PROTOTYPE;
        }
        return SERIAL.getOrDefault(id.getPath(), PROTOTYPE);
    }

    private static Map.Entry<String, PartialModel> entry(String id) {
        return Map.entry(id, block("propellers/" + id));
    }

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(
                AeronauticsPlus.MODID,
                "block/" + path
        ));
    }
}
