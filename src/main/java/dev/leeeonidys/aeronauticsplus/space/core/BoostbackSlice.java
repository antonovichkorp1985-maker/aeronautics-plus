package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Falcon boostback then landing. Downrange leftover makes a direct landing an impact.
 * Vacuum only — not ChemMod world gas.
 */
public final class BoostbackSlice {
    private BoostbackSlice() {
    }

    public record Result(
            double startHorizontal,
            double afterBoostback,
            boolean directImpact,
            boolean recovered) {
    }

    public static Result execute() {
        VesselState downrange = stack();
        double startHoriz = downrange.orbit().horizontalSpeedMetersPerSecond();
        if (!(startHoriz > 300.0)) {
            throw new IllegalStateException("Boostback demo must start downrange, horiz=" + startHoriz);
        }

        LandingOutcome direct = VesselDynamics.attemptLanding(downrange, 60.0, 0.05);
        if (!direct.has(FlightFault.IMPACT)) {
            throw new IllegalStateException("A downrange hopper must hit if it skips boostback");
        }

        BoostbackOutcome boost = VesselDynamics.attemptBoostback(downrange, 30.0, 0.05);
        if (!boost.killedDownrange()) {
            throw new IllegalStateException("Boostback must kill downrange speed: " + boost.faultText()
                    + " horiz=" + boost.endHorizontal());
        }
        if (!(boost.endHorizontal() < startHoriz - 200.0)) {
            throw new IllegalStateException("Boostback must dump hundreds of m/s");
        }

        LandingOutcome recovered = VesselDynamics.attemptLanding(boost.vessel(), 60.0, 0.05);
        if (!recovered.landed() || recovered.has(FlightFault.IMPACT)) {
            throw new IllegalStateException("After boostback the hopper must land: " + recovered.faultText()
                    + " h=" + recovered.altitudeMeters() + " v=" + recovered.speedMetersPerSecond());
        }
        return new Result(startHoriz, boost.endHorizontal(), direct.has(FlightFault.IMPACT), recovered.landed());
    }

    private static VesselState stack() {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + 3_000.0;
        OrbitState downrange = new OrbitState(
                earth,
                new Vector3d(radius, 0.0, 0.0),
                new Vector3d(0.0, 0.0, 400.0),
                0.0);
        EngineState engine = new EngineState(
                "engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                1.0, 0.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
        StageState stage = new StageState(
                "booster",
                List.of(new MassElement("frame", 120.0, Vector3d.ZERO)),
                List.of(new TankState(
                        "tank", 80.0, 400.0,
                        new PropellantState("rp1", "lox", 80.0, 184.0),
                        Vector3d.ZERO)),
                List.of(engine),
                false);
        Attitude radial = Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X);
        return new VesselState("boostback-demo", downrange, List.of(stage), 0, radial);
    }
}
