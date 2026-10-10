package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.Locale;

/** Named in-flight failure. Compile-time blueprint errors stay on {@link VesselDiagnostic}. */
public record FlightFault(String code, String message, double atSeconds) {
    public static final String DRY_TANK = "DRY_TANK";
    public static final String ZERO_THRUST = "ZERO_THRUST";
    public static final String FEED_BREAK = "FEED_BREAK";
    public static final String WRONG_PROPELLANT = "WRONG_PROPELLANT";
    public static final String HOLD_DOWN = "HOLD_DOWN";
    public static final String IMPACT = "IMPACT";
    public static final String NO_LEGS = "NO_LEGS";
    public static final String FAIRING_ATMOSPHERE = "FAIRING_ATMOSPHERE";

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

    public static FlightFault wrongPropellant() {
        return new FlightFault(WRONG_PROPELLANT,
                "Состав рабочего тела не совпадает с двигателем — burn отменён",
                0.0);
    }

    public static FlightFault holdDown(double thrustToWeight) {
        return new FlightFault(HOLD_DOWN,
                "Тяга на уровне моря не поднимает ракету (T/W=" + format(thrustToWeight)
                        + ") — зажимы не отпускают",
                0.0);
    }

    public static FlightFault impact(double atSeconds) {
        return new FlightFault(IMPACT,
                "Удар о поверхность на " + format(atSeconds) + " с",
                atSeconds);
    }

    public static FlightFault noLegs(double atSeconds) {
        return new FlightFault(NO_LEGS,
                "Нет посадочных ног — касание на " + format(atSeconds) + " с ломает ступень",
                atSeconds);
    }

    public static FlightFault fairingAtmosphere(double altitudeMeters, double dynamicPressurePascals) {
        return new FlightFault(FAIRING_ATMOSPHERE,
                "Обтекатель не сбрасывают в плотных слоях (h=" + format(altitudeMeters)
                        + " м, q=" + format(dynamicPressurePascals) + " Па)",
                0.0);
    }

    private static String format(double seconds) {
        return String.format(Locale.ROOT, "%.3f", seconds);
    }
}
