package dev.leeeonidys.aeronauticsplus.space.compile;

/**
 * Crane is a capability, not an AP block. Tower, carriage and mobile cranes
 * already exist (Create gantry/pulley/piston, pack machines). AP only times the lift.
 */
public final class WorldCrane {
    public static final int REACH_METERS = 16;

    public enum Kind {
        /** Standing / rope / elevator. */
        TOWER,
        /** Gantry on rails, factory carriage, piston. */
        CARRIAGE,
        /** Crane on a vehicle or train. */
        MOBILE
    }

    private WorldCrane() {
    }

    public static boolean inReach(GridPos crane, GridPos cell) {
        if (crane == null || cell == null) {
            throw new IllegalArgumentException("Crane and cell are required");
        }
        double dx = crane.x() - cell.x();
        double dy = crane.y() - cell.y();
        double dz = crane.z() - cell.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz) <= REACH_METERS + 1.0e-9;
    }

    public static String rejectLift(GridPos crane, GridPos from, GridPos to, boolean busy, boolean destOccupied) {
        if (crane == null || from == null || to == null) {
            throw new IllegalArgumentException("Crane lift requires crane, from and to");
        }
        if (busy) {
            return "CRANE_BUSY";
        }
        if (from.equals(to)) {
            return "SAME_CELL";
        }
        if (!inReach(crane, from) || !inReach(crane, to)) {
            return "TOO_FAR";
        }
        if (destOccupied) {
            return "OVERLAP";
        }
        return "";
    }

    /**
     * Classify a block id. AP does not register a crane block; this only names
     * the machine the pack or Create already placed.
     */
    public static Kind kindOf(String blockId) {
        if (blockId == null || blockId.isBlank()) {
            throw new IllegalArgumentException("Crane block id is required");
        }
        String id = blockId.toLowerCase(java.util.Locale.ROOT);
        if (id.contains("mobile") || id.contains("truck") || id.contains("crawler")
                || id.contains("bogey") || id.contains("train")) {
            return Kind.MOBILE;
        }
        if (id.contains("gantry") || id.contains("piston") || id.contains("carriage")) {
            return Kind.CARRIAGE;
        }
        return Kind.TOWER;
    }

    public static String kindLabel(Kind kind) {
        return switch (kind) {
            case TOWER -> "tower";
            case CARRIAGE -> "carriage";
            case MOBILE -> "mobile";
        };
    }
}
