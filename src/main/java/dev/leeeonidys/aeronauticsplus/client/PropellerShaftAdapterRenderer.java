package dev.leeeonidys.aeronauticsplus.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Rotates the complete tapered coupling around the transmitted Create axis.
 *
 * <p>As with the custom propellers, no Flywheel visual is registered for this
 * mod-owned block entity type. The fallback renderer therefore stays active
 * even when Flywheel is enabled.</p>
 */
public final class PropellerShaftAdapterRenderer
        extends KineticBlockEntityRenderer<SimpleKineticBlockEntity> {

    public PropellerShaftAdapterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(
            SimpleKineticBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        BlockState state = getRenderedBlockState(blockEntity);
        RenderType renderType = getRenderType(blockEntity, state);
        renderRotatingBuffer(
                blockEntity,
                getRotatedModel(blockEntity, state),
                poseStack,
                buffer.getBuffer(renderType),
                light
        );
    }
}
