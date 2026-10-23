package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Hohmann transfer: raise apoapsis from a circular parking orbit, coast, circularize.
 * Vacuum Kepler, not ChemMod world gas.
 */
public final class HohmannSlice {
    private HohmannSlice() {
    }

    public record Result(
            double parkingAltitude,
            double transferEccentricity,
            double transferApoapsisAltitude,
            double circularAltitude,
            double circularEccentricity) {
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

        double targetApoapsis = SpaceBodies.earth().radiusMeters() + 500_000.0;
        VesselState transfer = VesselDynamics.raiseApoapsis(parking, targetApoapsis);
        double transferE = transfer.orbit().eccentricity();
        if (!(transferE > 0.02)) {
            throw new IllegalStateException("Hohmann first burn must open an ellipse, e=" + transferE);
        }
        double periAlt = transfer.orbit().periapsisRadiusMeters() - SpaceBodies.earth().radiusMeters();
        if (Math.abs(periAlt - 200_000.0) > 5_000.0) {
            throw new IllegalStateException("Periapsis must stay near 200 km, hp=" + periAlt);
        }
        double apoAlt = transfer.orbit().apoapsisRadiusMeters() - SpaceBodies.earth().radiusMeters();
        if (Math.abs(apoAlt - 500_000.0) > 5_000.0) {
            throw new IllegalStateException("Apoapsis must rise to ~500 km, ha=" + apoAlt);
        }

        VesselState apo = VesselDynamics.coastToApoapsis(transfer, 4_000.0, 5.0);
        VesselState circular = VesselDynamics.circularize(apo);
        double circE = circular.orbit().eccentricity();
        if (!(circE < 0.01)) {
            throw new IllegalStateException("Second burn must circularize, e=" + circE);
        }
        double circAlt = circular.orbit().altitudeMeters();
        if (Math.abs(circAlt - 500_000.0) > 8_000.0) {
            throw new IllegalStateException("Must circularize near 500 km, h=" + circAlt);
        }
        if (!(circAlt > parkingAlt + 200_000.0)) {
            throw new IllegalStateException("Hohmann must raise the circular orbit");
        }
        return new Result(parkingAlt, transferE, apoAlt, circAlt, circE);
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
        return new VesselState("hohmann-demo", SpaceBodies.parkingOrbit(), List.of(stage), 0);
    }
}
