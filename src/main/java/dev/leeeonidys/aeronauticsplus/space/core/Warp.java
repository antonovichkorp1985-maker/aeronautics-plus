package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Physics multiplier on the flying stack. World ticks stay 20 Hz.
 * Dense column (below the 50 km fairing floor) caps like thrust.
 * Not molesAt, not a time-skip of the Overworld.
 */
public final class Warp {
    public static final int[] FACTORS = {1, 5, 10, 50};
    public static final int THRUST_CAP = 4;
    public static final double LOW_ALTITUDE_METERS = 2_500.0;
    /** Same floor as {@link Aerodynamics#FAIRING_MIN_ALTITUDE_METERS}. */
    public static final double DENSE_AIR_METERS = Aerodynamics.FAIRING_MIN_ALTITUDE_METERS;

    private Warp() {
    }

    public static int factor(int index) {
        return FACTORS[clampIndex(index)];
    }

    public static int clampIndex(int index) {
        if (index < 0) {
            return 0;
        }
        if (index >= FACTORS.length) {
            return FACTORS.length - 1;
        }
        return index;
    }

    public static int steps(boolean crewed, int index, boolean thrusting, boolean falling, double altitudeMeters) {
        if (!crewed) {
            return 1;
        }
        int steps = factor(index);
        if (thrusting) {
            steps = Math.min(steps, THRUST_CAP);
        }
        if (altitudeMeters < DENSE_AIR_METERS) {
            steps = Math.min(steps, THRUST_CAP);
        }
        if (falling && altitudeMeters < LOW_ALTITUDE_METERS) {
            steps = 1;
        }
        return Math.max(1, steps);
    }
}
