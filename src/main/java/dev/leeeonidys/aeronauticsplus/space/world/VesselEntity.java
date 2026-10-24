package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.compat.ChemAtmosphereHooks;
import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.DockCapture;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import dev.leeeonidys.aeronauticsplus.space.core.PartImpact;
import dev.leeeonidys.aeronauticsplus.space.core.VesselState;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import dev.leeeonidys.aeronauticsplus.space.core.Aerodynamics;
import dev.leeeonidys.aeronauticsplus.space.core.AscentOutcome;
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
    private static final EntityDataAccessor<Float> HUD_Q =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HUD_AP =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HUD_PE =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> HUD_WARP =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HUD_HOLD =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> HUD_BODY =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> HULL_INTEGRITY =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> DAMAGED_PARTS =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.STRING);
    /** Surviving pile no longer compiles as a ship: it drifts, it cannot thrust or steer. */
    private static final EntityDataAccessor<Boolean> WRECK =
            SynchedEntityData.defineId(VesselEntity.class, EntityDataSerializers.BOOLEAN);

    private static final double STEP_SECONDS = 0.05;
    /** One rock contact per 0.2 s, so a graze does not grind the ship to dust in one tick. */
    private static final int IMPACT_COOLDOWN_TICKS = 4;

    private FlightLoop loop;
    private Player pendingRemount;
    private int impactCooldown;
    private Vec3 lastWorldPosition = Vec3.ZERO;
    /** World-space velocity against a static rock, metres per second. */
    private Vec3 worldVelocity = Vec3.ZERO;

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
        entityData.set(HULL_INTEGRITY, 1.0f);
        entityData.set(DAMAGED_PARTS, "");
        entityData.set(WRECK, false);
        impactCooldown = 0;
        lastWorldPosition = new Vec3(getX(), getY(), getZ());
        worldVelocity = Vec3.ZERO;
        syncHud();
        syncPosition();
        lastWorldPosition = new Vec3(getX(), getY(), getZ());
    }

    /** True when the previous contact has cooled down enough to take another. */
    public boolean asteroidImpactReady() {
        return impactCooldown <= 0;
    }

    /**
     * Velocity of the ship relative to a stationary rock, in metres per second.
     * The vessel moves by teleporting with its altitude, so {@code getDeltaMovement()}
     * stays zero — the position delta across a tick is the real relative motion.
     */
    public Vec3 worldVelocityMetersPerSecond() {
        return worldVelocity;
    }

    private void sampleWorldVelocity() {
        Vec3 now = new Vec3(getX(), getY(), getZ());
        Vec3 delta = now.subtract(lastWorldPosition).scale(20.0);
        lastWorldPosition = now;
        // Teleports are not impacts: clamp a single-tick jump to 200 m/s.
        worldVelocity = new Vec3(
                Math.max(-200.0, Math.min(200.0, delta.x)),
                Math.max(-200.0, Math.min(200.0, delta.y)),
                Math.max(-200.0, Math.min(200.0, delta.z)));
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

    public float hudDynamicPressurePascals() {
        return entityData.get(HUD_Q);
    }

    public float hudApoapsisAltitudeMeters() {
        return entityData.get(HUD_AP);
    }

    public float hudPeriapsisAltitudeMeters() {
        return entityData.get(HUD_PE);
    }

    public int hudWarp() {
        return Warp.factor(entityData.get(HUD_WARP));
    }

    public String hudHoldLabel() {
        return HoldAim.label(holdMode());
    }

    public String hudBodyId() {
        String id = entityData.get(HUD_BODY);
        return id == null || id.isBlank() ? "earth" : id;
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
        if (isWreck()) {
            return "Судно неуправляемо: тяги нет.";
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
        if (isWreck()) {
            return "Судно неуправляемо: тяги нет.";
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
        if (isWreck()) {
            return "Судно неуправляемо: РКО мертвы.";
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

    public String commandUndock() {
        if (loop == null) {
            return "Нет полёта.";
        }
        try {
            applyAxial(loop.undock());
            return "Расстыковка.";
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            return message == null || message.isBlank() ? "Нет стыка." : message;
        }
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
        if (impactCooldown > 0) {
            impactCooldown--;
        }
        sampleWorldVelocity();
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
                    loop.vessel(), ChemAtmosphereHooks.ambient(), STEP_SECONDS, Aerodynamics.ROCKET_CD, 1.0);
            if (outcome.has(FlightFault.HOLD_DOWN) || outcome.has(FlightFault.IMPACT)) {
                landAndDiscard();
                return true;
            }
            loop = loop.withVessel(outcome.vessel());
        } else {
            AscentOutcome coasted = VesselDynamics.advanceAscent(
                    loop.vessel(), ChemAtmosphereHooks.ambient(), STEP_SECONDS, Aerodynamics.ROCKET_CD, 1.0);
            loop = loop.withVessel(coasted.vessel());
            if (coasted.has(FlightFault.HOLD_DOWN)
                    || coasted.has(FlightFault.IMPACT)
                    || loop.vessel().orbit().altitudeMeters() <= LandingOutcome.TOUCHDOWN_ALTITUDE_METERS) {
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
        entityData.set(HUD_Q, (float) ChemAtmosphereHooks.dynamicPressurePascals(orbit));
        entityData.set(HUD_AP, (float) orbit.apoapsisAltitudeMeters());
        entityData.set(HUD_PE, (float) orbit.periapsisAltitudeMeters());
        entityData.set(HUD_BODY, orbit.centralBody().id());
    }

    /**
     * Local asteroid damage. The cell that touched the rock takes the hit, scaled by the
     * closing speed along that face and by the part's own toughness: a wing edge shears
     * at walking pace, a structural cell only dents. Hull integrity is the fraction of the
     * ship's mass still attached, so losing the seat or the last engine ends the vessel
     * the same way it would in reality — it stops being a ship.
     *
     * <p>No ChemMod gas import: a pressure breach is reported as a stub until ChemMod
     * publishes tank contents.
     */
    public void applyAsteroidImpact(ImpactContact contact) {
        if (loop == null || level() == null || level().isClientSide || impactCooldown > 0) {
            return;
        }
        // Above 20 km the world entity is a placeholder for the map: there is no rock to hit.
        if (!WorldHandoff.inWorld(loop.vessel().orbit())) {
            return;
        }
        impactCooldown = IMPACT_COOLDOWN_TICKS;
        double load = contact.load();
        if (!(load > 0.0)) {
            return;
        }
        Map<String, Double> ledger = decodeDamage(entityData.get(DAMAGED_PARTS));
        String key = contact.cellKey();
        double total = ledger.getOrDefault(key, 0.0) + load;
        if (total < 1.0) {
            ledger.put(key, Math.min(total, 0.999));
            entityData.set(DAMAGED_PARTS, encodeDamage(ledger));
            if (PartImpact.penetration(load)) {
                tellPilot("Удар: " + partName(contact.kind()) + " — трещина "
                        + percent(total) + ", целостность " + percent(entityData.get(HULL_INTEGRITY)) + ".");
            }
            return;
        }
        destroyCell(contact, ledger);
    }

    private void destroyCell(ImpactContact contact, Map<String, Double> ledger) {
        double shipMass = massOf(loop.flying().occupants());
        String payload = pruneDisconnected(dropCell(entityData.get(PARTS), contact));
        ledger.remove(contact.cellKey());
        entityData.set(DAMAGED_PARTS, encodeDamage(ledger));
        if (payload.isBlank() || !rebuildFromPayload(payload)) {
            totalLoss(contact);
            return;
        }
        // Integrity is the mass still attached, so a torn-off wing panel counts as well.
        double leftMass = massOf(loop.flying().occupants());
        double remaining = shipMass > 0.0 ? Math.min(1.0, leftMass / shipMass) : 0.0;
        float integrity = (float) Math.max(0.0f, entityData.get(HULL_INTEGRITY) * remaining);
        entityData.set(HULL_INTEGRITY, integrity);
        tellPilot("Разрушено: " + partName(contact.kind()) + " — " + consequenceText(contact.consequence())
                + ". Целостность " + percent(integrity) + ".");
        if (contact.consequence() == PartImpact.Consequence.PRESSURE) {
            Player seated = pilot();
            if (seated != null) {
                seated.hurt(level().damageSources().generic(), 2.0f);
            }
        }
        if (integrity <= 0.0f) {
            totalLoss(contact);
        }
    }

    /**
     * A cut cell can leave pieces that are no longer attached: shear the wing root and the
     * outer panel is not part of the ship any more. Keep the component that holds the seat
     * (or the heaviest one when the seat itself was hit) and drop the rest.
     * Detached pieces are lost; they do not become debris blocks yet.
     */
    private String pruneDisconnected(String payload) {
        GridPos pad = new GridPos(padPos().getX(), padPos().getY(), padPos().getZ());
        VesselBlockGrid grid = FlightCodec.flyingGrid(payload, pad);
        if (grid.isEmpty()) {
            return "";
        }
        List<VesselBlockOccupant> occupants = grid.occupants();
        Map<GridPos, VesselBlockOccupant> byPos = new java.util.HashMap<>();
        for (VesselBlockOccupant occupant : occupants) {
            byPos.put(occupant.pos(), occupant);
        }
        List<List<VesselBlockOccupant>> components = new ArrayList<>();
        java.util.Set<GridPos> seen = new java.util.HashSet<>();
        for (VesselBlockOccupant start : occupants) {
            if (!seen.add(start.pos())) {
                continue;
            }
            List<VesselBlockOccupant> component = new ArrayList<>();
            java.util.ArrayDeque<GridPos> queue = new java.util.ArrayDeque<>();
            queue.add(start.pos());
            while (!queue.isEmpty()) {
                GridPos pos = queue.remove();
                VesselBlockOccupant cell = byPos.get(pos);
                if (cell == null) {
                    continue;
                }
                component.add(cell);
                for (BlockFace face : BlockFace.values()) {
                    GridPos next = pos.translate(face.dx(), face.dy(), face.dz());
                    if (byPos.containsKey(next) && seen.add(next)) {
                        queue.add(next);
                    }
                }
            }
            components.add(component);
        }
        if (components.size() <= 1) {
            return payload;
        }
        List<VesselBlockOccupant> keep = null;
        for (List<VesselBlockOccupant> component : components) {
            if (hasController(component)) {
                keep = component;
                break;
            }
        }
        if (keep == null) {
            double heaviest = -1.0;
            for (List<VesselBlockOccupant> component : components) {
                double mass = 0.0;
                for (VesselBlockOccupant cell : component) {
                    mass += cell.spec().massKg();
                }
                if (mass > heaviest) {
                    heaviest = mass;
                    keep = component;
                }
            }
        }
        int lost = occupants.size() - (keep == null ? 0 : keep.size());
        if (lost > 0) {
            tellPilot("Оторвано от корпуса: " + lost + " кл. — эти детали больше не часть корабля.");
        }
        return encodeCells(keep == null ? List.of() : keep, pad);
    }

    private static double massOf(List<VesselBlockOccupant> cells) {
        double mass = 0.0;
        for (VesselBlockOccupant cell : cells) {
            mass += cell.spec().massKg();
        }
        return mass;
    }

    private static boolean hasController(List<VesselBlockOccupant> cells) {
        for (VesselBlockOccupant cell : cells) {
            if (cell.spec().kind() == VesselPartKind.CONTROLLER) {
                return true;
            }
        }
        return false;
    }

    private static String encodeCells(List<VesselBlockOccupant> cells, GridPos pad) {
        StringBuilder builder = new StringBuilder();
        for (VesselBlockOccupant cell : cells) {
            if (!builder.isEmpty()) {
                builder.append(';');
            }
            builder.append(cell.spec().id())
                    .append(',')
                    .append(cell.pos().x() - pad.x())
                    .append(',')
                    .append(cell.pos().y() - pad.y())
                    .append(',')
                    .append(cell.pos().z() - pad.z())
                    .append(',')
                    .append(cell.facing().name());
        }
        return builder.toString();
    }

    /**
     * Rebuild the ship from the surviving payload. If what is left no longer compiles as a
     * vessel (no seat, no engine, no fuel path) it does not disappear — it becomes a hulk
     * that drifts with zero thrust, and the crew can still be recovered. Only an empty
     * payload is a total loss.
     */
    private boolean rebuildFromPayload(String payload) {
        GridPos pad = new GridPos(padPos().getX(), padPos().getY(), padPos().getZ());
        VesselBlockGrid flying = FlightCodec.flyingGrid(payload, pad);
        if (flying.isEmpty()) {
            return false;
        }
        VesselState state;
        boolean flyable;
        try {
            state = VesselBlockCompiler.analyze(flying)
                    .toVesselState("flight", loop.vessel().orbit())
                    .withAttitude(loop.vessel().attitude())
                    .withLiveThrottle(loop.vessel().liveThrottle());
            flyable = true;
        } catch (RuntimeException noLongerAShip) {
            state = loop.vessel().withLiveThrottle(0.0);
            flyable = false;
        }
        List<VesselBlockOccupant> stacked = new ArrayList<>();
        stacked.add(new VesselBlockOccupant(pad, VesselPartCatalog.PAD, BlockFace.UP));
        stacked.addAll(flying.occupants());
        loop = new FlightLoop(
                loop.presence(),
                new VesselBlockGrid(stacked),
                loop.padLeft(),
                flying,
                state,
                loop.recovery());
        entityData.set(PARTS, FlightCodec.encodeFlying(loop));
        entityData.set(WRECK, !flyable);
        if (!flyable) {
            tellPilot("Остатки корпуса больше не корабль: тяги и управления нет. Экипаж можно снять.");
        }
        syncHud();
        return true;
    }

    /** True when the surviving pile can no longer thrust or steer. */
    public boolean isWreck() {
        return entityData.get(WRECK);
    }

    private void totalLoss(ImpactContact contact) {
        entityData.set(HULL_INTEGRITY, 0.0f);
        tellPilot("Судно разрушено: " + partName(contact.kind()) + " — "
                + consequenceText(contact.consequence()) + ".");
        for (Entity passenger : new ArrayList<>(getPassengers())) {
            if (passenger instanceof Player player) {
                player.hurt(level().damageSources().generic(), 1_000.0f);
            }
        }
        ejectPassengers();
        discard();
    }

    private static String dropCell(String payload, ImpactContact contact) {
        if (payload == null || payload.isBlank()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String token : payload.split(";")) {
            String[] bits = token.split(",");
            if (bits.length != 5) {
                continue;
            }
            try {
                boolean same = Integer.parseInt(bits[1]) == contact.cellX()
                        && Integer.parseInt(bits[2]) == contact.cellY()
                        && Integer.parseInt(bits[3]) == contact.cellZ();
                if (same) {
                    continue;
                }
            } catch (NumberFormatException corruptCell) {
                // keep an unreadable token rather than silently deleting a part
            }
            if (!builder.isEmpty()) {
                builder.append(';');
            }
            builder.append(token);
        }
        return builder.toString();
    }

    private static Map<String, Double> decodeDamage(String payload) {
        Map<String, Double> ledger = new java.util.HashMap<>();
        if (payload == null || payload.isBlank()) {
            return ledger;
        }
        for (String token : payload.split(";")) {
            int equals = token.indexOf('=');
            if (equals <= 0) {
                continue;
            }
            try {
                ledger.put(token.substring(0, equals), Double.parseDouble(token.substring(equals + 1)));
            } catch (NumberFormatException ignored) {
                // drop a corrupt ledger entry
            }
        }
        return ledger;
    }

    private static String encodeDamage(Map<String, Double> ledger) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Double> entry : ledger.entrySet()) {
            if (builder.length() > 900) {
                break;
            }
            if (!builder.isEmpty()) {
                builder.append(';');
            }
            builder.append(entry.getKey()).append('=')
                    .append(String.format(java.util.Locale.ROOT, "%.3f", entry.getValue()));
        }
        return builder.toString();
    }

    private void tellPilot(String text) {
        Player seated = pilot();
        if (seated != null) {
            seated.displayClientMessage(Component.literal(text), false);
        }
    }

    private static String percent(double fraction) {
        return String.format(java.util.Locale.ROOT, "%.0f%%", Math.max(0.0, Math.min(1.0, fraction)) * 100.0);
    }

    private static String partName(VesselPartKind kind) {
        return switch (kind) {
            case STRUCTURE -> "силовая клетка";
            case TANK -> "топливный бак";
            case ENGINE -> "двигатель";
            case SEPARATOR -> "пиро-кольцо";
            case MOUNT -> "транспортное крепление";
            case PAD -> "стартовый стол";
            case FAIRING -> "обтекатель";
            case PAYLOAD -> "полезная нагрузка";
            case HABITAT -> "бытовой отсек";
            case SOLAR -> "солнечная панель";
            case GYRO -> "гиродин";
            case RCS -> "РКО";
            case DOCKING -> "стыковочный узел";
            case BATTERY -> "аккумуляторный отсек";
            case RADIATOR -> "радиатор";
            case ANTENNA -> "антенна";
            case LAB -> "лаборатория";
            case OXYGEN -> "кислородный отсек";
            case TRANSPONDER -> "транспондер";
            case TELESCOPE -> "телескоп";
            case CONTROLLER -> "кресло управления";
            case LEGS -> "опоры";
            case WING -> "крыло";
        };
    }

    private static String consequenceText(PartImpact.Consequence consequence) {
        return switch (consequence) {
            case STRUCTURE -> "пробита силовая клетка, корпус держит хуже";
            case POWER -> "солнечная панель сорвана, выработки энергии нет";
            case STORAGE -> "аккумуляторы разбиты, запас на затмение потерян";
            case COMMS -> "антенна смята, связи нет";
            case PRESSURE -> "разгерметизация, воздух уходит (заглушка: газ ChemMod ещё не опубликован)";
            case PROPELLANT -> "бак разорван, топливо этого отсека потеряно";
            case THRUST -> "двигатель выбит, тяги нет";
            case CONTROL -> "кресло управления разрушено, управлять некому";
            case ATTITUDE -> "контур ориентации выбит, угловая управляемость упала";
            case THERMAL -> "радиатор пробит, тепло не сбрасывается";
            case AERO -> "панель обтекателя сорвана, нагрузка под потоком";
            case SENSOR -> "телескоп разбит";
            case DOCKING -> "стыковочное кольцо деформировано";
            case CARGO -> "полезная нагрузка повреждена";
            case SURFACE -> "крыло сорвано с корня, несущей поверхности нет";
        };
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
        builder.define(HUD_Q, 0.0f);
        builder.define(HUD_AP, 0.0f);
        builder.define(HUD_PE, 0.0f);
        builder.define(HUD_WARP, 0);
        builder.define(HUD_HOLD, 0);
        builder.define(HUD_BODY, "earth");
        builder.define(HULL_INTEGRITY, 1.0f);
        builder.define(DAMAGED_PARTS, "");
        builder.define(WRECK, false);
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
        entityData.set(HULL_INTEGRITY, tag.getFloat("HullIntegrity"));
        entityData.set(DAMAGED_PARTS, tag.getString("DamagedParts"));
        entityData.set(WRECK, tag.getBoolean("Wreck"));
        restoreLoop(tag);
        impactCooldown = 0;
        lastWorldPosition = new Vec3(getX(), getY(), getZ());
        worldVelocity = Vec3.ZERO;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Parts", entityData.get(PARTS));
        tag.putInt("PadX", entityData.get(PAD_X));
        tag.putInt("PadY", entityData.get(PAD_Y));
        tag.putInt("PadZ", entityData.get(PAD_Z));
        tag.putInt("WarpIndex", entityData.get(HUD_WARP));
        tag.putInt("HoldMode", entityData.get(HUD_HOLD));
        tag.putFloat("HullIntegrity", entityData.get(HULL_INTEGRITY));
        tag.putString("DamagedParts", entityData.get(DAMAGED_PARTS));
        tag.putBoolean("Wreck", entityData.get(WRECK));
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
