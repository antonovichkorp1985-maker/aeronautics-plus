package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Arithmetic guard for the lunar clock, run at startup like the other slices.
 *
 * <p>The whole feature is three constants and one division, so the only way it breaks is a typo:
 * a day that is not 24 000 vanilla ticks long, a fraction that runs backwards, or a slowdown that
 * quietly turns the Moon back into a 20-minute planet.</p>
 */
public final class LunarTimeSlice {
    private LunarTimeSlice() {
    }

    public record Result(long lunarDayTicks, double hours, double days, double compression, boolean dayAtNoon) {
    }

    public static Result execute() {
        long ticks = LunarTime.LUNAR_DAY_TICKS;
        if (ticks != (long) LunarTime.VANILLA_DAY_TICKS * LunarTime.SLOWDOWN) {
            throw new IllegalStateException("Lunar day must be SLOWDOWN vanilla days, got " + ticks);
        }
        if (LunarTime.SLOWDOWN < 2) {
            throw new IllegalStateException(
                    "A lunar day slower than vanilla is the whole point; SLOWDOWN=" + LunarTime.SLOWDOWN);
        }
        double hours = LunarTime.LUNAR_DAY_HOURS;
        double seconds = ticks / (double) LunarTime.TICKS_PER_SECOND;
        if (Math.abs(seconds - 9_600.0) > 0.5) {
            throw new IllegalStateException("8x slowdown must be 9600 s, got " + seconds);
        }
        // The clock must be monotonic and advance exactly one tick per SLOWDOWN game ticks.
        long previous = Long.MIN_VALUE;
        for (long t = 0; t < 100 * LunarTime.SLOWDOWN; t++) {
            long dayTime = LunarTime.dayTimeFor(t);
            if (dayTime < previous) {
                throw new IllegalStateException("Lunar clock ran backwards at gameTime=" + t);
            }
            previous = dayTime;
        }
        if (LunarTime.dayTimeFor(LunarTime.SLOWDOWN - 1) != 0
                || LunarTime.dayTimeFor(LunarTime.SLOWDOWN) != 1) {
            throw new IllegalStateException("Lunar clock must step once per SLOWDOWN ticks");
        }
        if (!LunarTime.isDay(6_000) || LunarTime.isDay(18_000)) {
            throw new IllegalStateException("Noon must be day and midnight must be night");
        }
        double noonFraction = LunarTime.vanillaFraction(6_000);
        if (Math.abs(noonFraction - 0.25) > 1.0e-9) {
            throw new IllegalStateException("Vanilla noon must stay at 0.25, got " + noonFraction);
        }
        if (Math.abs(LunarTime.dayFraction(0)) > 1.0e-9
                || Math.abs(LunarTime.dayFraction(ticks / 2) - 0.5) > 1.0e-9) {
            throw new IllegalStateException("Lunar day fraction must run 0..1 across one lunar day");
        }
        double compression = LunarTime.SYNODIC_MONTH_EARTH_DAYS / LunarTime.SLOWDOWN;
        return new Result(ticks, hours, ticks / (double) LunarTime.VANILLA_DAY_TICKS,
                compression, LunarTime.isDay(6_000));
    }
}
