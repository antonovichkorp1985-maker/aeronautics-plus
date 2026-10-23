package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/**
 * After in-flight radial pyro: core keeps going, sides are their own vehicles.
 * Same ring as axial, facing out. Not ChemMod air.
 */
public record RadialFlightSplit(FlightLoop core, List<FlightLoop> sides) {
    public RadialFlightSplit {
        if (core == null || core.presence() == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("Radial pyro fires in flight, not as pad blocks");
        }
        sides = List.copyOf(sides == null ? List.of() : sides);
        if (sides.isEmpty()) {
            throw new IllegalArgumentException("Radial pyro needs at least one side booster");
        }
        for (FlightLoop side : sides) {
            if (side == null || side.presence() == FlightPresence.BLOCKS_ON_PAD) {
                throw new IllegalStateException("A side booster must be in flight");
            }
        }
    }
}
