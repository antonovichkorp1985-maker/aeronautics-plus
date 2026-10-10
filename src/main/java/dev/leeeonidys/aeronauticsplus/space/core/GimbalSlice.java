package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Thrust-vector control: the nozzle gimbals, the stack does not teleport attitude.
 * Not ChemMod world gas.
 */
public final class GimbalSlice {
    private GimbalSlice() {
    }

    public record Result(double limitedDegrees, double frozenDegrees, double steeredAngle, double fixedAngle) {
    }

    public static Result execute() {
        Vector3d rest = Vector3d.UNIT_Y;
        Vector3d command = new Vector3d(1.0, 1.0, 0.0);
        EngineState steerable = engine(8.0);
        EngineState frozen = engine(0.0);
        double limited = Math.toDegrees(steerable.gimbalToward(command).angleFrom(rest));
        if (Math.abs(limited - 8.0) > 0.05) {
            throw new IllegalStateException("8° gimbal must stop at 8°, got " + limited);
        }
        double frozenDeg = Math.toDegrees(frozen.gimbalToward(command).angleFrom(rest));
        if (frozenDeg > 1.0e-6) {
            throw new IllegalStateException("Fixed nozzle must not deflect, got " + frozenDeg);
        }
        double inside = Math.toDegrees(steerable.gimbalToward(new Vector3d(0.05, 1.0, 0.0)).angleFrom(rest));
        if (!(inside > 0.5) || inside > 8.0) {
            throw new IllegalStateException("A command inside the cone must be reached, got " + inside);
        }

        VesselState steered = VesselDynamics.gimbalTowardInertial(stack(8.0), Vector3d.UNIT_X);
        VesselState afterSteer = VesselDynamics.burnActiveStage(steered, 2.0);
        double steeredAngle = angleFromIdentity(afterSteer.attitude());
        if (!(steeredAngle > 0.02)) {
            throw new IllegalStateException("Gimballed thrust must rotate the stack, got " + steeredAngle);
        }

        VesselState fixed = VesselDynamics.gimbalTowardInertial(stack(0.0), Vector3d.UNIT_X);
        VesselState afterFixed = VesselDynamics.burnActiveStage(fixed, 2.0);
        double fixedAngle = angleFromIdentity(afterFixed.attitude());
        if (fixedAngle > 1.0e-6) {
            throw new IllegalStateException("A fixed nozzle on axis must not rotate, got " + fixedAngle);
        }
        if (!(steeredAngle > fixedAngle + 0.02)) {
            throw new IllegalStateException("TVC must out-rotate a fixed nozzle");
        }

        return new Result(limited, frozenDeg, steeredAngle, fixedAngle);
    }

    private static EngineState engine(double gimbalDegrees) {
        return new EngineState(
                "engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                1.0, gimbalDegrees, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
    }

    private static VesselState stack(double gimbalDegrees) {
        TankState tank = new TankState(
                "tank", 80.0, 1_000.0,
                new PropellantState("rp1", "lox", 50.0, 100.0),
                Vector3d.ZERO);
        StageState stage = new StageState(
                "stage-0",
                List.of(new MassElement("frame", 120.0, Vector3d.ZERO)),
                List.of(tank),
                List.of(engine(gimbalDegrees)),
                true);
        return new VesselState("gimbal-demo", SpaceBodies.parkingOrbit(), List.of(stage), 0);
    }

    private static double angleFromIdentity(Attitude attitude) {
        return Math.acos(Math.max(-1.0, Math.min(1.0, attitude.toInertial(Vector3d.UNIT_Y).dot(Vector3d.UNIT_Y))));
    }
}
