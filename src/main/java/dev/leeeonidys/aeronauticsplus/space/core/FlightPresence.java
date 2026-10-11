package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Where the pile lives. Blocks on the pad, Aeronautics entity in the Overworld,
 * or Kepler map at and above 20 km. Not Minecraft build height.
 */
public enum FlightPresence {
    BLOCKS_ON_PAD,
    WORLD_ENTITY,
    ORBIT_MAP
}
