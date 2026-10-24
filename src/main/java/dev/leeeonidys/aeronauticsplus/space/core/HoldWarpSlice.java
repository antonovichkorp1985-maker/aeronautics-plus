package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;

/**
 * Hold points the nose; warp is extra physics steps. Not ChemMod air.
 */
public final class HoldWarpSlice {
    private HoldWarpSlice() {
    }

    public record Result(double progradeDot, double retroDot, int coastWarp, int thrustWarp) {
    }

    public static Result execute() {
        if (Warp.factor(0) != 1 || Warp.factor(1) != 5 || Warp.factor(2) != 10 || Warp.factor(3) != 50) {
            throw new IllegalStateException("Warp ladder must be 1,5,10,50");
        }
        if (Warp.steps(false, 3, false, false, 100_000.0) != 1) {
            throw new IllegalStateException("Unmanned stacks stay at 1×");
        }
        int coast = Warp.steps(true, 3, false, false, 100_000.0);
        if (coast != 50) {
            throw new IllegalStateException("Crewed coast warp must be 50×, got " + coast);
        }
        int thrust = Warp.steps(true, 3, true, false, 100_000.0);
        if (thrust != Warp.THRUST_CAP) {
            throw new IllegalStateException("Thrusting warp must cap at " + Warp.THRUST_CAP);
        }
        if (Warp.steps(true, 3, false, true, 1_000.0) != 1) {
            throw new IllegalStateException("Falling near the ground stays 1×");
        }
        int dense = Warp.steps(true, 3, false, false, 20_000.0);
        if (dense != Warp.THRUST_CAP) {
            throw new IllegalStateException("20 km coast warp must cap like thrust, got " + dense);
        }
        if (Warp.steps(true, 3, false, false, 80_000.0) != 50) {
            throw new IllegalStateException("80 km coast warp must stay 50×");
        }
        OrbitState pad = SpaceBodies.rotatingPad();
        Vector3d prograde = HoldAim.inertialAim(pad, HoldAim.Mode.PROGRADE);
        Vector3d retro = HoldAim.inertialAim(pad, HoldAim.Mode.RETRO);
        Vector3d radial = HoldAim.inertialAim(pad, HoldAim.Mode.RADIAL);
        double progradeDot = prograde.dot(pad.velocityMetersPerSecond().normalized());
        double retroDot = retro.dot(pad.velocityMetersPerSecond().normalized());
        if (progradeDot < 0.99) {
            throw new IllegalStateException("Prograde must follow velocity, dot=" + progradeDot);
        }
        if (retroDot > -0.99) {
            throw new IllegalStateException("Retro must oppose velocity, dot=" + retroDot);
        }
        if (radial.dot(pad.radialUnit()) < 0.99) {
            throw new IllegalStateException("Radial hold must stay radial");
        }
        Attitude pointed = HoldAim.attitude(pad, HoldAim.Mode.PROGRADE);
        if (pointed.bodyY().dot(prograde) < 0.99) {
            throw new IllegalStateException("Hold attitude must put body +Y on the aim");
        }
        if (HoldAim.cycle(HoldAim.Mode.RETRO) != HoldAim.Mode.OFF) {
            throw new IllegalStateException("Hold cycle must wrap to off");
        }
        return new Result(progradeDot, retroDot, coast, thrust);
    }
}
