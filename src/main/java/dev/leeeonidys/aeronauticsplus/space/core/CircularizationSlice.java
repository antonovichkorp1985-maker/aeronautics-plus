package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Coast to apoapsis, then circularize. Vacuum Kepler, not ChemMod world gas.
 */
public final class CircularizationSlice {
    private CircularizationSlice() {
    }

    public record Result(
            double startEccentricity,
            double apoapsisAltitude,
            double circularEccentricity,
            double circularAltitude) {
    }

    public static Result execute() {
        VesselState start = stack();
        double startE = start.orbit().eccentricity();
        if (!(startE > 0.02)) {
            throw new IllegalStateException("Transfer ellipse must be eccentric, e=" + startE);
        }
        if (Math.abs(start.orbit().altitudeMeters() - 200_000.0) > 1_000.0) {
            throw new IllegalStateException("Must start near periapsis 200 km");
        }

        VesselState apo = VesselDynamics.coastToApoapsis(start, 4_000.0, 5.0);
        double apoAlt = apo.orbit().altitudeMeters();
        if (Math.abs(apoAlt - 500_000.0) > 8_000.0) {
            throw new IllegalStateException("Coast must reach ~500 km apoapsis, h=" + apoAlt);
        }
        if (Math.abs(apo.orbit().radialSpeedMetersPerSecond()) > 80.0) {
            throw new IllegalStateException(
                    "Apoapsis radial speed must be near zero, vr=" + apo.orbit().radialSpeedMetersPerSecond());
        }

        VesselState circular = VesselDynamics.circularize(apo);
        double circE = circular.orbit().eccentricity();
        if (!(circE < 0.01)) {
            throw new IllegalStateException("Circularization must kill eccentricity, e=" + circE);
        }
        if (!(circE < startE - 0.01)) {
            throw new IllegalStateException("Circular orbit must be rounder than the transfer");
        }
        double circAlt = circular.orbit().altitudeMeters();
        if (Math.abs(circAlt - apoAlt) > 5_000.0) {
            throw new IllegalStateException("Circularization is a burn, not a radius jump");
        }
        return new Result(startE, apoAlt, circE, circAlt);
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
        return new VesselState("circularize-demo", SpaceBodies.ellipticTransfer(), List.of(stage), 0);
    }
}
