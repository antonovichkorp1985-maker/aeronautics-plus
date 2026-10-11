package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.Locale;

/**
 * Pilot strip. Not an orientation GUI. q is the air column, not the leak field.
 */
public final class FlightReadout {
    private FlightReadout() {
    }

    public static String[] lines(double altitudeMeters, double horizontalMetersPerSecond,
                                 double radialMetersPerSecond, double throttle) {
        return lines(altitudeMeters, horizontalMetersPerSecond, radialMetersPerSecond, throttle, 1, "off", 0.0);
    }

    public static String[] lines(double altitudeMeters, double horizontalMetersPerSecond,
                                 double radialMetersPerSecond, double throttle, int warp, String hold) {
        return lines(altitudeMeters, horizontalMetersPerSecond, radialMetersPerSecond, throttle, warp, hold, 0.0);
    }

    public static String[] lines(double altitudeMeters, double horizontalMetersPerSecond,
                                 double radialMetersPerSecond, double throttle, int warp, String hold,
                                 double dynamicPressurePascals) {
        if (!Double.isFinite(altitudeMeters) || !Double.isFinite(horizontalMetersPerSecond)
                || !Double.isFinite(radialMetersPerSecond) || !Double.isFinite(throttle)
                || !Double.isFinite(dynamicPressurePascals) || dynamicPressurePascals < 0.0) {
            throw new IllegalArgumentException("Readout values must be finite");
        }
        if (warp < 1) {
            throw new IllegalArgumentException("Warp must be at least 1");
        }
        String holdLabel = hold == null || hold.isBlank() ? "off" : hold;
        return new String[] {
            String.format(Locale.ROOT, "h  %.0f m", altitudeMeters),
            String.format(Locale.ROOT, "v> %.0f m/s", horizontalMetersPerSecond),
            String.format(Locale.ROOT, "v^ %.0f m/s", radialMetersPerSecond),
            String.format(Locale.ROOT, "gas %.0f%%", throttle * 100.0),
            String.format(Locale.ROOT, "q  %.0f Pa", dynamicPressurePascals),
            String.format(Locale.ROOT, "x%d", warp),
            holdLabel
        };
    }
}
