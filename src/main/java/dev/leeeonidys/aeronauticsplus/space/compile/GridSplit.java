package dev.leeeonidys.aeronauticsplus.space.compile;

/**
 * After the pyro ring fires: upper keeps going, booster is a pile that can
 * return as Minecraft blocks. Not ChemMod air.
 */
public record GridSplit(VesselBlockGrid continuing, VesselBlockGrid booster) {
    public GridSplit {
        if (continuing == null || booster == null) {
            throw new IllegalArgumentException("Pyro split needs both stacks");
        }
        if (continuing.isEmpty() || booster.isEmpty()) {
            throw new IllegalArgumentException("Pyro split left an empty stack");
        }
    }
}
