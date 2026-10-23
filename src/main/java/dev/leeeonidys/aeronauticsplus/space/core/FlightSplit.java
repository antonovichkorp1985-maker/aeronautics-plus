package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * After in-flight pyro: upper keeps going, booster is its own vehicle.
 * The ring splits parts, not a catalog flag. Not ChemMod air.
 */
public record FlightSplit(FlightLoop continuing, FlightLoop booster) {
    public FlightSplit {
        if (continuing == null || booster == null) {
            throw new IllegalArgumentException("Pyro split needs both vehicles");
        }
        if (continuing.presence() == FlightPresence.BLOCKS_ON_PAD
                || booster.presence() == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("Pyro fires in flight, not as pad blocks");
        }
    }
}
