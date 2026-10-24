package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Moon and Mars are walkable worlds. Deimos is not. Station pocket is not the surface.
 */
public final class BodyWorldSlice {
    private BodyWorldSlice() {
    }

    public record Result(boolean moonWorld, boolean deimosMap, double moonG, boolean marsWorld, double marsG) {
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
        double marsG = SolarSystem.mars().surfaceGravityMetersPerSecond2();
        if (Math.abs(marsG - 3.71) > 0.05) {
            throw new IllegalStateException("Mars g must stay ~3.71, g=" + marsG);
        }
        double marsFactor = marsG / 9.80665;
        if (marsFactor < 0.36 || marsFactor > 0.40) {
            throw new IllegalStateException("Mars gravity factor ~0.38, f=" + marsFactor);
        }
        return new Result(true, true, moonG, true, marsG);
    }
}
