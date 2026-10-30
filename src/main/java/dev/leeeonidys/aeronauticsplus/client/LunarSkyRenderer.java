package dev.leeeonidys.aeronauticsplus.client;

import java.util.Random;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.core.LunarSurfaceCoordinates;
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
 * <p>The flat Moon uses a declared equirectangular convention: X is east/west longitude, Z is
 * south/north latitude, and Y is altitude. This positions the Earth and hides it below the
 * geometric horizon on the far side. Terrain remains flat; this is not a spherical terrain map.
 * Axial tilt and libration are still omitted.</p>
 */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, value = Dist.CLIENT)
public final class LunarSkyRenderer {
    private static final ResourceLocation PHASES = ResourceLocation.fromNamespaceAndPath(
            AeronauticsPlus.MODID, "textures/environment/lunar_earth_phases.png");
    /** Frames in the phase strip: frame 0 is new, frame 8 is full. */
    private static final int FRAMES = 16;
    /** Angular radius of the Earth seen from the Moon: 1.9° * 1.8 ≈ 3.4°, ~6.7× the Sun's. */
    private static final float EARTH_RADIUS_DEGREES = 3.4f;
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
        if (minecraft.player == null) {
            return;
        }
        LunarSurfaceCoordinates.Direction direction = LunarSurfaceCoordinates.earthDirection(
                minecraft.player.getX(), minecraft.player.getZ());
        if (!direction.visible()) {
            return;
        }
        PoseStack pose = new PoseStack();
        pose.mulPose(new Matrix4f(event.getModelViewMatrix()));
        float skyAngle = (float) ((minecraft.level.getDayTime() % 24000L) / 24000.0);
        float sunFraction = (float) ((minecraft.level.getDayTime() % 24000L) / 24000.0);
        // Stars belong to the world sky sphere — draw them BEFORE the Earth-local rotation.
        drawStars(pose, skyAngle);
        // From here on the pose is rotated to Earth's direction; Sun and Earth share that frame.
        pose.pushPose();
        // Minecraft's sky forward is north (-Z); positive map longitude is east (+X).
        pose.mulPose(new Quaternionf()
                .rotateY((float) -direction.azimuthFromNorthRadians())
                .rotateX((float) direction.elevationRadians()));
        drawSun(pose, sunFraction);
        drawEarth(pose, frame(minecraft.level.getDayTime()));
        pose.popPose();
    }

    /** Starfield on the lunar sky sphere. Stars rotate with the sky angle; brightest at midnight. */
    private static void drawStars(PoseStack pose, float skyAngle) {
        // Stars are always visible on the airless Moon (no atmosphere to wash them out).
        final float starAlpha = 1.0f;
        if (starAlpha <= 0.01f) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        float skyRot = skyAngle * (float) Math.PI * 2.0f;
        float cosR = (float) Math.cos(skyRot);
        float sinR = (float) Math.sin(skyRot);

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Random random = new Random(12345L);
        for (int i = 0; i < 400; i++) {
            float sx = random.nextFloat() * 2.0f - 1.0f;
            float sy = random.nextFloat() * 2.0f - 1.0f;
            float sz = random.nextFloat() * 2.0f - 1.0f;
            float len = (float) Math.sqrt(sx * sx + sy * sy + sz * sz);
            sx /= len; sy /= len; sz /= len;

            float wx = (sx * cosR + sz * sinR) * SHELL;
            float wy = sy * SHELL;
            float wz = (-sx * sinR + sz * cosR) * SHELL;

            pose.pushPose();
            pose.translate(wx, wy, wz);
            Matrix4f starMatrix = pose.last().pose();

            float size = 0.15f + random.nextFloat() * 0.45f;
            int alpha = (int) (starAlpha * 255);

            buffer.addVertex(starMatrix, -size, -size, 0).setColor(255, 255, 255, alpha);
            buffer.addVertex(starMatrix, size, -size, 0).setColor(255, 255, 255, alpha);
            buffer.addVertex(starMatrix, size, size, 0).setColor(255, 255, 255, alpha);
            buffer.addVertex(starMatrix, -size, size, 0).setColor(255, 255, 255, alpha);

            pose.popPose();
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
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

    /** Draw the Earth disk on the lunar sky sphere with its phase and increased angular size. */
    private static void drawEarth(PoseStack pose, int frame) {
        float half = (float) (Math.tan(Math.toRadians(EARTH_RADIUS_DEGREES)) * SHELL);
        float u0 = frame / (float) FRAMES;
        float u1 = (frame + 1) / (float) FRAMES;

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PHASES);

        Matrix4f matrix = pose.last().pose();
        float z = -SHELL;
        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        // Additive blend: bright center with soft falloff from the phase map alpha.
        buffer.addVertex(matrix, -half, -half, z).setUv(u0, 1.0f).setColor(255, 255, 255, 255);
        buffer.addVertex(matrix, half, -half, z).setUv(u1, 1.0f).setColor(255, 255, 255, 255);
        buffer.addVertex(matrix, half, half, z).setUv(u1, 0.0f).setColor(255, 255, 255, 255);
        buffer.addVertex(matrix, -half, half, z).setUv(u0, 0.0f).setColor(255, 255, 255, 255);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    /** Draw the Sun disk on the lunar sky sphere at the true time-of-day angle. */
    private static void drawSun(PoseStack pose, float sunFraction) {
        // Sun angular radius seen from the Moon: about 0.5°.
        final float SUN_RADIUS_DEGREES = 0.5f;
        float half = (float) (Math.tan(Math.toRadians(SUN_RADIUS_DEGREES)) * SHELL);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE); // additive
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = pose.last().pose();
        float z = -SHELL;
        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        // Bright white-yellow disk with full additive blend so it pops against black sky.
        buffer.addVertex(matrix, -half, -half, z).setColor(255, 255, 220, 200);
        buffer.addVertex(matrix, half, -half, z).setColor(255, 255, 220, 200);
        buffer.addVertex(matrix, half, half, z).setColor(255, 255, 220, 200);
        buffer.addVertex(matrix, -half, half, z).setColor(255, 255, 220, 200);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
