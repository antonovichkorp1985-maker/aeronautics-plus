package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.Locale;

/**
 * Pilot strip. Not an orientation GUI, not ChemMod air.
 */
public final class FlightReadout {
    private FlightReadout() {
    }

    public static String[] lines(double altitudeMeters, double horizontalMetersPerSecond,
                                 double radialMetersPerSecond, double throttle) {
        if (!Double.isFinite(altitudeMeters) || !Double.isFinite(horizontalMetersPerSecond)
                || !Double.isFinite(radialMetersPerSecond) || !Double.isFinite(throttle)) {
            throw new IllegalArgumentException("Readout values must be finite");
        }
        return new String[] {
            String.format(Locale.ROOT, "h  %.0f m", altitudeMeters),
            String.format(Locale.ROOT, "v> %.0f m/s", horizontalMetersPerSecond),
            String.format(Locale.ROOT, "v^ %.0f m/s", radialMetersPerSecond),
            String.format(Locale.ROOT, "gas %.0f%%", throttle * 100.0)
        };
    }
}
