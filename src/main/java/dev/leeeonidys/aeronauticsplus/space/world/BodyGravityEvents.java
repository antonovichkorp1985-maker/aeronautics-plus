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
 * Surface gravity per body from μ/R², not a cartoon moon hop: the Moon is 1.62 m/s², Mars
 * 3.73 m/s². ChemMod still owns vacuum and thin-air damage; this only touches the vanilla
 * gravity attribute, and it is removed again the moment the player leaves the body.
 */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class BodyGravityEvents {
    private static final ResourceLocation MOON_ID =
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "moon_gravity");
    private static final ResourceLocation MARS_ID =
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "mars_gravity");
    private static final AttributeModifier MOON = new AttributeModifier(
            MOON_ID, MoonWorld.GRAVITY_FACTOR - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    private static final AttributeModifier MARS = new AttributeModifier(
            MARS_ID, MarsWorld.GRAVITY_FACTOR - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    private BodyGravityEvents() {
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        LivingEntity living = event.getEntity();
        AttributeInstance gravity = living.getAttribute(Attributes.GRAVITY);
        if (gravity == null) {
            return;
        }
        apply(gravity, MoonWorld.isMoon(living.level()), MOON_ID, MOON);
        apply(gravity, MarsWorld.isMars(living.level()), MARS_ID, MARS);
    }

    private static void apply(AttributeInstance gravity, boolean wanted, ResourceLocation id, AttributeModifier modifier) {
        boolean present = gravity.hasModifier(id);
        if (wanted && !present) {
            gravity.addTransientModifier(modifier);
        } else if (!wanted && present) {
            gravity.removeModifier(id);
        }
    }
}
