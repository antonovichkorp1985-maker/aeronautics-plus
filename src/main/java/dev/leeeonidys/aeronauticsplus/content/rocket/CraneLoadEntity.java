package dev.leeeonidys.aeronauticsplus.content.rocket;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Visible part on the hook. The crane block entity owns the job.
 */
public final class CraneLoadEntity extends Entity {
    private static final EntityDataAccessor<String> SPEC =
            SynchedEntityData.defineId(CraneLoadEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> FACING =
            SynchedEntityData.defineId(CraneLoadEntity.class, EntityDataSerializers.STRING);

    public CraneLoadEntity(EntityType<? extends CraneLoadEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
        setSilent(true);
    }

    public void setPart(String specId, BlockFace facing) {
        entityData.set(SPEC, specId == null ? "" : specId);
        entityData.set(FACING, facing == null ? BlockFace.UP.name() : facing.name());
    }

    public String specId() {
        return entityData.get(SPEC);
    }

    public BlockFace facing() {
        try {
            return BlockFace.valueOf(entityData.get(FACING));
        } catch (RuntimeException ignored) {
            return BlockFace.UP;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > 20 * 60 * 5) {
            discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SPEC, "");
        builder.define(FACING, BlockFace.UP.name());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(SPEC, tag.getString("Spec"));
        entityData.set(FACING, tag.getString("Facing"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Spec", entityData.get(SPEC));
        tag.putString("Facing", entityData.get(FACING));
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
}
