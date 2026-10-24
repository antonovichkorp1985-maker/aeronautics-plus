package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * U.S. Standard Atmosphere 1976, geopotential layers 0–86 km.
 * ChemMod v1.0.0 {@code Atmosphere.EARTH} is only troposphere + isothermal tropopause.
 * This column is AP flight; HUD q via ChemMod stays the two-layer sample when present.
 */
public final class Isa1976 {
    private static final double G0 = 9.80665;
    private static final double M = 0.0289647;
    private static final double R = Atmosphere.GAS_CONSTANT;
    /** Geopotential bases, metres. */
    private static final double[] H = {0.0, 11_000.0, 20_000.0, 32_000.0, 47_000.0, 51_000.0, 71_000.0};
    private static final double[] T = {288.15, 216.65, 216.65, 228.65, 270.65, 270.65, 214.65};
    /** Lapse K/m. Positive = temperature rises with height. */
    private static final double[] L = {-0.0065, 0.0, 0.0010, 0.0028, 0.0, -0.0028, -0.0020};
    private static final double[] P = new double[H.length];
    private static final double MESOPAUSE_KELVIN = 186.87;
    private static final double MESOPAUSE_METRES = 84_852.0;

    static {
        P[0] = PropulsionMath.SEA_LEVEL_PRESSURE_PASCALS;
        for (int i = 1; i < H.length; i++) {
            P[i] = pressureInLayer(i - 1, H[i]);
        }
    }

    private Isa1976() {
    }

    public static double temperatureKelvin(double geopotentialMetres) {
        double h = Math.max(0.0, geopotentialMetres);
        if (h >= MESOPAUSE_METRES) {
            return MESOPAUSE_KELVIN;
        }
        int i = layer(h);
        return T[i] + L[i] * (h - H[i]);
    }

    public static double pressurePascals(double geopotentialMetres) {
        double h = Math.max(0.0, geopotentialMetres);
        if (h >= MESOPAUSE_METRES) {
            double pMeso = pressureInLayer(H.length - 1, MESOPAUSE_METRES);
            return pMeso * Math.exp(
                    -G0 * M * (h - MESOPAUSE_METRES) / (R * MESOPAUSE_KELVIN));
        }
        return pressureInLayer(layer(h), h);
    }

    private static int layer(double h) {
        int i = 0;
        for (int n = 1; n < H.length; n++) {
            if (h >= H[n]) {
                i = n;
            }
        }
        return i;
    }

    private static double pressureInLayer(int i, double h) {
        double dh = h - H[i];
        double lapse = L[i];
        if (Math.abs(lapse) < 1.0e-12) {
            return P[i] * Math.exp(-G0 * M * dh / (R * T[i]));
        }
        double t = T[i] + lapse * dh;
        return P[i] * Math.pow(t / T[i], -G0 * M / (R * lapse));
    }
}
