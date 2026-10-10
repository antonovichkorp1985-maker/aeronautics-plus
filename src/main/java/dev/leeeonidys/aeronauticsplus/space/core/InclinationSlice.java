package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Combined-plane change on a circular parking orbit. Vacuum Kepler, not ChemMod air.
 */
public final class InclinationSlice {
    private InclinationSlice() {
    }

    public record Result(
            double startInclinationDegrees,
            double turnedInclinationDegrees,
            double altitudeMeters,
            double speedChange) {
    }

    public static Result execute() {
        VesselState parking = stack();
        double startI = parking.orbit().inclinationRadians();
        double startAlt = parking.orbit().altitudeMeters();
        double startSpeed = parking.orbit().velocityMetersPerSecond().magnitude();
        if (parking.orbit().eccentricity() > 0.01) {
            throw new IllegalStateException("Parking orbit must be circular, e=" + parking.orbit().eccentricity());
        }

        double delta = Math.toRadians(10.0);
        VesselState turned = VesselDynamics.changeInclination(parking, delta);
        double turnedI = turned.orbit().inclinationRadians();
        double di = Math.toDegrees(Math.abs(turnedI - startI));
        if (Math.abs(di - 10.0) > 0.5) {
            throw new IllegalStateException("Plane change must move inclination by 10°, got " + di);
        }
        if (Math.abs(turned.orbit().altitudeMeters() - startAlt) > 1_000.0) {
            throw new IllegalStateException("Plane change must not jump radius");
        }
        if (turned.orbit().eccentricity() > 0.01) {
            throw new IllegalStateException("Plane change must stay circular, e=" + turned.orbit().eccentricity());
        }
        double speedChange = Math.abs(turned.orbit().velocityMetersPerSecond().magnitude() - startSpeed);
        if (speedChange > 1.0) {
            throw new IllegalStateException("Combined plane change keeps |v|, dVmag=" + speedChange);
        }
        VesselState untouched = VesselDynamics.changeInclination(parking, 0.0);
        if (Math.abs(untouched.orbit().inclinationRadians() - startI) > 1.0e-9) {
            throw new IllegalStateException("A zero plane change must be a no-op");
        }
        return new Result(
                Math.toDegrees(startI),
                Math.toDegrees(turnedI),
                turned.orbit().altitudeMeters(),
                speedChange);
    }

    private static VesselState stack() {
        TankState tank = new TankState(
                "tank", 80.0, 1_000.0,
                new PropellantState("rp1", "lox", 50.0, 100.0),
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
        return new VesselState("inclination-demo", SpaceBodies.parkingOrbit(), List.of(stage), 0);
    }
}
