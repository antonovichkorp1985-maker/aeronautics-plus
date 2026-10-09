package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.Locale;

/** Named in-flight failure. Compile-time blueprint errors stay on {@link VesselDiagnostic}. */
public record FlightFault(String code, String message, double atSeconds) {
    public static final String DRY_TANK = "DRY_TANK";
    public static final String ZERO_THRUST = "ZERO_THRUST";
    public static final String FEED_BREAK = "FEED_BREAK";

    public FlightFault {
        if (code == null || code.isBlank() || message == null || message.isBlank()) {
            throw new IllegalArgumentException("Flight fault identity is invalid");
        }
        if (atSeconds < 0.0 || !Double.isFinite(atSeconds)) {
            throw new IllegalArgumentException("Fault time must be finite and non-negative");
        }
    }

    public static FlightFault dryTank(double atSeconds) {
        return new FlightFault(DRY_TANK,
                "Рабочее тело закончилось на " + format(atSeconds) + " с — burn прерван",
                atSeconds);
    }

    public static FlightFault zeroThrust() {
        return new FlightFault(ZERO_THRUST,
                "Нет тяги: двигатели выключены или дроссель равен нулю",
                0.0);
    }

    public static FlightFault feedBreak(double atSeconds) {
        return new FlightFault(FEED_BREAK,
                "Обрыв топливной магистрали на " + format(atSeconds) + " с — оставшийся burn отменён",
                atSeconds);
    }

    private static String format(double seconds) {
        return String.format(Locale.ROOT, "%.3f", seconds);
    }
}
