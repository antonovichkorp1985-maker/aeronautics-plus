package dev.leeeonidys.aeronauticsplus.compat;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlock;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Create 6 movement: mount and flying AP parts assemble onto a train.
 * Pad stays on the ground. Loaded only when Create is present.
 */
final class CreateTrainMountHooks {
    private CreateTrainMountHooks() {
    }

    static void register() {
        BlockMovementChecks.registerMovementAllowedCheck((state, level, pos) ->
                flyingPart(state) ? BlockMovementChecks.CheckResult.SUCCESS : BlockMovementChecks.CheckResult.PASS);
        BlockMovementChecks.registerMovementNecessaryCheck((state, level, pos) -> {
            if (pad(state)) {
                return BlockMovementChecks.CheckResult.FAIL;
            }
            if (flyingPart(state)) {
                return BlockMovementChecks.CheckResult.SUCCESS;
            }
            return BlockMovementChecks.CheckResult.PASS;
        });
        BlockMovementChecks.registerAttachedCheck((state, level, pos, direction) ->
                flyingPart(state) ? BlockMovementChecks.CheckResult.SUCCESS : BlockMovementChecks.CheckResult.PASS);
        BlockMovementChecks.registerNotSupportiveCheck((state, direction) ->
                pad(state) ? BlockMovementChecks.CheckResult.SUCCESS : BlockMovementChecks.CheckResult.PASS);
        MovementBehaviour.REGISTRY.register(AeronauticsPlus.ROCKET_MOUNT.get(), new MountMovement());
    }

    private static boolean flyingPart(BlockState state) {
        return state.getBlock() instanceof VesselPartBlock part
                && part.spec().kind() != VesselPartKind.PAD;
    }

    private static boolean pad(BlockState state) {
        return state.getBlock() instanceof VesselPartBlock part
                && part.spec().kind() == VesselPartKind.PAD;
    }

    private static final class MountMovement implements MovementBehaviour {
        @Override
        public void startMoving(MovementContext context) {
            context.data.putBoolean("aeronauticsplus_mount", true);
        }
    }
}
