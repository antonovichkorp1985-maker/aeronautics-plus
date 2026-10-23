package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Falcon landing zone: the deck, not "any ocean". Soft contact off the pad is OFF_PAD.
 * Vacuum only — barge station-keeping and grid fins wait on ChemMod air.
 */
public final class LandingPadSlice {
    private LandingPadSlice() {
    }

    public record Result(boolean hitLz, boolean missedAsds, double missMeters) {
    }

    public static Result execute() {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters();
        LandingPad lz = LandingPad.at(new Vector3d(radius, 0.0, 0.0));

        LandingOutcome onPad = VesselDynamics.attemptLanding(hopper(), lz, 60.0, 0.05);
        if (!onPad.landed() || onPad.has(FlightFault.OFF_PAD)) {
            throw new IllegalStateException("Hopper over LZ-1 must land on the pad: " + onPad.faultText()
                    + " h=" + onPad.altitudeMeters() + " v=" + onPad.speedMetersPerSecond());
        }

        double arc = 2_000.0 / radius;
        LandingPad asds = LandingPad.at(new Vector3d(
                radius * Math.cos(arc), 0.0, radius * Math.sin(arc)));
        LandingOutcome miss = VesselDynamics.attemptLanding(hopper(), asds, 60.0, 0.05);
        if (!miss.has(FlightFault.OFF_PAD) || miss.landed()) {
            throw new IllegalStateException("A hopper 2 km from the barge must not count as a landing");
        }
        double missMeters = asds.groundDistanceMeters(
                miss.vessel().orbit().positionMeters(), radius);
        if (!(missMeters > LandingPad.DEFAULT_RADIUS_METERS)) {
            throw new IllegalStateException("ASDS miss must be outside the deck, d=" + missMeters);
        }
        return new Result(onPad.landed(), miss.has(FlightFault.OFF_PAD), missMeters);
    }

    private static VesselState hopper() {
        EngineState engine = new EngineState(
                "engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                1.0, 0.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
        StageState stage = new StageState(
                "hopper",
                List.of(
                        new MassElement("frame", 120.0, Vector3d.ZERO),
                        new MassElement("legs", 25.0, new Vector3d(0.0, -1.2, 0.0), MassElement.Role.LEGS)),
                List.of(new TankState(
                        "tank", 80.0, 300.0,
                        new PropellantState("rp1", "lox", 50.0, 115.0),
                        Vector3d.ZERO)),
                List.of(engine),
                false);
        Attitude radial = Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X);
        CelestialBody earth = SpaceBodies.earth();
        double r = earth.radiusMeters() + 2_000.0;
        OrbitState falling = new OrbitState(
                earth,
                new Vector3d(r, 0.0, 0.0),
                new Vector3d(-80.0, 0.0, 0.0),
                0.0);
        return new VesselState("pad-hopper", falling, List.of(stage), 0, radial);
    }
}
