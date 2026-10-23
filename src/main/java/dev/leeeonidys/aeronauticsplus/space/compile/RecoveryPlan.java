package dev.leeeonidys.aeronauticsplus.space.compile;

import java.util.ArrayList;
import java.util.List;

/**
 * Block layout of a recovered booster sitting on a pad cell at (0,0,0).
 * ChemMod tanks stay in the plan; AP only places registered {@code VesselPartBlock}s.
 */
public record RecoveryPlan(List<Placement> placements) {
    public record Placement(GridPos relativeToPad, VesselPartSpec spec, BlockFace facing) {
        public Placement {
            if (relativeToPad == null || spec == null || facing == null) {
                throw new IllegalArgumentException("Recovery placement is incomplete");
            }
            if (spec.kind() == VesselPartKind.PAD) {
                throw new IllegalArgumentException("Launch pad does not fly and does not recover");
            }
        }
    }

    public RecoveryPlan {
        placements = List.copyOf(placements == null ? List.of() : placements);
    }

    /** Sit the booster on top of the pad: lowest part at y=1. */
    public static RecoveryPlan sitOnPad(VesselBlockGrid booster) {
        if (booster == null || booster.isEmpty()) {
            throw new IllegalArgumentException("Recovered booster grid is required");
        }
        GridPos origin = booster.origin();
        List<Placement> list = new ArrayList<>();
        for (VesselBlockOccupant occupant : booster.occupants()) {
            if (occupant.spec().kind() == VesselPartKind.PAD) {
                continue;
            }
            GridPos relative = new GridPos(
                    occupant.pos().x() - origin.x(),
                    occupant.pos().y() - origin.y() + 1,
                    occupant.pos().z() - origin.z());
            list.add(new Placement(relative, occupant.spec(), occupant.facing()));
        }
        if (list.isEmpty()) {
            throw new IllegalStateException("Booster has no flying parts to put back");
        }
        return new RecoveryPlan(list);
    }

    public boolean includes(String specId) {
        return placements.stream().anyMatch(placement -> placement.spec().id().equals(specId));
    }

    public boolean includesKind(VesselPartKind kind) {
        return placements.stream().anyMatch(placement -> placement.spec().kind() == kind);
    }
}
