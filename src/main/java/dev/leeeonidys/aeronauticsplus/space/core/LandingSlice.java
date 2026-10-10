package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Falcon-class landing burn: suicide burn to the surface, booster kept after sep.
 * Vacuum only — grid fins and entry heat wait on ChemMod world gas.
 */
public final class LandingSlice {
    private LandingSlice() {
    }

    public record Result(
            boolean hopperLanded,
            boolean deadImpact,
            boolean boosterLanded,
            double touchdownSpeed,
            int upperStages) {
    }

    public static Result execute() {
        LandingOutcome hopper = VesselDynamics.attemptLanding(
                hopper(true), 60.0, 0.05);
        if (!hopper.landed() || hopper.has(FlightFault.IMPACT)) {
            throw new IllegalStateException("Hopper must suicide-burn to the pad: " + hopper.faultText()
                    + " h=" + hopper.altitudeMeters() + " v=" + hopper.speedMetersPerSecond());
        }
        if (hopper.altitudeMeters() > LandingOutcome.TOUCHDOWN_ALTITUDE_METERS) {
            throw new IllegalStateException("Landed hopper must be on the surface, h=" + hopper.altitudeMeters());
        }

        LandingOutcome dead = VesselDynamics.attemptLanding(
                hopper(false), 60.0, 0.05);
        if (!dead.has(FlightFault.IMPACT)) {
            throw new IllegalStateException("A dead hopper must hit the surface");
        }
        if (dead.landed()) {
            throw new IllegalStateException("Impact is not a landing");
        }

        StageSplit split = VesselDynamics.splitActiveStage(twoStage());
        if (split.continuing().stages().size() != 1 || split.booster().stages().size() != 1) {
            throw new IllegalStateException("Sep must leave a booster and an upper");
        }
        LandingOutcome booster = VesselDynamics.attemptLanding(split.booster(), 60.0, 0.05);
        if (!booster.landed() || booster.has(FlightFault.IMPACT)) {
            throw new IllegalStateException("Recoverable booster must land: " + booster.faultText()
                    + " h=" + booster.altitudeMeters() + " v=" + booster.speedMetersPerSecond());
        }
        return new Result(
                hopper.landed(),
                dead.has(FlightFault.IMPACT),
                booster.landed(),
                hopper.speedMetersPerSecond(),
                split.continuing().stages().size());
    }

    private static OrbitState falling() {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + 2_000.0;
        return new OrbitState(
                earth,
                new Vector3d(radius, 0.0, 0.0),
                new Vector3d(-80.0, 0.0, 0.0),
                0.0);
    }

    private static VesselState hopper(boolean enginesOn) {
        EngineState engine = new EngineState(
                "engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                1.0, 0.0, enginesOn, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
        StageState stage = new StageState(
                "hopper",
                List.of(new MassElement("frame", 120.0, Vector3d.ZERO)),
                List.of(new TankState(
                        "tank", 80.0, 300.0,
                        new PropellantState("rp1", "lox", 50.0, 115.0),
                        Vector3d.ZERO)),
                List.of(engine),
                false);
        Attitude radial = Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X);
        return new VesselState("landing-hopper", falling(), List.of(stage), 0, radial);
    }

    private static VesselState twoStage() {
        StageState booster = new StageState(
                "booster",
                List.of(new MassElement("booster-frame", 100.0, Vector3d.ZERO)),
                List.of(new TankState(
                        "booster-tank", 50.0, 250.0,
                        new PropellantState("rp1", "lox", 40.0, 92.0),
                        Vector3d.ZERO)),
                List.of(new EngineState(
                        "booster-engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                        1.0, 0.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y)),
                true);
        StageState upper = new StageState(
                "upper",
                List.of(new MassElement("upper-frame", 80.0, new Vector3d(0.0, 2.0, 0.0))),
                List.of(new TankState(
                        "upper-tank", 40.0, 150.0,
                        new PropellantState("rp1", "lox", 20.0, 46.0),
                        new Vector3d(0.0, 2.0, 0.0))),
                List.of(new EngineState(
                        "upper-engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                        1.0, 0.0, true, new Vector3d(0.0, 1.5, 0.0), Vector3d.UNIT_Y)),
                false);
        Attitude radial = Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X);
        return new VesselState("landing-stack", falling(), List.of(booster, upper), 0, radial);
    }
}
