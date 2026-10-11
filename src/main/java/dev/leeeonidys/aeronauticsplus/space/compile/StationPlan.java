package dev.leeeonidys.aeronauticsplus.space.compile;

import java.util.ArrayList;
import java.util.List;

/**
 * Block layout of a parked station. Same grid as flight, not sat on a pad,
 * not Overworld Y. Habitat is the walkable core (Aftermath / ISS).
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
        boolean habitat = false;
        for (VesselBlockOccupant occupant : flying.occupants()) {
            if (occupant.spec().kind() == VesselPartKind.PAD) {
                throw new IllegalStateException("Station must not include the launch pad");
            }
            if (occupant.spec().kind() == VesselPartKind.HABITAT) {
                habitat = true;
            }
            list.add(new Placement(occupant.pos(), occupant.spec(), occupant.facing()));
        }
        if (!habitat) {
            throw new IllegalStateException("Station needs a habitat to walk");
        }
        return new StationPlan(list);
    }

    public boolean includesKind(VesselPartKind kind) {
        return placements.stream().anyMatch(placement -> placement.spec().kind() == kind);
    }
}
