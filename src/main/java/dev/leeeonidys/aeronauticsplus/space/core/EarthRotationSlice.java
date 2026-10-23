package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Sidereal Earth rotation on the pad: free eastward speed. Not ChemMod world gas.
 */
public final class EarthRotationSlice {
    private EarthRotationSlice() {
    }

    public record Result(
            double inertialHorizontal,
            double rotatingHorizontal,
            double flownHorizontal,
            double flownAltitude) {
    }

    public static Result execute() {
        double inertial = SpaceBodies.pad().horizontalSpeedMetersPerSecond();
        if (inertial > 1.0) {
            throw new IllegalStateException("Inertial pad must sit still, horiz=" + inertial);
        }
        OrbitState rotating = SpaceBodies.rotatingPad();
        double rotatingHoriz = rotating.horizontalSpeedMetersPerSecond();
        if (Math.abs(rotatingHoriz - 465.0) > 10.0) {
            throw new IllegalStateException("Equator pad must be ~465 m/s east, horiz=" + rotatingHoriz);
        }
        if (!(rotating.velocityMetersPerSecond().z() > 400.0)) {
            throw new IllegalStateException("East must be +Z on the +X equator pad");
        }
        if (Math.abs(rotating.altitudeMeters()) > 1.0) {
            throw new IllegalStateException("Rotating pad is still on the surface");
        }

        GravityTurnOutcome inertialHop = VesselDynamics.attemptGravityTurn(
                stack(SpaceBodies.pad()), PitchProgram.vertical(), 40.0,
                VesselDynamics.DEFAULT_ASCENT_STEP_SECONDS);
        if (!inertialHop.completed() || inertialHop.horizontalSpeedMetersPerSecond() > 1.0) {
            throw new IllegalStateException(
                    "Inertial vertical hop must stay radial, horiz=" + inertialHop.horizontalSpeedMetersPerSecond());
        }

        GravityTurnOutcome flown = VesselDynamics.attemptGravityTurn(
                stack(SpaceBodies.rotatingPad()), PitchProgram.vertical(), 40.0,
                VesselDynamics.DEFAULT_ASCENT_STEP_SECONDS);
        if (!flown.completed() || flown.has(FlightFault.HOLD_DOWN) || flown.has(FlightFault.IMPACT)) {
            throw new IllegalStateException("Rotating pad must still lift off: " + flown.faultText());
        }
        if (!(flown.horizontalSpeedMetersPerSecond() > 400.0)) {
            throw new IllegalStateException(
                    "Sidereal east must remain after a vertical hop, horiz=" + flown.horizontalSpeedMetersPerSecond());
        }
        if (!(flown.altitudeMeters() > 1_000.0)) {
            throw new IllegalStateException("Rotating pad hop must climb, h=" + flown.altitudeMeters());
        }
        if (!(flown.horizontalSpeedMetersPerSecond() > inertialHop.horizontalSpeedMetersPerSecond() + 400.0)) {
            throw new IllegalStateException("Earth rotation must outrun an inertial vertical hop");
        }
        return new Result(inertial, rotatingHoriz, flown.horizontalSpeedMetersPerSecond(), flown.altitudeMeters());
    }

    private static VesselState stack(OrbitState pad) {
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
        return new VesselState("earth-rotation-demo", pad, List.of(stage), 0, radial);
    }
}
