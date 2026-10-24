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
 * Martian sky: butterscotch by day, dusty blue near the horizon at dusk, no clouds and no rain.
 * Purely visual. Air density, dust and everything chemical stay ChemMod's.
 *
 * <p>Registered for the {@code aeronauticsplus:mars} dimension type, which the server JSON
 * names in {@code "effects"}.</p>
 */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MarsSky {
    /** Dusty butterscotch averaged from Mars Pathfinder sky imagery. */
    private static final Vec3 FOG = new Vec3(0.83, 0.68, 0.51);
    private static final ResourceLocation TYPE =
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "mars");

    private MarsSky() {
    }

    @SubscribeEvent
    public static void register(RegisterDimensionSpecialEffectsEvent event) {
        event.register(TYPE, new Effects());
    }

    private static final class Effects extends DimensionSpecialEffects {
        Effects() {
            // cloudLevel NaN = no clouds; ground, normal sky type, no constant ambient light.
            super(Float.NaN, true, SkyType.NORMAL, false, false);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
            return fogColor.multiply(brightness * 0.94 + 0.06, brightness * 0.94 + 0.06, brightness * 0.94 + 0.06);
        }

        @Override
        public boolean isFoggyAt(int x, int z) {
            // The dust haze is thick near the ground; 60 blocks up the sky is clear.
            return false;
        }

        @Override
        public float getCloudHeight() {
            return Float.NaN;
        }

        @Override
        public float[] getSunriseColor(float skyAngle, float partialTick) {
            // Rust-coloured glow: the Martian sunsets are blue, but the dust halo is ochre.
            float f = 0.4F;
            float f1 = net.minecraft.util.Mth.cos(skyAngle * ((float) Math.PI * 2.0F));
            if (f1 >= -f && f1 <= f) {
                float f3 = (f1 + f) / (f * 2.0F) * 0.5F + 0.5F;
                float f4 = 1.0F - (1.0F - net.minecraft.util.Mth.sin(f3 * (float) Math.PI)) * 0.99F;
                f4 *= f4;
                return new float[] {f3 * 0.30F + 0.70F, f3 * f3 * 0.55F + 0.35F, f3 * f3 * 0.20F + 0.10F, f4};
            }
            return null;
        }
    }
}
