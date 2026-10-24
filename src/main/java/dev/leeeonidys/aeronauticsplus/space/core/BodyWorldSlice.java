package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Moon is a walkable world. Deimos is not. Station pocket is not the surface.
 */
public final class BodyWorldSlice {
    private BodyWorldSlice() {
    }

    public record Result(boolean moonWorld, boolean deimosMap, double moonG) {
    }

    public static Result execute() {
        double moonG = SolarSystem.moon().surfaceGravityMetersPerSecond2();
        if (Math.abs(moonG - 1.62) > 0.05) {
            throw new IllegalStateException("Moon g must stay ~1.62, g=" + moonG);
        }
        double factor = moonG / 9.80665;
        if (factor < 0.14 || factor > 0.20) {
            throw new IllegalStateException("Moon gravity factor ~0.165, f=" + factor);
        }
        if (SolarSystem.minorMoons().stream().noneMatch(moon -> "deimos".equals(moon.body().id()) && moon.mapOnly())) {
            throw new IllegalStateException("Deimos stays a map dot");
        }
        return new Result(true, true, moonG);
    }
}
