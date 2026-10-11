package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Logical flight state of a multi-stage vessel. Stages are ordered bottom to top. */
public record VesselState(
        String id,
        OrbitState orbit,
        List<StageState> stages,
        int activeStageIndex,
        Attitude attitude,
        Vector3d angularVelocityBody) {
    public VesselState {
        if (id == null || id.isBlank() || orbit == null) {
            throw new IllegalArgumentException("Vessel identity and orbit are required");
        }
        stages = List.copyOf(stages == null ? List.of() : stages);
        if (stages.isEmpty()) {
            throw new IllegalArgumentException("Vessel must contain at least one stage");
        }
        if (activeStageIndex < 0 || activeStageIndex >= stages.size()) {
            throw new IllegalArgumentException("Active stage index is outside the vessel");
        }
        if (attitude == null) {
            attitude = Attitude.IDENTITY;
        }
        if (angularVelocityBody == null) {
            angularVelocityBody = Vector3d.ZERO;
        }
    }

    public VesselState(String id, OrbitState orbit, List<StageState> stages, int activeStageIndex) {
        this(id, orbit, stages, activeStageIndex, Attitude.IDENTITY, Vector3d.ZERO);
    }

    public VesselState(String id, OrbitState orbit, List<StageState> stages, int activeStageIndex,
                       Attitude attitude) {
        this(id, orbit, stages, activeStageIndex, attitude, Vector3d.ZERO);
    }

    public StageState activeStage() {
        return stages.get(activeStageIndex);
    }

    /**
     * Strap-ons plus the core, burning together until radial pyro.
     * Axial upper stages wait. A stack with no strap-ons is just the first core.
     */
    public List<StageState> liveCluster() {
        List<StageState> cluster = new ArrayList<>();
        boolean coreSeen = false;
        for (StageState stage : stages) {
            if (stage.strapOn()) {
                cluster.add(stage);
            } else if (!coreSeen) {
                cluster.add(stage);
                coreSeen = true;
            }
        }
        if (cluster.isEmpty()) {
            return List.of(activeStage());
        }
        return List.copyOf(cluster);
    }

    public double clusterThrustNewtons() {
        return liveCluster().stream().mapToDouble(StageState::thrustNewtons).sum();
    }

    public double clusterThrustNewtonsAt(double ambientPascals) {
        return liveCluster().stream().mapToDouble(stage -> stage.thrustNewtonsAt(ambientPascals)).sum();
    }

    public Vector3d inertialClusterThrustNewtonsAt(double ambientPascals) {
        Vector3d sum = Vector3d.ZERO;
        for (StageState stage : liveCluster()) {
            sum = sum.add(attitude.toInertial(stage.netThrustNewtonsAt(ambientPascals)));
        }
        return sum;
    }

    public double livePropellantKg() {
        return liveCluster().stream().mapToDouble(StageState::propellantMassKg).sum();
    }

    public boolean hasStrapOns() {
        return stages.stream().anyMatch(StageState::strapOn);
    }

    public VesselState withStages(List<StageState> nextStages) {
        List<StageState> copy = List.copyOf(nextStages == null ? List.of() : nextStages);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Vessel must contain at least one stage");
        }
        int index = Math.min(activeStageIndex, copy.size() - 1);
        return new VesselState(id, orbit, copy, index, attitude, angularVelocityBody);
    }

    public double totalMassKg() {
        return stages.stream().mapToDouble(StageState::totalMassKg).sum();
    }

    public double remainingDeltaV() {
        return stages.subList(activeStageIndex, stages.size()).stream()
                .mapToDouble(StageState::idealDeltaV).sum();
    }

    /**
     * Centre of mass of remaining stages. The block compiler writes every stage in the
     * same grid frame, so this average is the stacked mass centre.
     */
    public Vector3d centerOfMassMeters() {
        double mass = 0.0;
        Vector3d moment = Vector3d.ZERO;
        for (StageState stage : stages.subList(activeStageIndex, stages.size())) {
            double stageMass = stage.totalMassKg();
            mass += stageMass;
            moment = moment.add(stage.centerOfMassMeters().multiply(stageMass));
        }
        if (!(mass > 0.0)) {
            throw new IllegalStateException("Vessel has no remaining mass");
        }
        return moment.multiply(1.0 / mass);
    }

    public ThrustGeometry activeThrustGeometry() {
        return activeStage().thrustGeometry();
    }

    /** Point-mass inertia of remaining stages about the stacked centre of mass. */
    public double momentOfInertiaKgM2(Vector3d axis) {
        Vector3d pivot = centerOfMassMeters();
        double inertia = 0.0;
        for (StageState stage : stages.subList(activeStageIndex, stages.size())) {
            inertia += stage.momentOfInertiaKgM2(pivot, axis);
        }
        return inertia;
    }

    /** Net engine thrust mapped through the current attitude into the inertial frame. */
    public Vector3d inertialThrustNewtons() {
        return attitude.toInertial(activeStage().netThrustNewtons());
    }

    public Vector3d inertialThrustNewtonsAt(double ambientPascals) {
        return attitude.toInertial(activeStage().netThrustNewtonsAt(ambientPascals));
    }

    public boolean hasResidualSpin() {
        return angularVelocityBody.magnitudeSquared() > 1.0e-18;
    }

    public VesselState withOrbit(OrbitState nextOrbit) {
        return new VesselState(id, nextOrbit, stages, activeStageIndex, attitude, angularVelocityBody);
    }

    public VesselState withAttitude(Attitude nextAttitude) {
        if (nextAttitude == null) {
            throw new IllegalArgumentException("Attitude must not be null");
        }
        return new VesselState(id, orbit, stages, activeStageIndex, nextAttitude, angularVelocityBody);
    }

    /**
     * Throttle the live cluster. Strap-ons and the core together; upper stages wait.
     * Not a GUI — the seated player sends a fraction in [0, 1].
     */
    public VesselState withLiveThrottle(double throttle) {
        Set<String> liveIds = new HashSet<>();
        for (StageState stage : liveCluster()) {
            liveIds.add(stage.id());
        }
        ArrayList<StageState> next = new ArrayList<>(stages.size());
        for (StageState stage : stages) {
            next.add(liveIds.contains(stage.id()) ? stage.withMainThrottle(throttle) : stage);
        }
        return withStages(next);
    }

    public double liveThrottle() {
        return liveCluster().stream()
                .flatMap(stage -> stage.mainEngines().stream())
                .mapToDouble(EngineState::throttle)
                .average()
                .orElse(0.0);
    }

    /** One detent is 10%. Clamped to [0, 1]. */
    public VesselState nudgeLiveThrottle(double delta) {
        if (!Double.isFinite(delta)) {
            throw new IllegalArgumentException("Throttle step must be finite");
        }
        double next = Math.round((liveThrottle() + delta) * 10.0) / 10.0;
        if (next < 0.0) {
            next = 0.0;
        }
        if (next > 1.0) {
            next = 1.0;
        }
        return withLiveThrottle(next);
    }

    public VesselState withAngularVelocity(Vector3d nextOmega) {
        if (nextOmega == null) {
            throw new IllegalArgumentException("Angular velocity must not be null");
        }
        return new VesselState(id, orbit, stages, activeStageIndex, attitude, nextOmega);
    }

    /** True when the live stage is marked separable and another stage sits above it. */
    public boolean canSeparateActive() {
        return activeStage().separable() && activeStageIndex < stages.size() - 1;
    }

    /** Drops the currently active stage and activates the next one. */
    public VesselState separateActiveStage() {
        if (!canSeparateActive()) {
            throw new IllegalStateException("Cannot separate the final active stage");
        }
        List<StageState> remaining = List.copyOf(stages.subList(activeStageIndex + 1, stages.size()));
        return new VesselState(id, orbit, remaining, 0, attitude, angularVelocityBody);
    }

    /** The live stage as its own vessel, still on the same trajectory. */
    public VesselState discardedBooster() {
        StageState spent = activeStage();
        StageState booster = new StageState(
                spent.id(), spent.structure(), spent.tanks(), spent.engines(), false);
        return new VesselState(id + "-booster", orbit, List.of(booster), 0, attitude, angularVelocityBody);
    }
}
