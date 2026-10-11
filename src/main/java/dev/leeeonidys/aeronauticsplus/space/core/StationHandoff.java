package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Parked circular orbit can become walkable blocks (ISS / Aftermath station).
 * Kepler map stays for transfers. Not Minecraft Y, not the pad, not ChemMod air.
 */
public final class StationHandoff {
    /** Apoapsis−periapsis must stay inside this band to count as parked. */
    public static final double PARK_BAND_METERS = 30_000.0;

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
}
