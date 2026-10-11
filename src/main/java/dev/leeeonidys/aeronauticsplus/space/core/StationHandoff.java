package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Parked circular orbit MAY become walkable blocks — only on a player command,
 * and only if there is a habitat. Unmanned satellites never. A burn stays on
 * the Kepler map. Not Minecraft Y, not the pad, not ChemMod air.
 */
public final class StationHandoff {
    /** Apoapsis−periapsis must stay inside this band to count as parked. */
    public static final double PARK_BAND_METERS = 30_000.0;
    /** Islands in the station pocket, metres = blocks. Not Overworld Y. */
    public static final int ISLAND_STRIDE = 512;
    public static final int ISLAND_Y = 64;

    private StationHandoff() {
    }

    /**
     * Bound, nearly circular, periapsis still on the map.
     * A 200×500 km ellipse is a transfer, not a station.
     */
    public static boolean parked(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        if (WorldHandoff.inWorld(orbit)) {
            return false;
        }
        if (!(orbit.specificOrbitalEnergy() < 0.0)) {
            return false;
        }
        double radius = orbit.centralBody().radiusMeters();
        double periAlt = orbit.periapsisRadiusMeters() - radius;
        double apoAlt = orbit.apoapsisRadiusMeters() - radius;
        if (!(periAlt >= WorldHandoff.MAP_ALTITUDE_METERS)) {
            return false;
        }
        return (apoAlt - periAlt) < PARK_BAND_METERS;
    }

    public static int islandX(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Island index must be non-negative");
        }
        return index * ISLAND_STRIDE;
    }
}
