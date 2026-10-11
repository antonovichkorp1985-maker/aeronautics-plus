package dev.leeeonidys.aeronauticsplus.space.compile;

/**
 * World gantry: timed lift, not a spawn. Reach is metres from the crane cell.
 * Not ChemMod, not a GUI, not an AP tread.
 */
public final class WorldCrane {
    public static final int REACH_METERS = 16;

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
}
