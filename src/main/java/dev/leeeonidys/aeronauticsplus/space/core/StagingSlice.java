package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Hot-staging on a vacuum gravity turn: dry booster drops, upper stage keeps thrusting.
 * Not ChemMod world gas.
 */
public final class StagingSlice {
    private StagingSlice() {
    }

    public record Result(
            int stagedStagesLeft,
            double stagedAltitude,
            double stuckAltitude,
            boolean stuckDry) {
    }

    public static Result execute() {
        GravityTurnOutcome staged = VesselDynamics.attemptGravityTurn(
                stack(true), PitchProgram.vertical(), 50.0, VesselDynamics.DEFAULT_ASCENT_STEP_SECONDS);
        if (staged.has(FlightFault.HOLD_DOWN) || staged.has(FlightFault.IMPACT) || staged.has(FlightFault.DRY_TANK)) {
            throw new IllegalStateException("Separable stack must stage through burnout: " + staged.faultText());
        }
        if (staged.vessel().stages().size() != 1) {
            throw new IllegalStateException(
                    "Hot-staging must drop the booster, stages=" + staged.vessel().stages().size());
        }
        if (!(staged.elapsedSeconds() > 45.0)) {
            throw new IllegalStateException("Upper stage must keep burning after sep, t=" + staged.elapsedSeconds());
        }
        if (!(staged.altitudeMeters() > 1_000.0)) {
            throw new IllegalStateException("Staged hop must climb, h=" + staged.altitudeMeters());
        }

        GravityTurnOutcome stuck = VesselDynamics.attemptGravityTurn(
                stack(false), PitchProgram.vertical(), 50.0, VesselDynamics.DEFAULT_ASCENT_STEP_SECONDS);
        if (!stuck.has(FlightFault.DRY_TANK)) {
            throw new IllegalStateException("A glued booster must dry and stop, not stage");
        }
        if (stuck.vessel().stages().size() != 2) {
            throw new IllegalStateException("Glued stack must keep the dead booster");
        }
        if (!(staged.altitudeMeters() > stuck.altitudeMeters() + 100.0)) {
            throw new IllegalStateException(
                    "Staging must out-climb dead mass, staged=" + staged.altitudeMeters()
                            + " stuck=" + stuck.altitudeMeters());
        }

        return new Result(
                staged.vessel().stages().size(),
                staged.altitudeMeters(),
                stuck.altitudeMeters(),
                stuck.has(FlightFault.DRY_TANK));
    }

    private static VesselState stack(boolean boosterSeparable) {
        StageState booster = new StageState(
                "booster",
                List.of(new MassElement("booster-frame", 100.0, Vector3d.ZERO)),
                List.of(new TankState(
                        "booster-tank", 50.0, 300.0,
                        new PropellantState("rp1", "lox", 80.0, 184.0),
                        Vector3d.ZERO)),
                List.of(new EngineState(
                        "booster-engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                        1.0, 0.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y)),
                boosterSeparable);
        StageState upper = new StageState(
                "upper",
                List.of(new MassElement("upper-frame", 80.0, new Vector3d(0.0, 2.0, 0.0))),
                List.of(new TankState(
                        "upper-tank", 40.0, 250.0,
                        new PropellantState("rp1", "lox", 60.0, 138.0),
                        new Vector3d(0.0, 2.0, 0.0))),
                List.of(new EngineState(
                        "upper-engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                        1.0, 0.0, true, new Vector3d(0.0, 1.5, 0.0), Vector3d.UNIT_Y)),
                false);
        Attitude radial = Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X);
        return new VesselState("staging-demo", SpaceBodies.pad(), List.of(booster, upper), 0, radial);
    }
}
