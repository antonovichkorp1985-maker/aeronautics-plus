package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Where the pile lives. Blocks on the pad, Aeronautics entity in the Overworld,
 * Kepler map at and above 20 km, or walkable station blocks when parked.
 * Not Minecraft build height.
 */
public enum FlightPresence {
    BLOCKS_ON_PAD,
    WORLD_ENTITY,
    ORBIT_MAP,
    /** Walkable pocket after the enter-station command. Not a parking side effect. */
    STATION_BLOCKS
}
