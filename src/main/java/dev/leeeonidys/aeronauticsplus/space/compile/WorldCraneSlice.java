package dev.leeeonidys.aeronauticsplus.space.compile;

/**
 * Crane is a capability: tower, carriage, mobile. No AP crane block.
 */
public final class WorldCraneSlice {
    private WorldCraneSlice() {
    }

    public record Result(
            double liftSeconds,
            boolean midEmpty,
            boolean tooFar,
            boolean busy,
            boolean placed,
            WorldCrane.Kind tower,
            WorldCrane.Kind carriage,
            WorldCrane.Kind mobile) {
    }

    public static Result execute() {
        if (WorldCrane.kindOf("create:rope_pulley") != WorldCrane.Kind.TOWER
                || WorldCrane.kindOf("create:elevator_pulley") != WorldCrane.Kind.TOWER) {
            throw new IllegalStateException("Pulley / elevator is a standing tower crane");
        }
        if (WorldCrane.kindOf("create:gantry_carriage") != WorldCrane.Kind.CARRIAGE
                || WorldCrane.kindOf("create:mechanical_piston") != WorldCrane.Kind.CARRIAGE) {
            throw new IllegalStateException("Gantry and piston are factory carriages");
        }
        if (WorldCrane.kindOf("pack:mobile_crane") != WorldCrane.Kind.MOBILE) {
            throw new IllegalStateException("A crane on a vehicle is MOBILE, not an AP block");
        }
        GridPos crane = new GridPos(0, 0, 0);
        GridPos from = new GridPos(10, 0, 0);
        GridPos to = new GridPos(0, 1, 0);
        if (!WorldCrane.inReach(crane, from) || !WorldCrane.inReach(crane, to)) {
            throw new IllegalStateException("A 10 m pad lift must be inside crane reach");
        }
        String ok = WorldCrane.rejectLift(crane, from, to, false, false);
        if (!ok.isEmpty()) {
            throw new IllegalStateException("Valid lift must not reject: " + ok);
        }
        if (!"TOO_FAR".equals(WorldCrane.rejectLift(crane, new GridPos(20, 0, 0), to, false, false))) {
            throw new IllegalStateException("20 m is past crane reach");
        }
        if (!"CRANE_BUSY".equals(WorldCrane.rejectLift(crane, from, to, true, false))) {
            throw new IllegalStateException("A second lift must be CRANE_BUSY");
        }
        if (!"SAME_CELL".equals(WorldCrane.rejectLift(crane, from, from, false, false))) {
            throw new IllegalStateException("Crane must not spawn onto the same cell");
        }
        if (!"OVERLAP".equals(WorldCrane.rejectLift(crane, from, to, false, true))) {
            throw new IllegalStateException("Occupied dest must be OVERLAP");
        }
        VesselBlockOccupant engine = new VesselBlockOccupant(from, VesselPartCatalog.ENGINE, BlockFace.DOWN);
        CraneJob job = CraneJob.start(engine, to);
        if (!(job.durationSeconds() > 1.0)) {
            throw new IllegalStateException("A ten-metre lift must take real time, got " + job.durationSeconds());
        }
        CraneJob mid = job.advance(job.durationSeconds() * 0.5);
        if (mid.complete()) {
            throw new IllegalStateException("Mid-lift must still be on the hook");
        }
        CraneJob done = mid.advance(job.durationSeconds());
        if (!done.complete() || !done.placed().pos().equals(to)) {
            throw new IllegalStateException("Part appears at dest only after the lift ends");
        }
        return new Result(
                job.durationSeconds(),
                !mid.complete(),
                true,
                true,
                done.complete(),
                WorldCrane.kindOf("create:rope_pulley"),
                WorldCrane.kindOf("create:gantry_carriage"),
                WorldCrane.kindOf("pack:mobile_crane"));
    }
}
