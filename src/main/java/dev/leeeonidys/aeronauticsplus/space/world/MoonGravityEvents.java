package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Lunar g from μ/R², not a cartoon moon hop. ChemMod still owns vacuum damage.
 */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class MoonGravityEvents {
    private static final ResourceLocation GRAVITY_ID =
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "moon_gravity");
    private static final AttributeModifier GRAVITY = new AttributeModifier(
            GRAVITY_ID,
            MoonWorld.GRAVITY_FACTOR - 1.0,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    private MoonGravityEvents() {
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        LivingEntity living = event.getEntity();
        AttributeInstance gravity = living.getAttribute(Attributes.GRAVITY);
        if (gravity == null) {
            return;
        }
        boolean moon = MoonWorld.isMoon(living.level());
        boolean has = gravity.hasModifier(GRAVITY_ID);
        if (moon && !has) {
            gravity.addTransientModifier(GRAVITY);
        } else if (!moon && has) {
            gravity.removeModifier(GRAVITY_ID);
        }
    }
}
