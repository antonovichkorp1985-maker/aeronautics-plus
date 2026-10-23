package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselCompileSlice;
import java.util.List;

/**
 * Deterministic mission-map slice: compiled two-stage stack flies coast / burn / separate
 * without a GUI.
 */
public final class MissionSlice {
    private MissionSlice() {
    }

    public record Result(String planText, int remainingStages, double finalSpeed, double energyGain) {
    }

    public static Result execute() {
        VesselCompilation compilation = VesselBlockCompiler.compile(VesselCompileSlice.twoStageStack()).analyze();
        if (!compilation.isLaunchable()) {
            throw new IllegalStateException("Mission reference stack must be launchable:\n" + compilation.diagnosticText());
        }
        VesselState initial = compilation.toVesselState("mission-demo", SpaceBodies.parkingOrbit());
        double energyBefore = initial.orbit().specificOrbitalEnergy();
        double speedBefore = initial.orbit().velocityMetersPerSecond().magnitude();

        MissionPlan plan = new MissionPlan("raise-then-stage", List.of(
                MissionEvent.burn("booster-kick", 4.0, BurnAim.ENGINE),
                MissionEvent.separate("drop-booster"),
                MissionEvent.coast("coast-thirty", 30.0),
                MissionEvent.burn("upper-prograde", 3.0, BurnAim.PROGRADE)));

        VesselState flown = MissionExecutor.execute(initial, plan);
        if (flown.stages().size() != 1) {
            throw new IllegalStateException("Mission must drop the booster and keep the upper stage");
        }
        if (!(flown.orbit().epochSeconds() > 32.0)) {
            throw new IllegalStateException("Coast and burns must advance epoch");
        }
        if (!(flown.orbit().velocityMetersPerSecond().magnitude() > speedBefore)) {
            throw new IllegalStateException("Prograde map must raise inertial speed");
        }
        if (!(flown.orbit().specificOrbitalEnergy() > energyBefore)) {
            throw new IllegalStateException("Mission map must raise specific orbital energy");
        }
        if (!(flown.totalMassKg() < initial.totalMassKg() - 1.0)) {
            throw new IllegalStateException("Mission burns and staging must drop mass");
        }
        return new Result(
                plan.describe(),
                flown.stages().size(),
                flown.orbit().velocityMetersPerSecond().magnitude(),
                flown.orbit().specificOrbitalEnergy() - energyBefore);
    }
}
