package dev.leeeonidys.aeronauticsplus.space.compile;

import java.util.ArrayList;
import java.util.List;

/**
 * Duration of a Create-train haul: the assembled plant is on a rocket mount
 * and is not at the pad until the train arrives. This is not a homemade
 * crawler vehicle.
 */
public record CrawlerJob(
        VesselBlockGrid cargo,
        GridPos from,
        GridPos to,
        double durationSeconds,
        double elapsedSeconds) {
    /** Crawler speed. Slower than the crane; a 20 m haul is tens of seconds. */
    public static final double METERS_PER_SECOND = 0.5;

    public CrawlerJob {
        if (cargo == null || cargo.isEmpty() || from == null || to == null) {
            throw new IllegalArgumentException("Crawler job is incomplete");
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Crawler cannot haul a plant onto its own cell");
        }
        if (!(durationSeconds > 0.0) || !Double.isFinite(durationSeconds)) {
            throw new IllegalArgumentException("Crawler duration must be finite and positive");
        }
        if (elapsedSeconds < 0.0 || !Double.isFinite(elapsedSeconds)) {
            throw new IllegalArgumentException("Crawler elapsed time must be finite and non-negative");
        }
    }

    public static CrawlerJob start(VesselBlockGrid cargo, GridPos destination) {
        if (cargo == null || cargo.isEmpty() || destination == null) {
            throw new IllegalArgumentException("Crawler start requires cargo and a pad origin");
        }
        GridPos from = cargo.origin();
        return new CrawlerJob(cargo, from, destination, durationSeconds(from, destination), 0.0);
    }

    public static double durationSeconds(GridPos from, GridPos to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Crawler path requires both ends");
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

    public CrawlerJob advance(double dt) {
        if (!(dt >= 0.0) || !Double.isFinite(dt)) {
            throw new IllegalArgumentException("Crawler step must be finite and non-negative");
        }
        if (complete()) {
            return this;
        }
        return new CrawlerJob(cargo, from, to, durationSeconds, elapsedSeconds + dt);
    }

    public VesselBlockGrid placed() {
        int dx = to.x() - from.x();
        int dy = to.y() - from.y();
        int dz = to.z() - from.z();
        List<VesselBlockOccupant> moved = new ArrayList<>();
        for (VesselBlockOccupant occupant : cargo.occupants()) {
            moved.add(new VesselBlockOccupant(
                    occupant.pos().translate(dx, dy, dz), occupant.spec(), occupant.facing()));
        }
        return new VesselBlockGrid(moved);
    }
}
