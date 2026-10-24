package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * The Moon's own clock. One solar day on the Moon is a synodic month: the Sun takes
 * 29.530589 Earth days to come back over the same meridian, because the Moon turns once per
 * orbit and not once per day. Vanilla gives every dimension a 24 000-tick (20 minute) day, which
 * is why a lunar base used to have a sunrise every twenty minutes.
 *
 * <p>Three honest ways to map that onto a game, all reachable by changing one constant:</p>
 * <ul>
 *   <li><b>{@link #SLOWDOWN} = 30</b> (current): one lunar day is 30 vanilla days,
 *       720 000 ticks, <b>10 hours</b> of wall clock — 5 hours of light, 5 hours of night. This is
 *       the physically consistent mapping <em>if</em> a game day stands for an Earth day:
 *       29.53 × 20 min ≈ 9 h 50 min.</li>
 *   <li><b>8</b>: the earlier playable compromise, 2 h 40 min per lunar day.</li>
 *   <li><b>2126</b>: absolute real time, one lunar day = 29.5 real days. Correct and unplayable.</li>
 * </ul>
 *
 * <p>Only the synodic (sun) day is implemented. The sidereal day, 27.32 Earth days, differs
 * because the Moon also travels around the Earth; vanilla draws no sidereal star rotation, so
 * there is nothing to drive with the second number. ChemMod owns heat and light budgets.</p>
 *
 * <p>Because the clock is derived from {@code gameTime}, skipping ahead needs an offset rather
 * than a new {@code dayTime}: {@code dayTime = (gameTime + offset) / SLOWDOWN}. The offset is
 * stored per dimension by {@code BodyClockData} and is what a cryo capsule moves.</p>
 */
public final class LunarTime {
    /** Vanilla day length, ticks. */
    public static final int VANILLA_DAY_TICKS = 24_000;
    /** Ticks per second at normal tick rate. */
    public static final int TICKS_PER_SECOND = 20;
    /** Real synodic month, Earth days: new moon to new moon, i.e. the solar day on the Moon. */
    public static final double SYNODIC_MONTH_EARTH_DAYS = 29.530589;
    /** Real sidereal month, Earth days: one turn against the stars. Documented, not driven. */
    public static final double SIDEREAL_MONTH_EARTH_DAYS = 27.321661;

    /** How many vanilla days one lunar day lasts. See the class comment for the alternatives. */
    public static final int SLOWDOWN = 30;
    /** One lunar day in ticks of real time. */
    public static final long LUNAR_DAY_TICKS = (long) VANILLA_DAY_TICKS * SLOWDOWN;
    /** Wall clock length of one lunar day, hours. */
    public static final double LUNAR_DAY_HOURS =
            LUNAR_DAY_TICKS / (double) TICKS_PER_SECOND / 3600.0;
    /** Wall clock length of lunar night, hours. */
    public static final double LUNAR_NIGHT_HOURS = LUNAR_DAY_HOURS / 2.0;

    private LunarTime() {
    }

    /** Backwards-compatible form: no offset. */
    public static long dayTimeFor(long gameTime) {
        return dayTimeFor(gameTime, 0L);
    }

    /**
     * The Moon's {@code dayTime} for a given server tick and clock offset. A pure function, so it
     * survives restarts without extra saved time and two chunks can never disagree.
     */
    public static long dayTimeFor(long gameTime, long offsetTicks) {
        return Math.floorDiv(gameTime + offsetTicks, SLOWDOWN);
    }

    /**
     * Offset that makes the clock read the next sunrise. Used by the cryo capsule: instead of
     * writing {@code dayTime} (which the next tick would overwrite), the mapping is shifted.
     */
    public static long offsetForNextDawn(long gameTime, long currentOffset) {
        long dayTime = dayTimeFor(gameTime, currentOffset);
        long phase = Math.floorMod(dayTime, (long) VANILLA_DAY_TICKS);
        // Strictly ahead of the current instant: standing at dawn skips a whole day, not zero.
        long target = dayTime - phase + VANILLA_DAY_TICKS;
        return target * SLOWDOWN - gameTime;
    }

    /** Position inside the lunar day, 0 = sunrise (06:00), 0.5 = sunset (18:00). */
    public static double dayFraction(long dayTime) {
        long t = Math.floorMod(dayTime, LUNAR_DAY_TICKS);
        return t / (double) LUNAR_DAY_TICKS;
    }

    /** Vanilla-style time of day: 0 at dawn, 6000 noon, 12000 dusk, 18000 midnight. */
    public static double vanillaFraction(long dayTime) {
        long t = Math.floorMod(dayTime, (long) VANILLA_DAY_TICKS);
        return t / (double) VANILLA_DAY_TICKS;
    }

    /** Sun above the horizon. Same rule vanilla uses for {@code Level.isDay()}. */
    public static boolean isDay(long dayTime) {
        long t = Math.floorMod(dayTime, (long) VANILLA_DAY_TICKS);
        return t < 12_000L;
    }

    /**
     * Sine of the Sun's elevation, clamped to 0..1: the geometric insolation factor.
     *
     * <p>This is the number AP hands over, not a temperature. Heat, regolith conductivity and
     * night cooling belong to ChemMod; AP only owns where the Sun is, which is the input such a
     * model needs and which vanilla's own clock cannot give at a 1/30 rate. 0 = sun below the
     * horizon, 1 = Sun at zenith.</p>
     */
    public static double insolation(long dayTime) {
        // Sunrise at fraction 0, zenith at 0.25, sunset at 0.5, nadir at 0.75.
        double fraction = vanillaFraction(dayTime);
        double elevation = Math.sin(fraction * Math.PI * 2.0);
        return Math.max(0.0, elevation);
    }

    /** Hours since sunrise, 0..24, for the HUD and the debug screen. */
    public static double localHours(long dayTime) {
        return (vanillaFraction(dayTime) * 24.0 + 6.0) % 24.0;
    }

    /** Coarse phase name for messages. Not a moon phase, that stays vanilla's eight steps. */
    public static String label(long dayTime) {
        double f = vanillaFraction(dayTime);
        if (f < 0.05 || f >= 0.95) {
            return "рассвет";
        }
        if (f < 0.45) {
            return "день";
        }
        if (f < 0.55) {
            return "закат";
        }
        return "ночь";
    }

    /** Human-readable summary, used by {@code /ap clock}. */
    public static String describe(long dayTime) {
        return String.format(
                java.util.Locale.ROOT,
                "%s, %s ч по местному, сутки %d ч (ночь %d ч, в %d раз длиннее ванильных)",
                label(dayTime),
                clock(localHours(dayTime)),
                Math.round(LUNAR_DAY_HOURS),
                Math.round(LUNAR_NIGHT_HOURS),
                SLOWDOWN);
    }

    private static String clock(double hours) {
        long h = (long) Math.floor(hours);
        long m = Math.round((hours - h) * 60.0);
        if (m == 60) {
            h = (h + 1) % 24;
            m = 0;
        }
        return String.format(java.util.Locale.ROOT, "%02d:%02d", h % 24, m);
    }
}
