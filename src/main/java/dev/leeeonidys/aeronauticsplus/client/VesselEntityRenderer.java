package dev.leeeonidys.aeronauticsplus.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlocks;
import dev.leeeonidys.aeronauticsplus.space.world.VesselEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * Draws the flying pile with the same block models as on the pad. Not a cube mesh.
 */
public final class VesselEntityRenderer extends EntityRenderer<VesselEntity> {
    private final BlockRenderDispatcher dispatcher;

    public VesselEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.dispatcher = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(
            VesselEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack pose,
            MultiBufferSource buffer,
            int packedLight) {
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
        pose.pushPose();
        pose.translate(-0.5, 0.0, -0.5);
        for (VesselEntity.PartView part : VesselEntity.decodeParts(entity.partsPayload())) {
            BlockState state = VesselPartBlocks.stateFor(part.specId(), part.facing());
            if (state == null) {
                continue;
            }
            pose.pushPose();
            pose.translate(part.x(), part.y(), part.z());
            dispatcher.renderSingleBlock(
                    state, pose, buffer, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
            pose.popPose();
        }
        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(VesselEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRenderOffScreen(VesselEntity entity) {
        return true;
    }
}
