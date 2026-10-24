package dev.leeeonidys.aeronauticsplus.content.rocket;

import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Invisible mount for the control seat on the pad. Ignition transfers the
 * player onto {@link dev.leeeonidys.aeronauticsplus.space.world.VesselEntity}.
 */
public final class SeatEntity extends Entity {
    /** Top of the bucket pan, straight from rocket_controller.obj. */
    private static final double PAN_Y = 0.40;
    /**
     * The player model is scaled by 15/16 and its hip pivot is 12 px up, so the hips of a rider
     * sit 0.70 m above the entity position. The rider's feet therefore end up 0.30 m below the
     * cell base — inside the pedestal, where nobody can see them.
     */
    private static final double HIP_PIVOT = 0.70;
    /** The player's own vehicle anchor: 0.6 m above the feet (Player.DEFAULT_VEHICLE_ATTACHMENT). */
    private static final double PLAYER_ANCHOR = 0.60;
    /** Butt slightly forward of the cell centre so the back does not clip the backrest. */
    private static final double SIT_FORWARD = 0.06;

    public SeatEntity(EntityType<? extends SeatEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
        setInvisible(true);
        setSilent(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        if (getPassengers().isEmpty()) {
            discard();
            return;
        }
        BlockPos pos = blockPosition();
        if (!(level().getBlockState(pos).getBlock() instanceof VesselPartBlock part)
                || part.spec().kind() != VesselPartKind.CONTROLLER) {
            ejectPassengers();
            discard();
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().isEmpty();
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
        // Offset is in world axes and the seat is spawned at the cell base, so it is rotated by the
        // entity yaw: the chair's front follows FACING, and the rider's knees follow with it.
        return new Vec3(0.0, PAN_Y - HIP_PIVOT + PLAYER_ANCHOR, SIT_FORWARD)
                .yRot(-this.getYRot() * Mth.DEG_TO_RAD);
    }

    /**
     * Feet are 0.30 m inside the cell, so standing up in place would leave the player embedded in
     * the block and drop them through the floor. Lift them onto the top of the seat instead.
     */
    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (level().isClientSide || !(passenger instanceof ServerPlayer player)) {
            return;
        }
        BlockPos pos = blockPosition();
        player.teleportTo(
                (ServerLevel) level(),
                pos.getX() + 0.5,
                pos.getY() + 1.0,
                pos.getZ() + 0.5,
                Set.<RelativeMovement>of(),
                this.getYRot(),
                player.getXRot());
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
