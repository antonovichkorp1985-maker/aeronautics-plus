package dev.leeeonidys.aeronauticsplus.space.compile;

import java.util.ArrayList;
import java.util.List;

/**
 * Create-train bay: {@link VesselPartKind#MOUNT} cells in one horizontal slice.
 * Not a 1-block cage and not an AP tread crawler.
 */
public final class TrainMount {
    private TrainMount() {
    }

    public static List<VesselBlockOccupant> mounts(VesselBlockGrid grid) {
        List<VesselBlockOccupant> mounts = new ArrayList<>();
        if (grid == null) {
            return mounts;
        }
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().kind() == VesselPartKind.MOUNT) {
                mounts.add(occupant);
            }
        }
        return mounts;
    }

    /** All mount cells share Y. A vertical stack of mounts would be a cage. */
    public static boolean isHorizontalBay(VesselBlockGrid grid) {
        List<VesselBlockOccupant> mounts = mounts(grid);
        if (mounts.isEmpty()) {
            return false;
        }
        int y = mounts.get(0).pos().y();
        int minX = mounts.get(0).pos().x();
        int maxX = minX;
        int minZ = mounts.get(0).pos().z();
        int maxZ = minZ;
        for (VesselBlockOccupant mount : mounts) {
            if (mount.pos().y() != y) {
                return false;
            }
            minX = Math.min(minX, mount.pos().x());
            maxX = Math.max(maxX, mount.pos().x());
            minZ = Math.min(minZ, mount.pos().z());
            maxZ = Math.max(maxZ, mount.pos().z());
        }
        int spanX = maxX - minX;
        int spanZ = maxZ - minZ;
        return mounts.size() == 1 || spanX >= 1 || spanZ >= 1;
    }

    public static VesselBlockGrid unclamp(VesselBlockGrid grid) {
        if (grid == null || grid.isEmpty()) {
            return new VesselBlockGrid(List.of());
        }
        List<VesselBlockOccupant> remaining = new ArrayList<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().kind() != VesselPartKind.MOUNT) {
                remaining.add(occupant);
            }
        }
        return new VesselBlockGrid(remaining);
    }

    public static boolean hasCargo(VesselBlockGrid grid) {
        if (grid == null) {
            return false;
        }
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().kind() != VesselPartKind.MOUNT && occupant.spec().kind() != VesselPartKind.PAD) {
                return true;
            }
        }
        return false;
    }
}
