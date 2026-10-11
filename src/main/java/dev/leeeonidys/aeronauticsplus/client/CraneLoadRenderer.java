package dev.leeeonidys.aeronauticsplus.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.leeeonidys.aeronauticsplus.content.rocket.CraneLoadEntity;
import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlocks;
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

/** Draws the hooked part with the same model as the block. */
public final class CraneLoadRenderer extends EntityRenderer<CraneLoadEntity> {
    private final BlockRenderDispatcher dispatcher;

    public CraneLoadRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.4f;
        this.dispatcher = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(
            CraneLoadEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack pose,
            MultiBufferSource buffer,
            int packedLight) {
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
        BlockState state = VesselPartBlocks.stateFor(entity.specId(), entity.facing());
        if (state == null) {
            return;
        }
        pose.pushPose();
        pose.translate(-0.5, 0.0, -0.5);
        dispatcher.renderSingleBlock(
                state, pose, buffer, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(CraneLoadEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
