package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;

/**
 * Create train plus {@code rocket_mount} is the transporter. No AP treads.
 */
public final class TrainMountSlice {
    private TrainMountSlice() {
    }

    public record Result(
            boolean horizontalBay,
            boolean onTransporter,
            boolean launchableAfterUnclamp,
            double haulSeconds,
            int mountCells,
            boolean noTreads) {
    }

    public static Result execute() {
        for (VesselPartKind kind : VesselPartKind.values()) {
            if (kind.name().contains("TREAD") || kind.name().contains("TRACK")) {
                throw new IllegalStateException("AP must not register treads or tracks, found " + kind);
            }
        }
        VesselBlockGrid carriage = bay();
        if (!TrainMount.isHorizontalBay(carriage)) {
            throw new IllegalStateException("Three mount cells on one Y must be a horizontal bay");
        }
        VesselBlockGrid cage = verticalCage();
        if (TrainMount.isHorizontalBay(cage)) {
            throw new IllegalStateException("Stacked mounts are a cage, not a train bay");
        }
        VesselCompilation riding = VesselBlockCompiler.analyze(carriage);
        if (riding.isLaunchable() || !riding.diagnosticText().contains("ON_TRANSPORTER")) {
            throw new IllegalStateException("Stack on the train mount must not launch:\n" + riding.diagnosticText());
        }
        if (TrainMount.mounts(carriage).size() != 3) {
            throw new IllegalStateException("Bay must be three 1 m mount cells, not a cage");
        }
        VesselBlockGrid released = TrainMount.unclamp(carriage);
        if (!TrainMount.mounts(released).isEmpty()) {
            throw new IllegalStateException("Unclamp must drop every mount cell");
        }
        VesselCompilation free = VesselBlockCompiler.analyze(released);
        if (!free.isLaunchable() || free.diagnosticText().contains("ON_TRANSPORTER")) {
            throw new IllegalStateException("After unclamp the rocket must be launchable:\n" + free.diagnosticText());
        }
        double haul = CrawlerJob.durationSeconds(new GridPos(0, 0, 0), new GridPos(20, 0, 0));
        if (!(haul > 1.0) || Math.abs(haul - 20.0 / CrawlerJob.METERS_PER_SECOND) > 1.0e-9) {
            throw new IllegalStateException("A 20 m train haul must take real time, got " + haul);
        }
        VesselEnvelope envelope = VesselEnvelope.of(carriage);
        if (!(envelope.diameter() >= 2.5)) {
            throw new IllegalStateException("A 3-cell bay must be at least 2.5 m across, got " + envelope.diameter());
        }
        return new Result(
                true,
                riding.diagnosticText().contains("ON_TRANSPORTER"),
                free.isLaunchable(),
                haul,
                TrainMount.mounts(carriage).size(),
                true);
    }

    private static VesselBlockGrid bay() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(10, 0, 0), VesselPartCatalog.MOUNT, BlockFace.NORTH),
                new VesselBlockOccupant(new GridPos(11, 0, 0), VesselPartCatalog.MOUNT, BlockFace.NORTH),
                new VesselBlockOccupant(new GridPos(12, 0, 0), VesselPartCatalog.MOUNT, BlockFace.NORTH),
                new VesselBlockOccupant(new GridPos(10, 1, 0), VesselPartCatalog.STRUCTURE, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(11, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(12, 1, 0), VesselPartCatalog.STRUCTURE, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(10, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(11, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(12, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(11, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }

    private static VesselBlockGrid verticalCage() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.MOUNT, BlockFace.NORTH),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.MOUNT, BlockFace.NORTH),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.MOUNT, BlockFace.NORTH));
    }
}
