package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Strap-ons burn with the core until radial pyro. Upper waits.
 * Vacuum only — not ChemMod world gas.
 */
public final class StrapOnSlice {
    private StrapOnSlice() {
    }

    public record Result(
            double clusterThrust,
            double coreThrust,
            int sides,
            boolean upperUntouched,
            boolean sidesShareOrbit) {
    }

    public static Result execute() {
        VesselState heavy = heavy();
        double cluster = heavy.clusterThrustNewtons();
        double coreThrust = heavy.stages().stream()
                .filter(stage -> !stage.strapOn() && "core".equals(stage.id()))
                .mapToDouble(StageState::thrustNewtons)
                .findFirst()
                .orElseThrow();
        if (!(cluster > coreThrust * 2.5)) {
            throw new IllegalStateException(
                    "Three live engines must out-thrust the core, cluster=" + cluster + " core=" + coreThrust);
        }
        if (heavy.liveCluster().size() != 3) {
            throw new IllegalStateException("Live cluster must be two straps plus core");
        }

        try {
            VesselDynamics.dropStrapOns(coreOnly());
            throw new IllegalStateException("A core without straps must not radial-sep");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().contains("strap-on")) {
                throw expected;
            }
        }

        double upperFuel = heavy.stages().get(3).propellantMassKg();
        VesselState burned = VesselDynamics.burnLiveCluster(heavy, 2.0);
        if (!(burned.orbit().velocityMetersPerSecond().magnitude() > 10.0)) {
            throw new IllegalStateException("Cluster burn must add speed");
        }
        if (Math.abs(burned.stages().get(3).propellantMassKg() - upperFuel) > 1.0e-9) {
            throw new IllegalStateException("Upper must wait while straps and core burn");
        }
        for (int i = 0; i < 3; i++) {
            if (!(burned.stages().get(i).propellantMassKg() < heavy.stages().get(i).propellantMassKg() - 1.0)) {
                throw new IllegalStateException("Live cluster must spend propellant, stage=" + i);
            }
        }

        StrapSplit split = VesselDynamics.dropStrapOns(burned);
        if (split.sides().size() != 2) {
            throw new IllegalStateException("Radial pyro must drop two sides, n=" + split.sides().size());
        }
        if (split.core().hasStrapOns() || split.core().stages().size() != 2) {
            throw new IllegalStateException("Core plus upper must remain after straps drop");
        }
        boolean sameOrbit = split.sides().stream().allMatch(side ->
                side.orbit().epochSeconds() == split.core().orbit().epochSeconds());
        if (!sameOrbit) {
            throw new IllegalStateException("Dropped straps keep the trajectory they had at pyro");
        }
        if (!(split.core().clusterThrustNewtons() < cluster / 2.0)) {
            throw new IllegalStateException("After drop only the core should be live");
        }

        return new Result(
                cluster,
                coreThrust,
                split.sides().size(),
                Math.abs(burned.stages().get(3).propellantMassKg() - upperFuel) < 1.0e-9,
                sameOrbit);
    }

    private static VesselState heavy() {
        Attitude up = Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X);
        return new VesselState(
                "heavy",
                SpaceBodies.pad(),
                List.of(strap("left"), strap("right"), core(), upper()),
                0,
                up);
    }

    private static VesselState coreOnly() {
        Attitude up = Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X);
        return new VesselState("core-only", SpaceBodies.pad(), List.of(core()), 0, up);
    }

    private static StageState strap(String id) {
        return stage(id, true);
    }

    private static StageState core() {
        return stage("core", false);
    }

    private static StageState upper() {
        return new StageState(
                "upper",
                List.of(new MassElement("upper-frame", 80.0, new Vector3d(0.0, 2.0, 0.0))),
                List.of(new TankState(
                        "upper-tank", 40.0, 150.0,
                        new PropellantState("rp1", "lox", 20.0, 46.0),
                        new Vector3d(0.0, 2.0, 0.0))),
                List.of(new EngineState(
                        "upper-engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                        1.0, 0.0, true, new Vector3d(0.0, 1.5, 0.0), Vector3d.UNIT_Y)),
                false,
                false);
    }

    private static StageState stage(String id, boolean strapOn) {
        return new StageState(
                id,
                List.of(new MassElement(id + "-frame", 100.0, Vector3d.ZERO)),
                List.of(new TankState(
                        id + "-tank", 50.0, 250.0,
                        new PropellantState("rp1", "lox", 40.0, 92.0),
                        Vector3d.ZERO)),
                List.of(new EngineState(
                        id + "-engine", "rp1", "lox", 90.0, 20_000.0, 300.0, 2.3,
                        1.0, 0.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y)),
                false,
                strapOn);
    }
}
