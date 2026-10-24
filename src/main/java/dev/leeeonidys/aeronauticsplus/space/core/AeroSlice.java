package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Cd(M) and ISA-1976. Not a flat 0.5. ChemMod two-layer is not this column above 20 km.
 */
public final class AeroSlice {
    private AeroSlice() {
    }

    public record Result(double subsonicCd, double transonicCd, double hypersonicCd, double t11, double t25) {
    }

    public static Result execute() {
        double sub = Aerodynamics.rocketCd(0.5);
        double trans = Aerodynamics.rocketCd(1.05);
        double hyper = Aerodynamics.rocketCd(8.0);
        if (sub < 0.15 || sub > 0.25) {
            throw new IllegalStateException("Subsonic Cd must sit near 0.20, Cd=" + sub);
        }
        if (!(trans > sub + 0.2)) {
            throw new IllegalStateException("Transonic Cd must peak above subsonic, Cd=" + trans);
        }
        if (!(hyper < trans - 0.2)) {
            throw new IllegalStateException("Hypersonic Cd must fall, Cd=" + hyper);
        }
        Atmosphere earth = Atmosphere.earth();
        double t11 = earth.temperatureKelvin(11_000.0);
        double t25 = earth.temperatureKelvin(25_000.0);
        if (Math.abs(t11 - 216.65) > 0.5) {
            throw new IllegalStateException("ISA tropopause must be 216.65 K, T=" + t11);
        }
        if (!(t25 > t11 + 2.0)) {
            throw new IllegalStateException("ISA-1976 must warm above 20 km, T25=" + t25);
        }
        double sea = earth.densityKgPerM3(0.0);
        if (Math.abs(sea - 1.225) > 0.01) {
            throw new IllegalStateException("Sea-level density must stay 1.225, rho=" + sea);
        }
        double sound = earth.speedOfSoundMetresPerSecond(0.0);
        if (Math.abs(sound - 340.3) > 2.0) {
            throw new IllegalStateException("Sea-level speed of sound ~340 m/s, a=" + sound);
        }
        return new Result(sub, trans, hyper, t11, t25);
    }
}
