package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

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

    public VesselState withAngularVelocity(Vector3d nextOmega) {
        if (nextOmega == null) {
            throw new IllegalArgumentException("Angular velocity must not be null");
        }
        return new VesselState(id, orbit, stages, activeStageIndex, attitude, nextOmega);
    }

    /** Drops the currently active stage and activates the next one. */
    public VesselState separateActiveStage() {
        if (activeStageIndex >= stages.size() - 1) {
            throw new IllegalStateException("Cannot separate the final active stage");
        }
        List<StageState> remaining = List.copyOf(stages.subList(activeStageIndex + 1, stages.size()));
        return new VesselState(id, orbit, remaining, 0, attitude, angularVelocityBody);
    }
}
