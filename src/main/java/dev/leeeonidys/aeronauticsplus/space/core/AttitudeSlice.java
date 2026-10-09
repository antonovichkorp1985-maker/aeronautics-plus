package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Deterministic proof that body-frame thrust is not inertial: rotating the vessel
 * changes which inertial axis a burn accelerates. No orientation GUI.
 */
public final class AttitudeSlice {
    private AttitudeSlice() {
    }

    public record Result(
            double identityDeltaY,
            double rolledDeltaX,
            double progradeSpeedGain,
            double identitySpeedGain) {
    }

    public static Result execute() {
        VesselState vessel = fueledVessel();
        Vector3d v0 = vessel.orbit().velocityMetersPerSecond();

        VesselState identityBurn = VesselDynamics.burnActiveStage(vessel, 2.0);
        Vector3d di = identityBurn.orbit().velocityMetersPerSecond().subtract(v0);
        if (!(di.y() > 1.0) || Math.abs(di.x()) > 0.05 || Math.abs(di.z()) > 0.05) {
            throw new IllegalStateException(
                    "Identity attitude must burn along inertial +Y, got delta-v=" + di);
        }

        Attitude rolled = Attitude.IDENTITY.rotateBody(Vector3d.UNIT_Z, -Math.PI / 2.0);
        Vector3d bodyY = rolled.toInertial(Vector3d.UNIT_Y);
        if (Math.abs(bodyY.x() - 1.0) > 1.0e-9 || Math.abs(bodyY.y()) > 1.0e-9 || Math.abs(bodyY.z()) > 1.0e-9) {
            throw new IllegalStateException("-90 deg about body Z must map body +Y onto inertial +X, got " + bodyY);
        }
        VesselState rolledBurn = VesselDynamics.burnActiveStage(vessel.withAttitude(rolled), 2.0);
        Vector3d dr = rolledBurn.orbit().velocityMetersPerSecond().subtract(v0);
        if (!(dr.x() > 1.0) || Math.abs(dr.y()) > 0.05 || Math.abs(dr.z()) > 0.05) {
            throw new IllegalStateException(
                    "Rolled attitude must burn along inertial +X, got delta-v=" + dr);
        }
        if (Math.abs(dr.magnitude() - di.magnitude()) > 1.0e-6) {
            throw new IllegalStateException("Attitude must rotate delta-v, not change its magnitude");
        }

        Attitude prograde = Attitude.pointing(Vector3d.UNIT_Y, v0);
        VesselState progradeBurn = VesselDynamics.burnActiveStage(vessel.withAttitude(prograde), 2.0);
        double identitySpeedGain = identityBurn.orbit().velocityMetersPerSecond().magnitude() - v0.magnitude();
        double progradeSpeedGain = progradeBurn.orbit().velocityMetersPerSecond().magnitude() - v0.magnitude();
        if (!(progradeSpeedGain > identitySpeedGain + 0.5)) {
            throw new IllegalStateException(
                    "Pointing the engine along prograde must raise speed more than a normal burn");
        }
        if (Math.abs(prograde.toInertial(Vector3d.UNIT_Y).normalized().dot(v0.normalized()) - 1.0) > 1.0e-9) {
            throw new IllegalStateException("pointing() must align body +Y with the requested inertial axis");
        }

        return new Result(di.y(), dr.x(), progradeSpeedGain, identitySpeedGain);
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
        return new VesselState("attitude-demo", SpaceBodies.parkingOrbit(), List.of(stage), 0);
    }
}
