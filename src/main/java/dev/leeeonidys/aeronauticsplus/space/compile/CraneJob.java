package dev.leeeonidys.aeronauticsplus.space.compile;

/**
 * One crane lift. The part leaves the yard when the job starts and appears at
 * the destination only when the job finishes. This is an action, not a spawn.
 */
public record CraneJob(
        VesselPartSpec spec,
        BlockFace facing,
        GridPos from,
        GridPos to,
        double durationSeconds,
        double elapsedSeconds) {
    /** Crane trolley speed. Adjacent cells take one second; nothing is instant. */
    public static final double METERS_PER_SECOND = 1.0;

    public CraneJob {
        if (spec == null || facing == null || from == null || to == null) {
            throw new IllegalArgumentException("Crane job is incomplete");
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Crane cannot install a part onto its own cell");
        }
        if (!(durationSeconds > 0.0) || !Double.isFinite(durationSeconds)) {
            throw new IllegalArgumentException("Crane duration must be finite and positive");
        }
        if (elapsedSeconds < 0.0 || !Double.isFinite(elapsedSeconds)) {
            throw new IllegalArgumentException("Crane elapsed time must be finite and non-negative");
        }
    }

    public static CraneJob start(VesselBlockOccupant occupant, GridPos destination) {
        if (occupant == null || destination == null) {
            throw new IllegalArgumentException("Crane start requires an occupant and a destination");
        }
        return new CraneJob(
                occupant.spec(),
                occupant.facing(),
                occupant.pos(),
                destination,
                durationSeconds(occupant.pos(), destination),
                0.0);
    }

    public static double durationSeconds(GridPos from, GridPos to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Crane path requires both ends");
        }
        double dx = to.x() - from.x();
        double dy = to.y() - from.y();
        double dz = to.z() - from.z();
        double meters = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return meters / METERS_PER_SECOND;
    }

    public boolean complete() {
        return elapsedSeconds >= durationSeconds;
    }

    public CraneJob advance(double dt) {
        if (!(dt >= 0.0) || !Double.isFinite(dt)) {
            throw new IllegalArgumentException("Crane step must be finite and non-negative");
        }
        if (complete()) {
            return this;
        }
        return new CraneJob(spec, facing, from, to, durationSeconds, elapsedSeconds + dt);
    }

    public VesselBlockOccupant placed() {
        return new VesselBlockOccupant(to, spec, facing);
    }
}
