package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Retrograde deorbit: drop periapsis from circular parking, then coast to it.
 * Vacuum Kepler; entry heating is ChemMod world gas, not this slice.
 */
public final class DeorbitSlice {
    private DeorbitSlice() {
    }

    public record Result(
            double parkingAltitude,
            double deorbitEccentricity,
            double periapsisAltitude,
            double coastAltitude) {
    }

    public static Result execute() {
        VesselState parking = stack();
        double parkingAlt = parking.orbit().altitudeMeters();
        if (parking.orbit().eccentricity() > 0.01) {
            throw new IllegalStateException("Parking orbit must be circular, e=" + parking.orbit().eccentricity());
        }
        if (Math.abs(parkingAlt - 200_000.0) > 1_000.0) {
            throw new IllegalStateException("Must start at 200 km parking, h=" + parkingAlt);
        }

        double targetPeriapsis = SpaceBodies.earth().radiusMeters() + 80_000.0;
        VesselState deorbit = VesselDynamics.lowerPeriapsis(parking, targetPeriapsis);
        double deorbitE = deorbit.orbit().eccentricity();
        if (!(deorbitE > 0.005)) {
            throw new IllegalStateException("Deorbit must open an ellipse, e=" + deorbitE);
        }
        double apoAlt = deorbit.orbit().apoapsisRadiusMeters() - SpaceBodies.earth().radiusMeters();
        if (Math.abs(apoAlt - 200_000.0) > 5_000.0) {
            throw new IllegalStateException("Apoapsis must stay near 200 km, ha=" + apoAlt);
        }
        double periAlt = deorbit.orbit().periapsisRadiusMeters() - SpaceBodies.earth().radiusMeters();
        if (Math.abs(periAlt - 80_000.0) > 5_000.0) {
            throw new IllegalStateException("Periapsis must drop to ~80 km, hp=" + periAlt);
        }

        VesselState peri = VesselDynamics.coastToPeriapsis(deorbit, 3_500.0, 5.0);
        double coastAlt = peri.orbit().altitudeMeters();
        if (Math.abs(coastAlt - 80_000.0) > 8_000.0) {
            throw new IllegalStateException("Coast must reach ~80 km periapsis, h=" + coastAlt);
        }
        if (Math.abs(peri.orbit().radialSpeedMetersPerSecond()) > 80.0) {
            throw new IllegalStateException(
                    "Periapsis radial speed must be near zero, vr=" + peri.orbit().radialSpeedMetersPerSecond());
        }
        if (!(coastAlt < parkingAlt - 50_000.0)) {
            throw new IllegalStateException("Deorbit must come down from parking");
        }
        return new Result(parkingAlt, deorbitE, periAlt, coastAlt);
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
        return new VesselState("deorbit-demo", SpaceBodies.parkingOrbit(), List.of(stage), 0);
    }
}
