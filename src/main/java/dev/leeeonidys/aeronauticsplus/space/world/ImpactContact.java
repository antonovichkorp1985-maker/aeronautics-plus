package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import dev.leeeonidys.aeronauticsplus.space.core.PartImpact;

/**
 * One local contact between a vessel cell and an asteroid block.
 *
 * <p>{@code closingMetersPerSecond} is the component of the relative velocity along the
 * contact normal — the part of the motion that actually drives into the rock.
 * {@code shearMetersPerSecond} is what slides along the rock face.
 * {@code normalDotFacing} tells whether the contact is face-on for a thin plate.
 *
 * <p>Cell coordinates are payload coordinates (pad-relative), so the ledger stays valid
 * across chunk reloads without keeping any world state.
 */
public record ImpactContact(
        String partId,
        VesselPartKind kind,
        int cellX,
        int cellY,
        int cellZ,
        double closingMetersPerSecond,
        double shearMetersPerSecond,
        double normalDotFacing) {

    public double load() {
        return PartImpact.load(kind, closingMetersPerSecond, shearMetersPerSecond, normalDotFacing);
    }

    public PartImpact.Consequence consequence() {
        return PartImpact.consequence(kind);
    }

    public String cellKey() {
        return cellX + "," + cellY + "," + cellZ;
    }
}
