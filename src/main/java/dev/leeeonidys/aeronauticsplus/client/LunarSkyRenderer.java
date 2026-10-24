package dev.leeeonidys.aeronauticsplus.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.core.LunarTime;
import dev.leeeonidys.aeronauticsplus.space.world.MoonWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

/**
 * The Earth in the lunar sky.
 *
 * <p>From the Moon the Earth is <b>not</b> a moving object. The Moon is tidally locked, so it keeps
 * one face toward the Earth: the Earth hangs at a fixed point of the sky, depends only on where
 * you stand, and is never visible from the far side. What moves is the <b>Sun</b>, one slow turn
 * per synodic month — and with it the Earth's phase: new at lunar noon, full at lunar midnight.
 * Vanilla already rotates its sky by {@code getTimeOfDay}, so at 1/30 speed the Sun and the stars
 * take a lunar month per revolution for free.</p>
 *
 * <p>This renderer therefore does exactly two things: pin one textured quad to a fixed direction,
 * and pick the frame of a 16-step phase strip from the lunar clock. No axial tilt model, no
 * libration, no parallax: the player can walk in any direction on an infinite flat Moon, so
 * "where am I on the near side" has no answer yet.</p>
 */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, value = Dist.CLIENT)
public final class LunarSkyRenderer {
    private static final ResourceLocation PHASES = ResourceLocation.fromNamespaceAndPath(
            AeronauticsPlus.MODID, "textures/environment/lunar_earth_phases.png");
    /** Frames in the phase strip: frame 0 is new, frame 8 is full. */
    private static final int FRAMES = 16;
    /** Angular radius of the Earth seen from the Moon: 1.9°, about 3.7 times the Sun's. */
    private static final float EARTH_RADIUS_DEGREES = 1.9f;
    /** Fixed elevation of the sub-Earth point in this model, degrees. */
    private static final float ELEVATION_DEGREES = 55.0f;
    /** Fixed azimuth, degrees from north. */
    private static final float AZIMUTH_DEGREES = 0.0f;
    /** Vanilla draws its sky on a shell of radius 100; stay just inside it. */
    private static final float SHELL = 99.0f;

    private LunarSkyRenderer() {
    }

    @SubscribeEvent
    public static void afterSky(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !MoonWorld.isMoon(minecraft.level)) {
            return;
        }
        // AFTER_SKY is dispatched with a null pose stack, so the camera transform has to be
        // applied by hand. Vanilla's sky has already been drawn and wrote no depth.
        PoseStack pose = new PoseStack();
        pose.mulPose(new Matrix4f(event.getModelViewMatrix()));
        pose.mulPose(new Quaternionf()
                .rotateY((float) Math.toRadians(AZIMUTH_DEGREES))
                .rotateX((float) Math.toRadians(ELEVATION_DEGREES)));
        drawEarth(pose, frame(minecraft.level.getDayTime()));
    }

    /**
     * Which of the sixteen phase frames belongs to this lunar instant. At lunar noon the Moon is
     * between the Earth and the Sun, so the Earth is new; half a lunar day later it is full.
     */
    static int frame(long dayTime) {
        double fraction = LunarTime.vanillaFraction(dayTime);
        double sinceNoon = (fraction - 0.25 + 1.0) % 1.0;
        int index = (int) Math.floor(sinceNoon * FRAMES);
        return Math.floorMod(index, FRAMES);
    }

    private static void drawEarth(PoseStack pose, int frame) {
        float half = (float) (Math.tan(Math.toRadians(EARTH_RADIUS_DEGREES)) * SHELL);
        float u0 = frame / (float) FRAMES;
        float u1 = (frame + 1) / (float) FRAMES;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PHASES);

        Matrix4f matrix = pose.last().pose();
        float z = -SHELL;
        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -half, -half, z).setUv(u0, 1.0f);
        buffer.addVertex(matrix, half, -half, z).setUv(u1, 1.0f);
        buffer.addVertex(matrix, half, half, z).setUv(u1, 0.0f);
        buffer.addVertex(matrix, -half, half, z).setUv(u0, 0.0f);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
