package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.core.Aerodynamics;
import dev.leeeonidys.aeronauticsplus.space.core.AscentOutcome;
import dev.leeeonidys.aeronauticsplus.space.core.Atmosphere;
import dev.leeeonidys.aeronauticsplus.space.core.FlightFault;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDynamics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

/**
 * Flying pile after {@link WorldLiftoff}. Renders existing vessel-part models.
 * The player is not on board — Ad Astra still holds player space.
 */
public final class VesselEntity extends Entity {
    private static final EntityDataAccessor<String> PARTS =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> PAD_X =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PAD_Y =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PAD_Z =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.INT);

    private static final double STEP_SECONDS = 0.05;

    private FlightLoop loop;

    public VesselEntity(EntityType<? extends VesselEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public void bind(FlightLoop loop) {
        if (loop == null || loop.presence() != FlightPresence.WORLD_ENTITY) {
            throw new IllegalStateException("Vessel entity starts as the world entity");
        }
        this.loop = loop;
        GridPos pad = loop.padLeft().origin();
        entityData.set(PAD_X, pad.x());
        entityData.set(PAD_Y, pad.y());
        entityData.set(PAD_Z, pad.z());
        entityData.set(PARTS, encodeParts(loop));
        syncPosition();
    }

    public String partsPayload() {
        return entityData.get(PARTS);
    }

    public BlockPos padPos() {
        return new BlockPos(entityData.get(PAD_X), entityData.get(PAD_Y), entityData.get(PAD_Z));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || loop == null) {
            return;
        }
        AscentOutcome outcome = VesselDynamics.advanceAscent(
                loop.vessel(), Atmosphere.earth(), STEP_SECONDS, Aerodynamics.ROCKET_CD, 1.0);
        if (outcome.has(FlightFault.HOLD_DOWN)) {
            WorldLiftoff.land(level(), padPos(), loop);
            discard();
            return;
        }
        loop = loop.withVessel(outcome.vessel());
        syncPosition();
        if (loop.presence() == FlightPresence.ORBIT_MAP) {
            discard();
        }
    }

    private void syncPosition() {
        BlockPos pad = padPos();
        double alt = loop == null ? 0.0 : Math.max(0.0, loop.vessel().orbit().altitudeMeters());
        setPos(pad.getX() + 0.5, pad.getY() + alt, pad.getZ() + 0.5);
    }

    private static String encodeParts(FlightLoop loop) {
        StringBuilder builder = new StringBuilder();
        GridPos pad = loop.padLeft().origin();
        for (VesselBlockOccupant occupant : loop.flying().occupants()) {
            if (!builder.isEmpty()) {
                builder.append(';');
            }
            builder.append(occupant.spec().id())
                    .append(',')
                    .append(occupant.pos().x() - pad.x())
                    .append(',')
                    .append(occupant.pos().y() - pad.y())
                    .append(',')
                    .append(occupant.pos().z() - pad.z())
                    .append(',')
                    .append(occupant.facing().name());
        }
        return builder.toString();
    }

    public static PartView[] decodeParts(String payload) {
        if (payload == null || payload.isBlank()) {
            return new PartView[0];
        }
        String[] tokens = payload.split(";");
        PartView[] views = new PartView[tokens.length];
        int count = 0;
        for (String token : tokens) {
            String[] bits = token.split(",");
            if (bits.length != 5) {
                continue;
            }
            try {
                views[count++] = new PartView(
                        bits[0],
                        Integer.parseInt(bits[1]),
                        Integer.parseInt(bits[2]),
                        Integer.parseInt(bits[3]),
                        BlockFace.valueOf(bits[4]));
            } catch (RuntimeException ignored) {
                // skip a corrupt part rather than crash the renderer
            }
        }
        if (count == views.length) {
            return views;
        }
        PartView[] trimmed = new PartView[count];
        System.arraycopy(views, 0, trimmed, 0, count);
        return trimmed;
    }

    public record PartView(String specId, int x, int y, int z, BlockFace facing) {
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PARTS, "");
        builder.define(PAD_X, 0);
        builder.define(PAD_Y, 0);
        builder.define(PAD_Z, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(PARTS, tag.getString("Parts"));
        entityData.set(PAD_X, tag.getInt("PadX"));
        entityData.set(PAD_Y, tag.getInt("PadY"));
        entityData.set(PAD_Z, tag.getInt("PadZ"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Parts", entityData.get(PARTS));
        tag.putInt("PadX", entityData.get(PAD_X));
        tag.putInt("PadY", entityData.get(PAD_Y));
        tag.putInt("PadZ", entityData.get(PAD_Z));
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
}
