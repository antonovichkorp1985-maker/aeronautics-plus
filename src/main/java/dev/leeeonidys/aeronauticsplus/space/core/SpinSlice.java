package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Deterministic proof that offset thrust leaves residual body-frame spin, and that
 * coast in vacuum keeps that rate: attitude continues to change with no extra torque.
 */
public final class SpinSlice {
    private SpinSlice() {
    }

    public record Result(
            double burnRate,
            double coastRate,
            double extraCoastAngle,
            double thrustDot) {
    }

    public static Result execute() {
        VesselState centered = VesselDynamics.burnActiveStage(vessel(0.0), 0.2);
        if (centered.hasResidualSpin()) {
            throw new IllegalStateException("On-axis thrust must not leave spin, got "
                    + centered.angularVelocityBody());
        }
        VesselState centeredCoast = VesselDynamics.propagate(centered, 2.0, 1.0);
        if (angleFromIdentity(centeredCoast.attitude()) > 1.0e-6) {
            throw new IllegalStateException("A non-spinning coast must not rotate attitude");
        }

        VesselState afterBurn = VesselDynamics.burnActiveStage(vessel(0.5), 0.2);
        double burnRate = afterBurn.angularVelocityBody().magnitude();
        if (!(burnRate > 0.2)) {
            throw new IllegalStateException("Offset burn must leave residual spin, got " + burnRate);
        }
        double burnAngle = angleFromIdentity(afterBurn.attitude());
        Vector3d thrustAfterBurn = afterBurn.inertialThrustNewtons().normalized();

        VesselState afterCoast = VesselDynamics.propagate(afterBurn, 2.0, 1.0);
        double coastRate = afterCoast.angularVelocityBody().magnitude();
        if (Math.abs(coastRate - burnRate) > 1.0e-9) {
            throw new IllegalStateException("Vacuum coast must not damp spin, " + burnRate + " -> " + coastRate);
        }
        double extraCoastAngle = angleFromIdentity(afterCoast.attitude()) - burnAngle;
        if (!(extraCoastAngle > 0.2)) {
            throw new IllegalStateException("Residual spin must keep rotating on coast, extra=" + extraCoastAngle);
        }
        double predicted = burnRate * 2.0;
        if (Math.abs(extraCoastAngle - predicted) > 0.05) {
            throw new IllegalStateException("Coast angle must match ωΔt: extra=" + extraCoastAngle
                    + " predicted=" + predicted);
        }
        Vector3d thrustAfterCoast = afterCoast.inertialThrustNewtons().normalized();
        double thrustDot = thrustAfterBurn.dot(thrustAfterCoast);
        if (thrustDot > 0.99) {
            throw new IllegalStateException("Coast spin must slew the next inertial thrust vector, dot=" + thrustDot);
        }
        return new Result(burnRate, coastRate, extraCoastAngle, thrustDot);
    }

    private static double angleFromIdentity(Attitude attitude) {
        double dot = Math.max(-1.0, Math.min(1.0, attitude.toInertial(Vector3d.UNIT_Y).dot(Vector3d.UNIT_Y)));
        return Math.acos(dot);
    }

    private static VesselState vessel(double engineX) {
        TankState tank = new TankState(
                "tank", 80.0, 1_000.0,
                new PropellantState("rp1", "lox", 50.0, 100.0),
                Vector3d.ZERO);
        EngineState engine = new EngineState(
                "engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.0,
                1.0, 0.0, true, new Vector3d(engineX, -1.0, 0.0), Vector3d.UNIT_Y);
        StageState stage = new StageState(
                "stage-0",
                List.of(new MassElement("frame", 400.0, Vector3d.ZERO)),
                List.of(tank),
                List.of(engine),
                true);
        return new VesselState("spin-demo", SpaceBodies.parkingOrbit(), List.of(stage), 0);
    }
}
