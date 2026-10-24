package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Numbers a seated pilot can read without an orientation GUI.
 */
public final class FlightReadoutSlice {
    private FlightReadoutSlice() {
    }

    public record Result(String altitude, String throttle) {
    }

    public static Result execute() {
        VesselState stepped = stack().nudgeLiveThrottle(-0.1);
        if (Math.abs(stepped.liveThrottle() - 0.9) > 1.0e-9) {
            throw new IllegalStateException("A down detent must be 90%, got " + stepped.liveThrottle());
        }
        VesselState floor = stack().withLiveThrottle(0.0).nudgeLiveThrottle(-0.1);
        if (floor.liveThrottle() > 1.0e-9) {
            throw new IllegalStateException("Throttle must not go below zero");
        }
        String[] lines = FlightReadout.lines(
                12_345.0, 320.4, -12.2, 0.7, 50, "prograde", 12_000.0, 80_000.0, -4_000.0);
        if (lines.length != 10 || !lines[0].contains("12345") || !lines[3].contains("70")
                || !lines[4].contains("12000") || !lines[5].contains("80000")
                || !lines[6].contains("-4000") || !lines[7].contains("50")
                || !lines[8].contains("prograde") || !lines[9].contains("earth")) {
            throw new IllegalStateException("Readout lost the numbers: " + String.join(" | ", lines));
        }
        String[] lunar = FlightReadout.lines(
                100_000.0, 1_600.0, 0.0, 0.0, 1, "off", 0.0, 100_000.0, 100_000.0, "moon");
        if (!lunar[9].contains("moon")) {
            throw new IllegalStateException("Lunar parking must read body moon, got " + lunar[9]);
        }
        String[] escaping = FlightReadout.lines(
                200_000.0, 11_000.0, 100.0, 0.0, 1, "off", 0.0, Double.POSITIVE_INFINITY, 80_000.0);
        if (!escaping[5].contains("esc") || !escaping[6].contains("80000")) {
            throw new IllegalStateException("Escape must read Ap esc, got " + String.join(" | ", escaping));
        }
        OrbitState parking = SpaceBodies.parkingOrbit();
        if (Math.abs(parking.apoapsisAltitudeMeters() - 200_000.0) > 5_000.0
                || Math.abs(parking.periapsisAltitudeMeters() - 200_000.0) > 5_000.0) {
            throw new IllegalStateException("Parking Ap/Pe must sit near 200 km, Ap="
                    + parking.apoapsisAltitudeMeters() + " Pe=" + parking.periapsisAltitudeMeters());
        }
        return new Result(lines[0], lines[3]);
    }

    private static VesselState stack() {
        TankState tank = new TankState(
                "tank", 80.0, 1_000.0,
                new PropellantState("rp1", "lox", 200.0, 460.0),
                Vector3d.ZERO);
        EngineState engine = new EngineState(
                "engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                1.0, 8.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
        StageState stage = new StageState(
                "stage-0",
                List.of(new MassElement("frame", 120.0, Vector3d.ZERO)),
                List.of(tank),
                List.of(engine),
                true);
        return new VesselState("readout-demo", SpaceBodies.pad(), List.of(stage), 0);
    }
}
