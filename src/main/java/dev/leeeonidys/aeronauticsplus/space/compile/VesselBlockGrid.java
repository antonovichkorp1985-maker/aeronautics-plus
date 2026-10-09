package dev.leeeonidys.aeronauticsplus.space.compile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Occupied vessel blocks in world-grid coordinates. */
public record VesselBlockGrid(List<VesselBlockOccupant> occupants) {
    public static final int MAX_BLOCKS = 256;

    public VesselBlockGrid {
        occupants = List.copyOf(occupants == null ? List.of() : occupants);
        if (occupants.size() > MAX_BLOCKS) {
            throw new IllegalArgumentException("Vessel exceeds " + MAX_BLOCKS + " blocks");
        }
        Set<GridPos> seen = new HashSet<>();
        for (VesselBlockOccupant occupant : occupants) {
            if (!seen.add(occupant.pos())) {
                throw new IllegalArgumentException("Duplicate block at " + occupant.pos());
            }
        }
        occupants = occupants.stream().sorted(Comparator.comparing(VesselBlockOccupant::pos)).toList();
    }

    public static VesselBlockGrid of(VesselBlockOccupant... occupants) {
        return new VesselBlockGrid(List.of(occupants));
    }

    public boolean isEmpty() {
        return occupants.isEmpty();
    }

    public GridPos origin() {
        if (occupants.isEmpty()) {
            return new GridPos(0, 0, 0);
        }
        int minX = occupants.stream().mapToInt(occupant -> occupant.pos().x()).min().orElseThrow();
        int minY = occupants.stream().mapToInt(occupant -> occupant.pos().y()).min().orElseThrow();
        int minZ = occupants.stream().mapToInt(occupant -> occupant.pos().z()).min().orElseThrow();
        return new GridPos(minX, minY, minZ);
    }
}
