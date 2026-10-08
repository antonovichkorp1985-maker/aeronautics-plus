package dev.leeeonidys.aeronauticsplus.space.core;

/** Operations that change a vessel flight state without rendering or Minecraft dependencies. */
public final class VesselDynamics {
    private VesselDynamics() {
    }

    public static VesselState applyImpulse(VesselState vessel, Vector3d deltaVelocity) {
        if (deltaVelocity == null) {
            throw new IllegalArgumentException("Delta-v must not be null");
        }
        return vessel.withOrbit(OrbitalSimulator.applyImpulse(
                vessel.orbit(),
                new Maneuver(vessel.orbit().epochSeconds(), deltaVelocity, 0.0)));
    }

    public static VesselState propagate(VesselState vessel, double durationSeconds, double stepSeconds) {
        return vessel.withOrbit(OrbitalSimulator.propagate(vessel.orbit(), durationSeconds, stepSeconds));
    }

    public static VesselState burnActiveStage(VesselState vessel, double durationSeconds) {
        StageBurnResult burn = vessel.activeStage().burn(durationSeconds);
        java.util.ArrayList<StageState> stages = new java.util.ArrayList<>(vessel.stages());
        stages.set(vessel.activeStageIndex(), burn.stage());
        return new VesselState(vessel.id(), vessel.orbit(), stages, vessel.activeStageIndex());
    }

    public static VesselState separateActiveStage(VesselState vessel) {
        return vessel.separateActiveStage();
    }
}
