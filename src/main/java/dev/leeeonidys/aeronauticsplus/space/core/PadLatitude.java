package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * TFC overworld latitude from Z, without importing TFC.
 * Matches the 1.21 {@code SolarCalculator} table: north pole at {@code z = -scale},
 * spawn {@code z = 0} at 45°N, equator at {@code z = +scale}, south pole at
 * {@code z = 3·scale}. Repeats every {@code 8·scale}. Not ChemMod air.
 */
public final class PadLatitude {
    /** {@code ClimateModel.hemisphereScale()} default when TFC is silent. */
    public static final double DEFAULT_HEMISPHERE_SCALE_BLOCKS = 20_000.0;

    private PadLatitude() {
    }

    public static double latitudeRadians(int worldZ, double hemisphereScaleBlocks) {
        if (!(hemisphereScaleBlocks > 0.0) || !Double.isFinite(hemisphereScaleBlocks)) {
            throw new IllegalArgumentException("Hemisphere scale must be finite and positive");
        }
        double quarter = 2.0 * hemisphereScaleBlocks;
        double period = 4.0 * quarter;
        double t = worldZ + hemisphereScaleBlocks;
        t -= period * Math.floor(t / period);
        if (t < quarter) {
            return (Math.PI / 2.0) * (1.0 - t / quarter);
        }
        if (t < 2.0 * quarter) {
            return -(Math.PI / 2.0) * ((t - quarter) / quarter);
        }
        if (t < 3.0 * quarter) {
            return -(Math.PI / 2.0) * (1.0 - (t - 2.0 * quarter) / quarter);
        }
        return (Math.PI / 2.0) * ((t - 3.0 * quarter) / quarter);
    }

    public static double latitudeDegrees(int worldZ, double hemisphereScaleBlocks) {
        return Math.toDegrees(latitudeRadians(worldZ, hemisphereScaleBlocks));
    }

    public static OrbitState rotatingPad(CelestialBody body, double latitudeRadians) {
        if (body == null) {
            throw new IllegalArgumentException("Body is required");
        }
        if (!Double.isFinite(latitudeRadians) || Math.abs(latitudeRadians) > Math.PI / 2.0 + 1.0e-9) {
            throw new IllegalArgumentException("Latitude must be in [-π/2, π/2]");
        }
        double radius = body.radiusMeters();
        Vector3d position = new Vector3d(
                radius * Math.cos(latitudeRadians),
                radius * Math.sin(latitudeRadians),
                0.0);
        return new OrbitState(body, position, body.surfaceVelocityMetersPerSecond(position), 0.0);
    }
}
