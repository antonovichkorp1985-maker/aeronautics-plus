package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.CellOccupancy;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;

/** Integer block coordinate in the vessel grid. One block is one metre. */
public record GridPos(int x, int y, int z) implements Comparable<GridPos> {
    public GridPos offset(BlockFace face) {
        return new GridPos(x + face.dx(), y + face.dy(), z + face.dz());
    }

    public Vector3d centerMeters(GridPos origin) {
        return occupancyCentroidMeters(origin, CellOccupancy.FULL);
    }

    /** Mass centre of a part that occupies only part of the 1 m cell. */
    public Vector3d occupancyCentroidMeters(GridPos origin, CellOccupancy occupancy) {
        if (occupancy == null) {
            throw new IllegalArgumentException("Occupancy is required");
        }
        return new Vector3d(x - origin.x, y - origin.y, z - origin.z).add(occupancy.centroid());
    }

    @Override
    public int compareTo(GridPos other) {
        int byY = Integer.compare(y, other.y);
        if (byY != 0) {
            return byY;
        }
        int byX = Integer.compare(x, other.x);
        return byX != 0 ? byX : Integer.compare(z, other.z);
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
}
