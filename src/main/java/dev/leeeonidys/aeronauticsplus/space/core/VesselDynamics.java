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

    /**
     * Burns the active stage and applies rocket-equation delta-v along the net engine axis.
     * Until an attitude layer exists, the stage-local thrust axis is treated as inertial.
     */
    public static VesselState burnActiveStage(VesselState vessel, double durationSeconds) {
        Vector3d netThrust = vessel.activeStage().netThrustNewtons();
        if (netThrust.magnitudeSquared() > 0.0) {
            return burnActiveStage(vessel, durationSeconds, netThrust);
        }
        return consumeActiveStage(vessel, durationSeconds);
    }

    /** Burns and applies the rocket-equation delta-v in a supplied inertial thrust direction. */
    public static VesselState burnActiveStage(VesselState vessel, double durationSeconds,
                                               Vector3d thrustDirection) {
        if (thrustDirection == null || !(thrustDirection.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Thrust direction must be finite and non-zero");
        }
        double initialMass = vessel.totalMassKg();
        double exhaustVelocity = vessel.activeStage().effectiveExhaustVelocityMetersPerSecond();
        VesselState burned = consumeActiveStage(vessel, durationSeconds);
        double finalMass = burned.totalMassKg();
        double deltaV = exhaustVelocity > 0.0 && finalMass > 0.0 && initialMass > finalMass
                ? exhaustVelocity * Math.log(initialMass / finalMass) : 0.0;
        return deltaV > 0.0
                ? applyImpulse(burned, thrustDirection.normalized().multiply(deltaV))
                : burned;
    }

    public static VesselState consumeActiveStage(VesselState vessel, double durationSeconds) {
        StageBurnResult burn = vessel.activeStage().burn(durationSeconds);
        java.util.ArrayList<StageState> stages = new java.util.ArrayList<>(vessel.stages());
        stages.set(vessel.activeStageIndex(), burn.stage());
        return new VesselState(vessel.id(), vessel.orbit(), stages, vessel.activeStageIndex());
    }

    public static VesselState separateActiveStage(VesselState vessel) {
        return vessel.separateActiveStage();
    }
}
