package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Overworld Aeronautics entity versus Kepler map.
 * Not Minecraft build height, not ChemMod air.
 * Below 20 km the vehicle stays in the world; at 20 km it leaves for the map.
 * Coming back below 20 km returns to the world. Pyro is independent of this line.
 */
public final class WorldHandoff {
    /** Geometric altitude where the stack leaves the Overworld for the orbit map. */
    public static final double MAP_ALTITUDE_METERS = 20_000.0;

    private WorldHandoff() {
    }

    public static boolean inWorld(double altitudeMeters) {
        if (!Double.isFinite(altitudeMeters)) {
            throw new IllegalArgumentException("Altitude must be finite");
        }
        return altitudeMeters < MAP_ALTITUDE_METERS;
    }

    public static boolean onMap(double altitudeMeters) {
        return !inWorld(altitudeMeters);
    }

    public static boolean inWorld(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        return inWorld(orbit.altitudeMeters());
    }

    public static boolean onMap(OrbitState orbit) {
        return !inWorld(orbit);
    }
}
