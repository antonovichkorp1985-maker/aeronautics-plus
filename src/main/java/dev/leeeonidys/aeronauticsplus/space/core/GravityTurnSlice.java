package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Pitch kick then gravity turn. Guidance of the vehicle — not ChemMod world gas.
 */
public final class GravityTurnSlice {
    private GravityTurnSlice() {
    }

    public record Result(
            double verticalHorizontal,
            double turnedHorizontal,
            double turnedFlightPathDegrees,
            double turnedAltitude) {
    }

    public static Result execute() {
        VesselState stack = stack();
        GravityTurnOutcome vertical = VesselDynamics.attemptGravityTurn(
                stack, PitchProgram.vertical(), 40.0, VesselDynamics.DEFAULT_ASCENT_STEP_SECONDS);
        if (!vertical.completed() || vertical.has(FlightFault.HOLD_DOWN) || vertical.has(FlightFault.IMPACT)) {
            throw new IllegalStateException("Vertical hop must leave the pad: " + vertical.faultText());
        }
        if (!(vertical.altitudeMeters() > 1_000.0)) {
            throw new IllegalStateException("Vertical hop must climb, h=" + vertical.altitudeMeters());
        }
        if (vertical.horizontalSpeedMetersPerSecond() > 1.0) {
            throw new IllegalStateException(
                    "A zero-kick hop must stay radial, horiz=" + vertical.horizontalSpeedMetersPerSecond());
        }

        GravityTurnOutcome turned = VesselDynamics.attemptGravityTurn(
                stack, PitchProgram.kickThenTurn(), 40.0, VesselDynamics.DEFAULT_ASCENT_STEP_SECONDS);
        if (!turned.completed() || turned.has(FlightFault.HOLD_DOWN) || turned.has(FlightFault.IMPACT)) {
            throw new IllegalStateException("Gravity turn must leave the pad: " + turned.faultText());
        }
        if (!(turned.horizontalSpeedMetersPerSecond() > 50.0)) {
            throw new IllegalStateException(
                    "Pitch kick must build downrange speed, horiz=" + turned.horizontalSpeedMetersPerSecond());
        }
        if (!(turned.horizontalSpeedMetersPerSecond() > vertical.horizontalSpeedMetersPerSecond() + 50.0)) {
            throw new IllegalStateException("Turned downrange must exceed a vertical hop");
        }
        double fpaDeg = Math.toDegrees(turned.flightPathAngleRadians());
        if (!(fpaDeg < 85.0) || !(fpaDeg > 20.0)) {
            throw new IllegalStateException("Flight-path angle must come off vertical, fpa=" + fpaDeg);
        }
        if (!(turned.altitudeMeters() > 1_000.0)) {
            throw new IllegalStateException("Turn must still climb, h=" + turned.altitudeMeters());
        }

        return new Result(
                vertical.horizontalSpeedMetersPerSecond(),
                turned.horizontalSpeedMetersPerSecond(),
                fpaDeg,
                turned.altitudeMeters());
    }

    private static VesselState stack() {
        TankState tank = new TankState(
                "tank", 80.0, 1_000.0,
                new PropellantState("rp1", "lox", 200.0, 460.0),
                Vector3d.ZERO);
        EngineState engine = new EngineState(
                "engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                1.0, 0.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
        StageState stage = new StageState(
                "stage-0",
                List.of(new MassElement("frame", 120.0, Vector3d.ZERO)),
                List.of(tank),
                List.of(engine),
                true);
        Attitude radial = Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X);
        return new VesselState("gravity-turn-demo", SpaceBodies.pad(), List.of(stage), 0, radial);
    }
}
