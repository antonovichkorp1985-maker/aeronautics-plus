package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;

/** Integer block coordinate in the vessel grid. One block is one metre. */
public record GridPos(int x, int y, int z) implements Comparable<GridPos> {
    public GridPos offset(BlockFace face) {
        return new GridPos(x + face.dx(), y + face.dy(), z + face.dz());
    }

    public Vector3d centerMeters(GridPos origin) {
        return new Vector3d(
                x - origin.x + 0.5,
                y - origin.y + 0.5,
                z - origin.z + 0.5);
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
