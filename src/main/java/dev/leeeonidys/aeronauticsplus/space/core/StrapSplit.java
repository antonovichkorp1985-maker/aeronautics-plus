package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/**
 * After radial pyro in flight: core continues, strap-ons keep the same trajectory
 * as their own vessels. Not ChemMod air.
 */
public record StrapSplit(VesselState core, List<VesselState> sides) {
    public StrapSplit {
        if (core == null) {
            throw new IllegalArgumentException("Strap split needs a core");
        }
        sides = List.copyOf(sides == null ? List.of() : sides);
        if (sides.isEmpty()) {
            throw new IllegalArgumentException("Strap split needs at least one side booster");
        }
    }
}
