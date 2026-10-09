package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Deterministic proof of in-flight faults: dry tank, zero thrust and a feed break
 * during burn. Orientation GUI is intentionally absent.
 */
public final class FailureSlice {
    private FailureSlice() {
    }

    public record Result(String dryTank, String zeroThrust, String feedBreak, double feedBreakElapsed) {
    }

    public static Result execute() {
        VesselState fueled = fueledVessel();
        double mass0 = fueled.totalMassKg();
        double speed0 = fueled.orbit().velocityMetersPerSecond().magnitude();

        BurnOutcome nominal = VesselDynamics.attemptBurn(fueled, 2.0);
        if (!nominal.completed() || !(nominal.vessel().totalMassKg() < mass0 - 0.5)
                || !(nominal.deltaVMetersPerSecond() > 0.0)) {
            throw new IllegalStateException("Healthy burn must complete with mass loss and delta-v");
        }

        BurnOutcome dry = VesselDynamics.attemptBurn(fueled, 1_000.0);
        if (!dry.has(FlightFault.DRY_TANK) || dry.completed()
                || !(dry.elapsedSeconds() < 1_000.0)
                || !(dry.vessel().activeStage().propellantMassKg() < 1.0e-6)) {
            throw new IllegalStateException("Over-long burn must stop on a dry tank:\n" + dry.faultText());
        }
        if (!(dry.vessel().orbit().velocityMetersPerSecond().magnitude() > speed0)) {
            throw new IllegalStateException("Dry-tank cutoff must still apply the burned delta-v");
        }

        VesselState dead = replaceEngines(fueled, false);
        BurnOutcome zero = VesselDynamics.attemptBurn(dead, 5.0);
        if (!zero.has(FlightFault.ZERO_THRUST)
                || zero.elapsedSeconds() != 0.0
                || Math.abs(zero.vessel().totalMassKg() - mass0) > 1.0e-9
                || Math.abs(zero.vessel().orbit().velocityMetersPerSecond().magnitude() - speed0) > 1.0e-9) {
            throw new IllegalStateException("Disabled engines must report zero thrust and change nothing:\n"
                    + zero.faultText());
        }

        BurnOutcome broken = VesselDynamics.attemptBurn(fueled, 8.0, Vector3d.UNIT_Y, 1.0);
        if (!broken.has(FlightFault.FEED_BREAK)
                || Math.abs(broken.elapsedSeconds() - 1.0) > 1.0e-6
                || broken.has(FlightFault.DRY_TANK)
                || !(broken.vessel().totalMassKg() < mass0 - 0.5)
                || !(broken.vessel().totalMassKg() > dry.vessel().totalMassKg() + 1.0)) {
            throw new IllegalStateException(
                    "Feed break must abort the remaining burn after the break time:\n" + broken.faultText());
        }
        if (broken.vessel().activeStage().thrustNewtons() > 0.0) {
            throw new IllegalStateException("Broken feed must leave engines starved");
        }
        BurnOutcome afterBreak = VesselDynamics.attemptBurn(broken.vessel(), 2.0);
        if (!afterBreak.has(FlightFault.ZERO_THRUST)) {
            throw new IllegalStateException("A later burn after feed break must see zero thrust");
        }

        return new Result(dry.faultText(), zero.faultText(), broken.faultText(), broken.elapsedSeconds());
    }

    private static VesselState fueledVessel() {
        TankState tank = new TankState(
                "tank", 80.0, 1_000.0,
                new PropellantState("rp1", "lox", 50.0, 100.0),
                Vector3d.ZERO);
        EngineState engine = new EngineState(
                "engine", "rp1", "lox", 90.0, 20_000.0, 300.0,
                1.0, 0.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
        StageState stage = new StageState(
                "stage-0",
                List.of(new MassElement("frame", 120.0, Vector3d.ZERO)),
                List.of(tank),
                List.of(engine),
                true);
        return new VesselState("failure-demo", SpaceBodies.parkingOrbit(), List.of(stage), 0);
    }

    private static VesselState replaceEngines(VesselState vessel, boolean enabled) {
        StageState stage = vessel.activeStage();
        StageState next = enabled ? stage : stage.withDisabledEngines();
        return new VesselState(vessel.id(), vessel.orbit(), List.of(next), 0, vessel.attitude());
    }
}
