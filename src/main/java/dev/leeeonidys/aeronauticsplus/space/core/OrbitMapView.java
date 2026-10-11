package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Plot of the Kepler map. Earth is a disk; altitude is linear out to parking (200 km)
 * so the 20 km handoff ring is visible. Not geometric Earth scale. Not ChemMod air.
 */
public final class OrbitMapView {
    /** Fraction of the plot radius that is the Earth disk. */
    public static final double EARTH_FRACTION = 0.72;
    /** Outer ring of the plot: parking-orbit altitude. */
    public static final double VIEW_ALTITUDE_METERS = 200_000.0;

    private OrbitMapView() {
    }

    public record Plot(double x, double y) {
        public Plot {
            if (!Double.isFinite(x) || !Double.isFinite(y)) {
                throw new IllegalArgumentException("Plot coordinates must be finite");
            }
        }

        public double radius() {
            return Math.hypot(x, y);
        }
    }

    public static Plot plot(double altitudeMeters, double angleRadians) {
        if (!Double.isFinite(altitudeMeters) || !Double.isFinite(angleRadians)) {
            throw new IllegalArgumentException("Plot altitude and angle must be finite");
        }
        double t = Math.max(0.0, altitudeMeters) / VIEW_ALTITUDE_METERS;
        if (t > 1.0) {
            t = 1.0;
        }
        double r = EARTH_FRACTION + (1.0 - EARTH_FRACTION) * t;
        return new Plot(r * Math.cos(angleRadians), r * Math.sin(angleRadians));
    }

    public static Plot plot(OrbitMapTrack track) {
        if (track == null) {
            throw new IllegalArgumentException("Plot needs a track");
        }
        return plot(track.altitudeMeters(), track.angleRadians());
    }

    /** Radius of the 20 km handoff ring in plot units (0..1). */
    public static double handoffRadius() {
        return EARTH_FRACTION
                + (1.0 - EARTH_FRACTION) * (WorldHandoff.MAP_ALTITUDE_METERS / VIEW_ALTITUDE_METERS);
    }
}
