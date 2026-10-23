package dev.leeeonidys.aeronauticsplus.client;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.content.propeller.AircraftPropellerBlockEntity;
import dev.leeeonidys.aeronauticsplus.content.rotor.CoaxialRotorKinematics;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Compile-checked Flywheel proof for two independently transformable rotor
 * meshes attached to one Create Aeronautics propeller block entity.
 *
 * <p>This visual is deliberately not registered yet: the final v0.3 block and
 * exported Blender partial models must be accepted first. It proves the API
 * path without exposing an unfinished gameplay block.</p>
 */
public final class CoaxialRotorVisualPrototype
        extends OrientedRotatingVisual<AircraftPropellerBlockEntity>
        implements SimpleDynamicVisual {

    private static final PartialModel UPPER_ROTOR = block("coaxial_rotor/upper_rotor");
    private static final PartialModel LOWER_ROTOR = block("coaxial_rotor/lower_rotor");

    private final OrientedInstance upperRotor;
    private final OrientedInstance lowerRotor;
    private final Vector3f rotationAxis;
    private final Quaternionf blockOrientation;
    private float lastBaseAngle = Float.NaN;

    public CoaxialRotorVisualPrototype(
            VisualizationContext context,
            AircraftPropellerBlockEntity blockEntity,
            float partialTick
    ) {
        super(
                context,
                blockEntity,
                partialTick,
                Direction.SOUTH,
                blockEntity.getBlockState().getValue(BlockStateProperties.FACING).getOpposite(),
                Models.partial(AllPartialModels.SHAFT_HALF)
        );

        Direction facing = this.blockState.getValue(BlockStateProperties.FACING);
        Vec3i normal = facing.getNormal();
        Vec3 normalPosition = new Vec3(normal.getX(), normal.getY(), normal.getZ());
        Vector3f modelPosition = Vec3.atLowerCornerOf(this.getVisualPosition())
                .add(normalPosition.scale(3.0 / 16.0))
                .toVector3f();

        this.rotationAxis = Direction.get(Direction.AxisDirection.POSITIVE, this.rotationAxis()).step();
        this.blockOrientation = new Quaternionf(facing.getRotation());

        this.upperRotor = this.instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(UPPER_ROTOR))
                .createInstance();
        this.lowerRotor = this.instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(LOWER_ROTOR))
                .createInstance();

        this.upperRotor.position(modelPosition).rotation(this.blockOrientation).setChanged();
        this.lowerRotor.position(modelPosition).rotation(this.blockOrientation).setChanged();
    }

    @Override
    public void beginFrame(Context context) {
        CoaxialRotorKinematics.RotorAngles angles = CoaxialRotorKinematics.interpolateDegrees(
                this.blockEntity.getPreviousAngle(),
                this.blockEntity.getAngle(),
                context.partialTick()
        );
        if (this.lastBaseAngle == angles.upperDegrees()) {
            return;
        }
        this.lastBaseAngle = angles.upperDegrees();

        rotate(this.upperRotor, angles.upperDegrees());
        rotate(this.lowerRotor, angles.lowerDegrees());
    }

    private void rotate(OrientedInstance instance, float degrees) {
        instance.identityRotation()
                .rotate((float) Math.toRadians(degrees), this.rotationAxis.x, this.rotationAxis.y, this.rotationAxis.z)
                .rotate(this.blockOrientation)
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(this.pos, this.upperRotor, this.lowerRotor);
    }

    @Override
    protected void _delete() {
        super._delete();
        this.upperRotor.delete();
        this.lowerRotor.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(this.upperRotor);
        consumer.accept(this.lowerRotor);
    }

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(
                AeronauticsPlus.MODID,
                "block/" + path
        ));
    }
}
