package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Deterministic proof that an offset thrust vector rotates the vessel during burn.
 * Spin is not kept after the impulse; only the finite attitude change is applied.
 */
public final class TorqueSlice {
    private TorqueSlice() {
    }

    public record Result(double centeredAngle, double offsetAngle, double offsetMeters) {
    }

    public static Result execute() {
        VesselState centered = vessel(0.0);
        if (centered.activeThrustGeometry().hasMaterialOffset()) {
            throw new IllegalStateException("Centered engine must not warn about thrust offset");
        }
        Attitude afterCentered = VesselDynamics.burnActiveStage(centered, 0.2).attitude();
        double centeredAngle = angleFromIdentity(afterCentered);
        if (centeredAngle > 1.0e-6) {
            throw new IllegalStateException("On-axis thrust must leave attitude unchanged, got " + centeredAngle);
        }

        VesselState offset = vessel(0.5);
        ThrustGeometry geometry = offset.activeThrustGeometry();
        if (!geometry.hasMaterialOffset()) {
            throw new IllegalStateException("Half-metre lateral engine must produce a material offset");
        }
        VesselState spun = VesselDynamics.burnActiveStage(offset, 0.2);
        double offsetAngle = angleFromIdentity(spun.attitude());
        if (!(offsetAngle > 0.05)) {
            throw new IllegalStateException("Offset thrust must rotate the vessel, got " + offsetAngle);
        }
        Vector3d bodyY = spun.attitude().toInertial(Vector3d.UNIT_Y);
        if (Math.abs(bodyY.x()) < 0.01) {
            throw new IllegalStateException("Rotation about Z must tilt body +Y off the inertial Y axis");
        }
        return new Result(centeredAngle, offsetAngle, geometry.perpendicularOffsetMeters());
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
        return new VesselState("torque-demo", SpaceBodies.parkingOrbit(), List.of(stage), 0);
    }
}
