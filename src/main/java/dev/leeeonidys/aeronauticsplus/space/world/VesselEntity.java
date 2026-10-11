package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.DockCapture;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import dev.leeeonidys.aeronauticsplus.space.core.Aerodynamics;
import dev.leeeonidys.aeronauticsplus.space.core.AscentOutcome;
import dev.leeeonidys.aeronauticsplus.space.core.Atmosphere;
import dev.leeeonidys.aeronauticsplus.space.core.BoostbackOutcome;
import dev.leeeonidys.aeronauticsplus.space.core.FlightFault;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.HoldAim;
import dev.leeeonidys.aeronauticsplus.space.core.Warp;
import dev.leeeonidys.aeronauticsplus.space.core.FlightSplit;
import dev.leeeonidys.aeronauticsplus.space.core.Attitude;
import dev.leeeonidys.aeronauticsplus.space.core.LandingOutcome;
import dev.leeeonidys.aeronauticsplus.space.core.LookGuidance;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapTrack;
import dev.leeeonidys.aeronauticsplus.space.core.RadialFlightSplit;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitState;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDynamics;
import dev.leeeonidys.aeronauticsplus.space.core.WorldHandoff;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Flying pile after {@link WorldLiftoff}. Renders existing vessel-part models.
 * Players who sat in the control seat ride this entity. Unmanned stacks still
 * leave for the Kepler map at 20 km.
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
    private static final EntityDataAccessor<Float> HUD_ALTITUDE =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HUD_HORIZ =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HUD_RADIAL =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HUD_THROTTLE =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> HUD_WARP =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HUD_HOLD =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.INT);

    private static final double STEP_SECONDS = 0.05;

    private FlightLoop loop;
    private Player pendingRemount;

    public VesselEntity(EntityType<? extends VesselEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public FlightLoop loop() {
        return loop;
    }

    public void clearLoop() {
        loop = null;
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
        syncHud();
        syncPosition();
    }

    public String partsPayload() {
        return entityData.get(PARTS);
    }

    public BlockPos padPos() {
        return new BlockPos(entityData.get(PAD_X), entityData.get(PAD_Y), entityData.get(PAD_Z));
    }

    public OrbitMapTrack mapTrack() {
        return loop == null ? null : OrbitMapTrack.from(loop);
    }

    public float hudAltitudeMeters() {
        return entityData.get(HUD_ALTITUDE);
    }

    public float hudHorizontalMetersPerSecond() {
        return entityData.get(HUD_HORIZ);
    }

    public float hudRadialMetersPerSecond() {
        return entityData.get(HUD_RADIAL);
    }

    public float hudThrottle() {
        return entityData.get(HUD_THROTTLE);
    }

    public int hudWarp() {
        return Warp.factor(entityData.get(HUD_WARP));
    }

    public String hudHoldLabel() {
        return HoldAim.label(holdMode());
    }

    private HoldAim.Mode holdMode() {
        HoldAim.Mode[] modes = HoldAim.Mode.values();
        int ordinal = entityData.get(HUD_HOLD);
        if (ordinal < 0 || ordinal >= modes.length) {
            return HoldAim.Mode.OFF;
        }
        return modes[ordinal];
    }

    public String commandThrottle(double throttle) {
        if (loop == null) {
            return "Нет полёта.";
        }
        loop = loop.withVessel(loop.vessel().withLiveThrottle(throttle));
        entityData.set(PARTS, FlightCodec.encodeFlying(loop));
        syncHud();
        return String.format(java.util.Locale.ROOT, "Тяга %.0f%%.", throttle * 100.0);
    }

    public String commandNudgeThrottle(double delta) {
        if (loop == null) {
            return "Нет полёта.";
        }
        loop = loop.withVessel(loop.vessel().nudgeLiveThrottle(delta));
        entityData.set(PARTS, FlightCodec.encodeFlying(loop));
        syncHud();
        return String.format(java.util.Locale.ROOT, "Тяга %.0f%%.", loop.vessel().liveThrottle() * 100.0);
    }

    public String commandRcs() {
        if (loop == null) {
            return "Нет полёта.";
        }
        try {
            loop = loop.fireRcs(0.2);
            entityData.set(PARTS, FlightCodec.encodeFlying(loop));
            syncHud();
            return "РСУ.";
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            return message == null || message.isBlank() ? "РСУ не сработало." : message;
        }
    }

    public String commandStage() {
        if (loop == null) {
            return "Нет полёта.";
        }
        try {
            if (loop.hasRadialRing() && loop.vessel().hasStrapOns()) {
                applyRadial(loop.fireRadial());
                return "Боковушки отделены.";
            }
            if (loop.hasPyroRing() && loop.vessel().canSeparateActive()) {
                applyAxial(loop.firePyro());
                return "Ступень отделена.";
            }
            return "Нет пирокольца или верхней ступени.";
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            return message == null || message.isBlank() ? "Кольцо не сработало." : message;
        }
    }

    public String commandWarp(int delta) {
        int next = Warp.clampIndex(entityData.get(HUD_WARP) + delta);
        entityData.set(HUD_WARP, next);
        return String.format(java.util.Locale.ROOT, "x%d.", Warp.factor(next));
    }

    public String commandHoldCycle() {
        HoldAim.Mode next = HoldAim.cycle(holdMode());
        entityData.set(HUD_HOLD, next.ordinal());
        return "Удержание " + HoldAim.label(next) + ".";
    }

    public String commandDropFairing() {
        if (loop == null) {
            return "Нет полёта.";
        }
        try {
            loop = loop.dropFairing();
            entityData.set(PARTS, FlightCodec.encodeFlying(loop));
            syncHud();
            return "";
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            return message == null || message.isBlank() ? "Обтекатель не сброшен." : message;
        }
    }

    private Player pilot() {
        for (Entity passenger : getPassengers()) {
            if (passenger instanceof Player player) {
                return player;
            }
        }
        return null;
    }

    private void applyPilotLook() {
        Player pilot = pilot();
        if (pilot == null || loop == null) {
            return;
        }
        Attitude attitude = LookGuidance.attitudeFromLook(
                loop.vessel().orbit(), pilot.getYRot(), pilot.getXRot());
        Vector3d aim = LookGuidance.inertialAim(
                loop.vessel().orbit(), pilot.getYRot(), pilot.getXRot());
        loop = loop.withVessel(VesselDynamics.gimbalTowardInertial(
                loop.vessel().withAttitude(attitude), aim));
    }

    private void applyHold() {
        if (loop == null) {
            return;
        }
        HoldAim.Mode mode = holdMode();
        if (mode == HoldAim.Mode.OFF) {
            return;
        }
        Vector3d aim = HoldAim.inertialAim(loop.vessel().orbit(), mode);
        Attitude attitude = HoldAim.attitude(loop.vessel().orbit(), mode);
        loop = loop.withVessel(VesselDynamics.gimbalTowardInertial(
                loop.vessel().withAttitude(attitude), aim));
    }

    private void applyCrewSteer() {
        if (holdMode() != HoldAim.Mode.OFF) {
            applyHold();
        } else {
            applyPilotLook();
        }
    }

    private void syncVisualAttitude() {
        if (loop != null && holdMode() != HoldAim.Mode.OFF) {
            Vector3d aim = HoldAim.inertialAim(loop.vessel().orbit(), holdMode());
            double[] look = LookGuidance.lookDegreesFromAim(loop.vessel().orbit(), aim);
            setYRot((float) look[0]);
            setXRot((float) look[1] + 90.0f);
            return;
        }
        Player pilot = pilot();
        if (pilot != null) {
            setYRot(pilot.getYRot());
            setXRot(pilot.getXRot() + 90.0f);
        } else {
            setXRot(0.0f);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || loop == null) {
            return;
        }
        if (pendingRemount != null && pendingRemount.isAlive() && !pendingRemount.isPassenger()) {
            pendingRemount.startRiding(this, true);
            pendingRemount.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            "Выше 20 км слезть нельзя. Карта M. Карман N на круговой стоянке."),
                    false);
        }
        pendingRemount = null;
        if (loop.presence() == FlightPresence.ORBIT_MAP && getPassengers().isEmpty()) {
            handoffToMap();
            return;
        }
        boolean crewed = pilot() != null;
        if (crewed) {
            applyCrewSteer();
        }
        syncVisualAttitude();
        if (loop.hasRadialRing() && loop.vessel().hasStrapOns() && loop.strapOnsDry()) {
            try {
                applyRadial(loop.fireRadial());
            } catch (IllegalStateException ignored) {
                // rings present but they did not cut sides
            }
        } else if (loop.hasPyroRing()
                && !loop.vessel().hasStrapOns()
                && loop.vessel().canSeparateActive()
                && !(loop.vessel().activeStage().propellantMassKg() > 1.0e-9)) {
            try {
                applyAxial(loop.firePyro());
            } catch (IllegalStateException ignored) {
                // ring present but it did not cut a stage
            }
        }
        int warpIndex = entityData.get(HUD_WARP);
        for (int step = 0; ; step++) {
            boolean thrusting = loop.vessel().livePropellantKg() > 1.0e-9
                    && loop.vessel().clusterThrustNewtons() > 0.0;
            boolean falling = loop.vessel().orbit().radialSpeedMetersPerSecond() < 0.0;
            double altitude = loop.vessel().orbit().altitudeMeters();
            if (step >= Warp.steps(crewed, warpIndex, thrusting, falling, altitude)) {
                break;
            }
            if (crewed && step > 0) {
                applyCrewSteer();
            }
            if (stepPhysics(crewed)) {
                return;
            }
        }
        syncHud();
        syncPosition();
        if (loop.presence() == FlightPresence.ORBIT_MAP && getPassengers().isEmpty()) {
            handoffToMap();
            return;
        }
        tryDock();
    }

    private boolean stepPhysics(boolean crewed) {
        boolean thrusting = loop.vessel().livePropellantKg() > 1.0e-9
                && loop.vessel().clusterThrustNewtons() > 0.0;
        boolean falling = loop.vessel().orbit().radialSpeedMetersPerSecond() < 0.0;
        double altitude = loop.vessel().orbit().altitudeMeters();
        boolean hasLegs = loop.vessel().activeStage().hasLandingLegs();
        boolean downrange = loop.vessel().orbit().horizontalSpeedMetersPerSecond()
                > BoostbackOutcome.DONE_HORIZONTAL_METERS_PER_SECOND;
        boolean landingBurn = falling && altitude < Warp.LOW_ALTITUDE_METERS && hasLegs;
        if (!crewed && thrusting && downrange && hasLegs) {
            BoostbackOutcome boost = VesselDynamics.attemptBoostback(
                    loop.vessel(), STEP_SECONDS, STEP_SECONDS);
            loop = loop.withVessel(boost.vessel());
            if (boost.has(FlightFault.IMPACT)
                    || loop.vessel().orbit().altitudeMeters() <= LandingOutcome.TOUCHDOWN_ALTITUDE_METERS) {
                landAndDiscard();
                return true;
            }
        } else if (!crewed && landingBurn) {
            LandingOutcome landing = VesselDynamics.attemptLanding(
                    loop.vessel(), STEP_SECONDS, STEP_SECONDS);
            loop = loop.withVessel(landing.vessel());
            if (landing.landed()
                    || landing.has(FlightFault.IMPACT)
                    || landing.has(FlightFault.NO_LEGS)
                    || loop.vessel().orbit().altitudeMeters() <= LandingOutcome.TOUCHDOWN_ALTITUDE_METERS) {
                landAndDiscard();
                return true;
            }
        } else if (thrusting) {
            AscentOutcome outcome = VesselDynamics.advanceAscent(
                    loop.vessel(), Atmosphere.earth(), STEP_SECONDS, Aerodynamics.ROCKET_CD, 1.0);
            if (outcome.has(FlightFault.HOLD_DOWN) || outcome.has(FlightFault.IMPACT)) {
                landAndDiscard();
                return true;
            }
            loop = loop.withVessel(outcome.vessel());
        } else {
            loop = loop.coast(STEP_SECONDS);
            if (loop.vessel().orbit().altitudeMeters() <= LandingOutcome.TOUCHDOWN_ALTITUDE_METERS) {
                landAndDiscard();
                return true;
            }
        }
        return false;
    }

    private void landAndDiscard() {
        pendingRemount = null;
        ejectPassengers();
        WorldLiftoff.land(level(), padPos(), loop);
        discard();
    }

    private void applyRadial(RadialFlightSplit split) {
        for (FlightLoop side : split.sides()) {
            releaseCompanion(side);
        }
        loop = split.core();
        entityData.set(PARTS, FlightCodec.encodeFlying(loop));
    }

    private void applyAxial(FlightSplit split) {
        releaseCompanion(split.booster());
        loop = split.continuing();
        entityData.set(PARTS, FlightCodec.encodeFlying(loop));
        syncHud();
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
        if (!getPassengers().isEmpty()) {
            return;
        }
        if (level() instanceof ServerLevel server) {
            OrbitMapData.get(server).enter(loop);
        }
        discard();
    }

    private void tryDock() {
        if (loop == null || loop.presence() != FlightPresence.WORLD_ENTITY) {
            return;
        }
        if (!(level() instanceof ServerLevel server)) {
            return;
        }
        AABB box = getBoundingBox().inflate(DockCapture.REACH_METERS);
        for (VesselEntity other : server.getEntitiesOfClass(VesselEntity.class, box,
                candidate -> candidate != this && candidate.isAlive())) {
            if (getUUID().compareTo(other.getUUID()) > 0) {
                continue;
            }
            if (other.loop() == null || other.loop().presence() != FlightPresence.WORLD_ENTITY) {
                continue;
            }
            try {
                FlightLoop joined = loop.dock(other.loop(), distanceTo(other));
                List<Entity> riders = new ArrayList<>(other.getPassengers());
                other.clearLoop();
                other.ejectPassengers();
                other.discard();
                loop = joined;
                entityData.set(PARTS, FlightCodec.encodeFlying(loop));
                syncHud();
                for (Entity rider : riders) {
                    if (rider.isAlive() && !rider.isPassenger()) {
                        rider.startRiding(this, true);
                    }
                }
                Player seated = pilot();
                if (seated != null) {
                    seated.displayClientMessage(Component.literal("Стыковка."), false);
                }
                return;
            } catch (RuntimeException ignored) {
                // not aligned this tick
            }
        }
    }

    private void syncHud() {
        if (loop == null) {
            return;
        }
        OrbitState orbit = loop.vessel().orbit();
        entityData.set(HUD_ALTITUDE, (float) orbit.altitudeMeters());
        entityData.set(HUD_HORIZ, (float) orbit.horizontalSpeedMetersPerSecond());
        entityData.set(HUD_RADIAL, (float) orbit.radialSpeedMetersPerSecond());
        entityData.set(HUD_THROTTLE, (float) loop.vessel().liveThrottle());
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
        builder.define(HUD_ALTITUDE, 0.0f);
        builder.define(HUD_HORIZ, 0.0f);
        builder.define(HUD_RADIAL, 0.0f);
        builder.define(HUD_THROTTLE, 1.0f);
        builder.define(HUD_WARP, 0);
        builder.define(HUD_HOLD, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(PARTS, tag.getString("Parts"));
        entityData.set(PAD_X, tag.getInt("PadX"));
        entityData.set(PAD_Y, tag.getInt("PadY"));
        entityData.set(PAD_Z, tag.getInt("PadZ"));
        entityData.set(HUD_WARP, Warp.clampIndex(tag.getInt("WarpIndex")));
        int hold = tag.getInt("HoldMode");
        if (hold < 0 || hold >= HoldAim.Mode.values().length) {
            hold = 0;
        }
        entityData.set(HUD_HOLD, hold);
        restoreLoop(tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Parts", entityData.get(PARTS));
        tag.putInt("PadX", entityData.get(PAD_X));
        tag.putInt("PadY", entityData.get(PAD_Y));
        tag.putInt("PadZ", entityData.get(PAD_Z));
        tag.putInt("WarpIndex", entityData.get(HUD_WARP));
        tag.putInt("HoldMode", entityData.get(HUD_HOLD));
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
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().size() < 4;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
        double y = 0.42;
        for (PartView part : decodeParts(partsPayload())) {
            if ("rocket_controller".equals(part.specId())) {
                y = Math.max(y, part.y() + 0.42);
            }
        }
        return new Vec3(0.0, y, 0.0);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!level().isClientSide
                && loop != null
                && WorldHandoff.onMap(loop.vessel().orbit())
                && passenger instanceof Player player
                && player.isAlive()) {
            pendingRemount = player;
        }
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (!level().isClientSide && passenger instanceof Player player) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            "На борту. Слезь до 20 км — упадёшь. На линии стек уходит на карту M, если никого нет."),
                    false);
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
