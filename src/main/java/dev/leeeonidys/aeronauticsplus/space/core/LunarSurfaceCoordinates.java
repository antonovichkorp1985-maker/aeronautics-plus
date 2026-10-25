package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Equirectangular coordinates for the flat, walkable Moon dimension.
 * Minecraft X is east, Z is south, and Y remains altitude. One block is treated as one metre.
 * This is a coordinate/sky convention, not spherical terrain: the lunar radius converts position
 * to latitude/longitude, while the generated surface remains an infinite plane.
 */
public final class LunarSurfaceCoordinates {
    public static final double MOON_RADIUS_METERS = 1_737_400.0;
    public static final double METERS_PER_DEGREE_AT_EQUATOR = Math.PI * MOON_RADIUS_METERS / 180.0;
    private static final double HALF_PI = Math.PI / 2.0;
    /** Mean Earth–Moon centre distance divided by lunar radius. */
    private static final double EARTH_DISTANCE_IN_LUNAR_RADII = 384_400.0 / 1_737.4;

    private LunarSurfaceCoordinates() {
    }

    /** East longitude in radians, wrapped to [-π, π). */
    public static double longitudeRadians(double xBlocks) {
        requireFinite(xBlocks);
        return wrapRadians(xBlocks / MOON_RADIUS_METERS);
    }

    /** North latitude in radians, clamped at the poles; Minecraft north is -Z. */
    public static double latitudeRadians(double zBlocks) {
        requireFinite(zBlocks);
        return Math.max(-HALF_PI, Math.min(HALF_PI, -zBlocks / MOON_RADIUS_METERS));
    }

    public static double longitudeDegrees(double xBlocks) {
        return Math.toDegrees(longitudeRadians(xBlocks));
    }

    public static double latitudeDegrees(double zBlocks) {
        return Math.toDegrees(latitudeRadians(zBlocks));
    }

    /** Local direction to the Earth at (0° longitude, 0° latitude). */
    public static Direction earthDirection(double xBlocks, double zBlocks) {
        double lon = longitudeRadians(xBlocks);
        double lat = latitudeRadians(zBlocks);
        double cosLat = Math.cos(lat);
        // Player position on the reference sphere, with the sub-Earth point at +X.
        double px = cosLat * Math.cos(lon);
        double py = Math.sin(lat);
        double pz = cosLat * Math.sin(lon);
        // Vector from the player's location to the sub-Earth point. Scaling by R is irrelevant.
        double dx = EARTH_DISTANCE_IN_LUNAR_RADII - px;
        double dy = -py;
        double dz = -pz;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (!(length > 1.0e-12)) {
            return new Direction(0.0, HALF_PI, true);
        }
        dx /= length;
        dy /= length;
        dz /= length;
        double upX = px;
        double upY = py;
        double upZ = pz;
        double eastX = -Math.sin(lon);
        double eastZ = Math.cos(lon);
        double northX = -Math.sin(lat) * Math.cos(lon);
        double northY = Math.cos(lat);
        double northZ = -Math.sin(lat) * Math.sin(lon);
        double up = dx * upX + dy * upY + dz * upZ;
        double east = dx * eastX + dz * eastZ;
        double north = dx * northX + dy * northY + dz * northZ;
        double elevation = Math.atan2(up, Math.hypot(east, north));
        double azimuthFromNorth = Math.atan2(east, north);
        // A portion of Earth's 1.9°-radius disk remains above the horizon while its centre is
        // slightly below it. The renderer's later terrain pass clips the lower part naturally.
        return new Direction(azimuthFromNorth, elevation, elevation > -Math.toRadians(1.9));
    }

    private static double wrapRadians(double angle) {
        double wrapped = angle % (2.0 * Math.PI);
        if (wrapped < -Math.PI) {
            wrapped += 2.0 * Math.PI;
        } else if (wrapped >= Math.PI) {
            wrapped -= 2.0 * Math.PI;
        }
        return wrapped;
    }

    private static void requireFinite(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Lunar coordinates must be finite");
        }
    }

    public record Direction(double azimuthFromNorthRadians, double elevationRadians, boolean visible) {
        public Direction {
            if (!Double.isFinite(azimuthFromNorthRadians) || !Double.isFinite(elevationRadians)) {
                throw new IllegalArgumentException("Earth direction must be finite");
            }
        }
    }
}
