package dev.leeeonidys.aeronauticsplus.space.compile;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Occupied vessel objects in world-grid coordinates.
 * Several objects may share one cell when their occupancies fit.
 */
public record VesselBlockGrid(List<VesselBlockOccupant> occupants) {
    public static final int MAX_BLOCKS = 256;

    public VesselBlockGrid {
        occupants = List.copyOf(occupants == null ? List.of() : occupants);
        if (occupants.size() > MAX_BLOCKS) {
            throw new IllegalArgumentException("Vessel exceeds " + MAX_BLOCKS + " objects");
        }
        Set<String> ids = new HashSet<>();
        for (VesselBlockOccupant occupant : occupants) {
            if (!ids.add(occupant.componentId())) {
                throw new IllegalArgumentException("Duplicate component " + occupant.componentId());
            }
        }
        occupants = occupants.stream()
                .sorted(Comparator.comparing(VesselBlockOccupant::pos).thenComparing(VesselBlockOccupant::componentId))
                .toList();
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
