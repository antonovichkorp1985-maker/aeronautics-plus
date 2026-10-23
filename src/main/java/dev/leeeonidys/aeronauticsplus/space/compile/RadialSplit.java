package dev.leeeonidys.aeronauticsplus.space.compile;

import java.util.List;

/**
 * After radial pyro: core keeps going, side boosters are their own piles.
 * Same ring as the axial decoupler, facing outward. Not ChemMod air.
 */
public record RadialSplit(VesselBlockGrid core, List<VesselBlockGrid> sides) {
    public RadialSplit {
        if (core == null || core.isEmpty()) {
            throw new IllegalArgumentException("Radial split needs a core");
        }
        sides = List.copyOf(sides == null ? List.of() : sides);
        if (sides.isEmpty()) {
            throw new IllegalArgumentException("Radial split needs at least one side booster");
        }
        for (VesselBlockGrid side : sides) {
            if (side == null || side.isEmpty()) {
                throw new IllegalArgumentException("A side booster grid is empty");
            }
        }
    }
}
