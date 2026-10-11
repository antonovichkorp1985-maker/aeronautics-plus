package dev.leeeonidys.aeronauticsplus.space.compile;

import java.util.ArrayList;
import java.util.List;

/**
 * Block layout of a parked station. Same grid as flight, not sat on a pad,
 * not Overworld Y. The control seat is what marks players aboard.
 */
public record StationPlan(List<Placement> placements) {
    public record Placement(GridPos pos, VesselPartSpec spec, BlockFace facing) {
        public Placement {
            if (pos == null || spec == null || facing == null) {
                throw new IllegalArgumentException("Station placement is incomplete");
            }
            if (spec.kind() == VesselPartKind.PAD) {
                throw new IllegalArgumentException("Launch pad does not fly and is not a station");
            }
        }
    }

    public StationPlan {
        placements = List.copyOf(placements == null ? List.of() : placements);
    }

    public static StationPlan of(VesselBlockGrid flying) {
        if (flying == null || flying.isEmpty()) {
            throw new IllegalArgumentException("Station grid is required");
        }
        List<Placement> list = new ArrayList<>();
        boolean seat = false;
        for (VesselBlockOccupant occupant : flying.occupants()) {
            if (occupant.spec().kind() == VesselPartKind.PAD) {
                throw new IllegalStateException("Station must not include the launch pad");
            }
            if (occupant.spec().kind() == VesselPartKind.CONTROLLER) {
                seat = true;
            }
            list.add(new Placement(occupant.pos(), occupant.spec(), occupant.facing()));
        }
        if (!seat) {
            throw new IllegalStateException("Station needs a control seat");
        }
        return new StationPlan(list);
    }

    public boolean includesKind(VesselPartKind kind) {
        return placements.stream().anyMatch(placement -> placement.spec().kind() == kind);
    }

    public GridPos origin() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        for (Placement placement : placements) {
            minX = Math.min(minX, placement.pos().x());
            minY = Math.min(minY, placement.pos().y());
            minZ = Math.min(minZ, placement.pos().z());
        }
        return new GridPos(minX, minY, minZ);
    }

    /** Stand on the control seat relative to {@link #origin()}. */
    public GridPos crewStand() {
        GridPos origin = origin();
        for (Placement placement : placements) {
            if (placement.spec().kind() == VesselPartKind.CONTROLLER) {
                return new GridPos(
                        placement.pos().x() - origin.x(),
                        placement.pos().y() - origin.y() + 1,
                        placement.pos().z() - origin.z());
            }
        }
        throw new IllegalStateException("Station needs a control seat");
    }
}
