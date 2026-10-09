package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.ArrayList;
import java.util.List;

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
        return attemptBurn(vessel, durationSeconds).vessel();
    }

    /** Burns and applies the rocket-equation delta-v in a supplied inertial thrust direction. */
    public static VesselState burnActiveStage(VesselState vessel, double durationSeconds,
                                               Vector3d thrustDirection) {
        return attemptBurn(vessel, durationSeconds, thrustDirection, Double.NaN).vessel();
    }

    public static BurnOutcome attemptBurn(VesselState vessel, double requestedSeconds) {
        Vector3d netThrust = vessel.activeStage().netThrustNewtons();
        Vector3d direction = netThrust.magnitudeSquared() > 0.0 ? netThrust : Vector3d.UNIT_Y;
        return attemptBurn(vessel, requestedSeconds, direction, Double.NaN);
    }

    /**
     * Attempts a burn. Dry tank, zero thrust and a mid-burn feed break become named faults
     * instead of silent no-ops. {@code feedBreakAtSeconds} is ignored when NaN or negative.
     */
    public static BurnOutcome attemptBurn(VesselState vessel, double requestedSeconds,
                                          Vector3d thrustDirection, double feedBreakAtSeconds) {
        if (requestedSeconds < 0.0 || !Double.isFinite(requestedSeconds)) {
            throw new IllegalArgumentException("Burn duration must be finite and non-negative");
        }
        List<FlightFault> faults = new ArrayList<>();
        if (!(vessel.activeStage().thrustNewtons() > 0.0)) {
            faults.add(FlightFault.zeroThrust());
            return new BurnOutcome(vessel, requestedSeconds, 0.0, 0.0, faults);
        }
        if (thrustDirection == null || !(thrustDirection.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Thrust direction must be finite and non-zero");
        }

        boolean feedBreak = Double.isFinite(feedBreakAtSeconds) && feedBreakAtSeconds >= 0.0;
        if (feedBreak && feedBreakAtSeconds == 0.0) {
            VesselState starved = replaceActive(vessel, vessel.activeStage().withDisabledEngines());
            faults.add(FlightFault.feedBreak(0.0));
            return new BurnOutcome(starved, requestedSeconds, 0.0, 0.0, faults);
        }

        double allowed = requestedSeconds;
        if (feedBreak) {
            allowed = Math.min(allowed, feedBreakAtSeconds);
        }

        double initialMass = vessel.totalMassKg();
        double exhaustVelocity = vessel.activeStage().effectiveExhaustVelocityMetersPerSecond();
        StageBurnResult burn = vessel.activeStage().burn(allowed);
        StageState nextStage = burn.stage();
        if (feedBreak && burn.elapsedSeconds() + 1.0e-9 >= feedBreakAtSeconds
                && burn.elapsedSeconds() + 1.0e-9 < requestedSeconds
                && !burn.propellantDepleted()) {
            nextStage = nextStage.withDisabledEngines();
            faults.add(FlightFault.feedBreak(burn.elapsedSeconds()));
        }
        if (burn.propellantDepleted()) {
            faults.add(FlightFault.dryTank(burn.elapsedSeconds()));
        }

        VesselState consumed = replaceActive(vessel, nextStage);
        double finalMass = consumed.totalMassKg();
        double deltaV = exhaustVelocity > 0.0 && finalMass > 0.0 && initialMass > finalMass
                ? exhaustVelocity * Math.log(initialMass / finalMass) : 0.0;
        VesselState flown = deltaV > 0.0
                ? applyImpulse(consumed, thrustDirection.normalized().multiply(deltaV))
                : consumed;
        return new BurnOutcome(flown, requestedSeconds, burn.elapsedSeconds(), deltaV, faults);
    }

    public static VesselState consumeActiveStage(VesselState vessel, double durationSeconds) {
        return replaceActive(vessel, vessel.activeStage().burn(durationSeconds).stage());
    }

    public static VesselState separateActiveStage(VesselState vessel) {
        return vessel.separateActiveStage();
    }

    private static VesselState replaceActive(VesselState vessel, StageState nextStage) {
        ArrayList<StageState> stages = new ArrayList<>(vessel.stages());
        stages.set(vessel.activeStageIndex(), nextStage);
        return new VesselState(vessel.id(), vessel.orbit(), stages, vessel.activeStageIndex());
    }
}
