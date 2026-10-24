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
        return lines(altitudeMeters, horizontalMetersPerSecond, radialMetersPerSecond, throttle, warp, hold,
                dynamicPressurePascals, Double.NaN, Double.NaN);
    }

    public static String[] lines(double altitudeMeters, double horizontalMetersPerSecond,
                                 double radialMetersPerSecond, double throttle, int warp, String hold,
                                 double dynamicPressurePascals, double apoapsisAltitudeMeters,
                                 double periapsisAltitudeMeters) {
        return lines(altitudeMeters, horizontalMetersPerSecond, radialMetersPerSecond, throttle, warp, hold,
                dynamicPressurePascals, apoapsisAltitudeMeters, periapsisAltitudeMeters, "earth");
    }

    public static String[] lines(double altitudeMeters, double horizontalMetersPerSecond,
                                 double radialMetersPerSecond, double throttle, int warp, String hold,
                                 double dynamicPressurePascals, double apoapsisAltitudeMeters,
                                 double periapsisAltitudeMeters, String bodyId) {
        if (!Double.isFinite(altitudeMeters) || !Double.isFinite(horizontalMetersPerSecond)
                || !Double.isFinite(radialMetersPerSecond) || !Double.isFinite(throttle)
                || !Double.isFinite(dynamicPressurePascals) || dynamicPressurePascals < 0.0) {
            throw new IllegalArgumentException("Readout values must be finite");
        }
        if (bodyId == null || bodyId.isBlank()) {
            throw new IllegalArgumentException("Body id is required");
        }
        if (warp < 1) {
            throw new IllegalArgumentException("Warp must be at least 1");
        }
        if (Double.isInfinite(periapsisAltitudeMeters)) {
            throw new IllegalArgumentException("Periapsis altitude must be finite");
        }
        String holdLabel = hold == null || hold.isBlank() ? "off" : hold;
        String ap = Double.isNaN(apoapsisAltitudeMeters)
                ? "Ap  —"
                : !Double.isFinite(apoapsisAltitudeMeters)
                ? "Ap  esc"
                : String.format(Locale.ROOT, "Ap %.0f m", apoapsisAltitudeMeters);
        String pe = Double.isNaN(periapsisAltitudeMeters)
                ? "Pe  —"
                : String.format(Locale.ROOT, "Pe %.0f m", periapsisAltitudeMeters);
        return new String[] {
            String.format(Locale.ROOT, "h  %.0f m", altitudeMeters),
            String.format(Locale.ROOT, "v> %.0f m/s", horizontalMetersPerSecond),
            String.format(Locale.ROOT, "v^ %.0f m/s", radialMetersPerSecond),
            String.format(Locale.ROOT, "gas %.0f%%", throttle * 100.0),
            String.format(Locale.ROOT, "q  %.0f Pa", dynamicPressurePascals),
            ap,
            pe,
            String.format(Locale.ROOT, "x%d", warp),
            holdLabel,
            "body " + bodyId
        };
    }
}
