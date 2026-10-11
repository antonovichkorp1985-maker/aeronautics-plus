package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Cut throttle coasts. Full throttle burns. Not a GUI, not ChemMod air.
 */
public final class ThrottleSlice {
    private ThrottleSlice() {
    }

    public record Result(
            double risenAltitude,
            double cutPropellantKg,
            double fullPropellantKg,
            double cutAltitude,
            double fullAltitude) {
    }

    public static Result execute() {
        VesselState risen = fly(stack(), 80);
        if (!(risen.orbit().altitudeMeters() > 40.0)) {
            throw new IllegalStateException("Full throttle must leave the pad, h=" + risen.orbit().altitudeMeters());
        }
        double risenPropellant = risen.livePropellantKg();
        VesselState cut = fly(risen.withLiveThrottle(0.0), 40);
        VesselState full = fly(risen, 40);
        if (Math.abs(cut.livePropellantKg() - risenPropellant) > 0.05) {
            throw new IllegalStateException(
                    "Cut throttle must not burn, was=" + risenPropellant + " now=" + cut.livePropellantKg());
        }
        if (!(full.livePropellantKg() < cut.livePropellantKg() - 5.0)) {
            throw new IllegalStateException(
                    "Full throttle must burn more than cut, full=" + full.livePropellantKg()
                            + " cut=" + cut.livePropellantKg());
        }
        if (!(full.orbit().altitudeMeters() > cut.orbit().altitudeMeters() + 20.0)) {
            throw new IllegalStateException(
                    "Full throttle must out-climb a cut, full=" + full.orbit().altitudeMeters()
                            + " cut=" + cut.orbit().altitudeMeters());
        }
        if (cut.clusterThrustNewtons() > 1.0e-6) {
            throw new IllegalStateException("Cut cluster must be silent, F=" + cut.clusterThrustNewtons());
        }
        return new Result(
                risen.orbit().altitudeMeters(),
                cut.livePropellantKg(),
                full.livePropellantKg(),
                cut.orbit().altitudeMeters(),
                full.orbit().altitudeMeters());
    }

    private static VesselState fly(VesselState state, int steps) {
        Attitude radial = Attitude.pointing(Vector3d.UNIT_Y, state.orbit().radialUnit());
        state = state.withAttitude(radial);
        for (int i = 0; i < steps; i++) {
            AscentOutcome step = VesselDynamics.advanceAscent(
                    state, Atmosphere.earth(), 0.1, Aerodynamics.ROCKET_CD, 1.0);
            state = step.vessel();
        }
        return state;
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
        return new VesselState("throttle-demo", SpaceBodies.pad(), List.of(stage), 0);
    }
}
