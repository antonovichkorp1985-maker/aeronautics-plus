package dev.leeeonidys.aeronauticsplus.space.compile;

/** One physical block in a vessel grid. */
public record VesselBlockOccupant(GridPos pos, VesselPartSpec spec, BlockFace facing) {
    public VesselBlockOccupant {
        if (pos == null || spec == null || facing == null) {
            throw new IllegalArgumentException("Vessel block occupant is incomplete");
        }
    }

    public String componentId() {
        return spec.id() + "@" + pos;
    }
}
