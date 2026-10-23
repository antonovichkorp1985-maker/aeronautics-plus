package dev.leeeonidys.aeronauticsplus.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.SimplePropellerRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Dynamic renderer for Aeronautics Plus propellers.
 *
 * <p>Create Aeronautics' base renderer normally returns early whenever Flywheel
 * is enabled, because its own propellers also register a Flywheel visual. Our
 * block entity types do not have that upstream registration, so this renderer
 * deliberately keeps the safe block-entity path active. This guarantees that
 * the OBJ follows the actual kinetic angle instead of remaining a static chunk
 * model.</p>
 */
public final class AircraftPropellerRenderer<T extends BasePropellerBlockEntity>
        extends SimplePropellerRenderer<T> {

    public AircraftPropellerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public PartialModel getCurrentModel(T blockEntity) {
        return PropellerPartialModels.forState(blockEntity.getBlockState());
    }

    @Override
    public void renderSafe(
            T blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        BlockState state = blockEntity.getBlockState();
        Direction direction = state.getValue(BlockStateProperties.FACING);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer propeller = CachedBuffers.partialFacing(
                getCurrentModel(blockEntity), state, direction.getOpposite()
        );

        float angle = getAngle(partialTicks, direction, blockEntity);
        kineticRotationTransform(propeller, blockEntity, direction.getAxis(), angle, light);

        // Our OBJ is authored around the Z axis through the exact block centre,
        // with its rear drive shaft pointing toward +Z. BasePropellerBlock
        // connects to the kinetic shaft opposite FACING, so partialFacing()
        // deliberately maps +Z to direction.getOpposite(). The line of that axis
        // is still direction.getAxis(), which keeps the live kinetic rotation
        // centred on the shaft for all six facing directions. Do not apply the
        // stock model's later tilts or offsets: its canonical axis is different.
        propeller.renderInto(poseStack, vertexConsumer);
    }
}
