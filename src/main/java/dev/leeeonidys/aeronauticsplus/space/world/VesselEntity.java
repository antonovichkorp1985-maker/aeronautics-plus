package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.core.Aerodynamics;
import dev.leeeonidys.aeronauticsplus.space.core.AscentOutcome;
import dev.leeeonidys.aeronauticsplus.space.core.Atmosphere;
import dev.leeeonidys.aeronauticsplus.space.core.FlightFault;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.FlightSplit;
import dev.leeeonidys.aeronauticsplus.space.core.LandingOutcome;
import dev.leeeonidys.aeronauticsplus.space.core.RadialFlightSplit;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitState;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDynamics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

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
        entityData.set(PARTS, FlightCodec.encodeFlying(loop));
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
        if (loop.presence() == FlightPresence.ORBIT_MAP) {
            handoffToMap();
            return;
        }
        if (loop.hasRadialRing() && loop.vessel().hasStrapOns() && loop.strapOnsDry()) {
            try {
                RadialFlightSplit split = loop.fireRadial();
                for (FlightLoop side : split.sides()) {
                    releaseCompanion(side);
                }
                loop = split.core();
                entityData.set(PARTS, FlightCodec.encodeFlying(loop));
            } catch (IllegalStateException ignored) {
                // rings present but they did not cut sides
            }
        } else if (loop.hasPyroRing()
                && !loop.vessel().hasStrapOns()
                && loop.vessel().canSeparateActive()
                && !(loop.vessel().activeStage().propellantMassKg() > 1.0e-9)) {
            try {
                FlightSplit split = loop.firePyro();
                releaseCompanion(split.booster());
                loop = split.continuing();
                entityData.set(PARTS, FlightCodec.encodeFlying(loop));
            } catch (IllegalStateException ignored) {
                // ring present but it did not cut a stage
            }
        }
        boolean thrusting = loop.vessel().livePropellantKg() > 1.0e-9
                && loop.vessel().clusterThrustNewtons() > 0.0;
        if (thrusting) {
            AscentOutcome outcome = VesselDynamics.advanceAscent(
                    loop.vessel(), Atmosphere.earth(), STEP_SECONDS, Aerodynamics.ROCKET_CD, 1.0);
            if (outcome.has(FlightFault.HOLD_DOWN) || outcome.has(FlightFault.IMPACT)) {
                landAndDiscard();
                return;
            }
            loop = loop.withVessel(outcome.vessel());
        } else {
            loop = loop.coast(STEP_SECONDS);
            if (loop.vessel().orbit().altitudeMeters() <= LandingOutcome.TOUCHDOWN_ALTITUDE_METERS) {
                landAndDiscard();
                return;
            }
        }
        syncPosition();
        if (loop.presence() == FlightPresence.ORBIT_MAP) {
            handoffToMap();
        }
    }

    private void landAndDiscard() {
        WorldLiftoff.land(level(), padPos(), loop);
        discard();
    }

    private void releaseCompanion(FlightLoop companion) {
        if (!(level() instanceof ServerLevel server)) {
            return;
        }
        if (companion.presence() == FlightPresence.ORBIT_MAP) {
            OrbitMapData.get(server).enter(companion);
            return;
        }
        VesselLaunch.spawn(server, companion);
    }

    private void handoffToMap() {
        if (level() instanceof ServerLevel server) {
            OrbitMapData.get(server).enter(loop);
        }
        discard();
    }

    private void syncPosition() {
        BlockPos pad = padPos();
        double alt = loop == null ? 0.0 : Math.max(0.0, loop.vessel().orbit().altitudeMeters());
        setPos(pad.getX() + 0.5, pad.getY() + alt, pad.getZ() + 0.5);
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
        restoreLoop(tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Parts", entityData.get(PARTS));
        tag.putInt("PadX", entityData.get(PAD_X));
        tag.putInt("PadY", entityData.get(PAD_Y));
        tag.putInt("PadZ", entityData.get(PAD_Z));
        if (loop != null) {
            FlightCodec.putOrbit(tag, loop.vessel().orbit());
        }
    }

    private void restoreLoop(CompoundTag tag) {
        OrbitState orbit = FlightCodec.readOrbit(tag);
        if (orbit == null) {
            return;
        }
        try {
            loop = FlightCodec.restore(
                    entityData.get(PARTS),
                    new GridPos(entityData.get(PAD_X), entityData.get(PAD_Y), entityData.get(PAD_Z)),
                    orbit);
        } catch (RuntimeException ignored) {
            loop = null;
        }
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
