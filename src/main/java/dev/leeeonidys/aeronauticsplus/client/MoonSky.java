package dev.leeeonidys.aeronauticsplus.client;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;

/**
 * Airless lunar sky: black, stars, no clouds, no sunset glow. Vanilla's own day/night sun is
 * drawn on top, which is the only reason the long lunar day is visible at all — the old
 * {@code minecraft:the_end} effects had no sun and no dawn, so a slower clock would have been
 * invisible.
 *
 * <p>Visual only. Light level still comes from sky light; vacuum, dust and heat stay ChemMod's.</p>
 */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MoonSky {
    private static final ResourceLocation TYPE =
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "moon");
    /** Space has no fog colour; a hair of blue keeps distant chunks from being pure black. */
    private static final Vec3 VACUUM_FOG = new Vec3(0.015, 0.018, 0.028);

    private MoonSky() {
    }

    @SubscribeEvent
    public static void register(RegisterDimensionSpecialEffectsEvent event) {
        event.register(TYPE, new Effects());
    }

    private static final class Effects extends DimensionSpecialEffects {
        Effects() {
            // NaN cloud level = no clouds at all; no ground haze; normal sky so the sun exists.
            super(Float.NaN, false, SkyType.NORMAL, false, false);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
            return VACUUM_FOG;
        }

        @Override
        public boolean isFoggyAt(int x, int z) {
            return false;
        }

        @Override
        public float getCloudHeight() {
            return Float.NaN;
        }

        @Override
        public float[] getSunriseColor(float skyAngle, float partialTick) {
            // No atmosphere, no refraction: the Sun just appears. No sunrise band.
            return null;
        }
    }
}
