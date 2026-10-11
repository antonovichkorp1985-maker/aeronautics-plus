package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Seated look pitches the stack. Not ChemMod air, not the orientation GUI.
 */
public final class LookSteerSlice {
    private LookSteerSlice() {
    }

    public record Result(
            boolean skyIsRadial,
            boolean horizonIsOffRadial,
            double verticalHorizontal,
            double pitchedHorizontal) {
    }

    public static Result execute() {
        OrbitState pad = SpaceBodies.pad();
        Vector3d sky = LookGuidance.inertialAim(pad, 0.0, -90.0);
        if (sky.dot(pad.radialUnit()) < 0.99) {
            throw new IllegalStateException("Looking at the sky must aim radial, got " + sky);
        }
        Vector3d pitchedAim = LookGuidance.inertialAim(pad, 0.0, -45.0);
        double pitchedDot = pitchedAim.dot(pad.radialUnit());
        if (pitchedDot < 0.55 || pitchedDot > 0.85) {
            throw new IllegalStateException("A 45° look must sit between radial and horizon, got " + pitchedAim);
        }

        VesselState vertical = fly(LookGuidance.attitudeFromLook(pad, 0.0, -90.0));
        VesselState pitched = fly(LookGuidance.attitudeFromLook(pad, 0.0, -45.0));
        double vHoriz = vertical.orbit().horizontalSpeedMetersPerSecond();
        double pHoriz = pitched.orbit().horizontalSpeedMetersPerSecond();
        if (vHoriz > 15.0) {
            throw new IllegalStateException("A sky look must stay nearly radial, horiz=" + vHoriz);
        }
        if (!(pHoriz > vHoriz + 40.0)) {
            throw new IllegalStateException(
                    "A horizon look must build downrange, vertical=" + vHoriz + " pitched=" + pHoriz);
        }
        return new Result(true, true, vHoriz, pHoriz);
    }

    private static VesselState fly(Attitude attitude) {
        VesselState state = stack().withAttitude(attitude);
        for (int i = 0; i < 80; i++) {
            AscentOutcome step = VesselDynamics.advanceAscent(
                    state, Atmosphere.earth(), 0.1, Aerodynamics.ROCKET_CD, 1.0);
            state = step.vessel();
        }
        return state;
    }

    private static VesselState stack() {
        TankState tank = new TankState(
                "tank", 80.0, 1_000.0,
                new PropellantState("rp1", "lox", 200.0, 460.0),
                Vector3d.ZERO);
        EngineState engine = new EngineState(
                "engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                1.0, 8.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
        StageState stage = new StageState(
                "stage-0",
                List.of(new MassElement("frame", 120.0, Vector3d.ZERO)),
                List.of(tank),
                List.of(engine),
                true);
        return new VesselState("look-steer-demo", SpaceBodies.pad(), List.of(stage), 0);
    }
}
