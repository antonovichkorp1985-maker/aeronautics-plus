package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselConnection;

/**
 * Entity↔entity capture. Ports look at each other. Not Create, not a crane.
 */
public final class DockCaptureSlice {
    private DockCaptureSlice() {
    }

    public record Result(
            boolean captured,
            int dockingLinks,
            boolean tooFar,
            boolean noPort,
            boolean notFacing,
            boolean closing,
            boolean overlap) {
    }

    public static Result execute() {
        VesselBlockGrid host = flyer(BlockFace.NORTH);
        VesselBlockGrid guest = flyer(BlockFace.SOUTH);
        if (!DockCapture.reject(host, guest, 2.0, 0.0).isEmpty()) {
            throw new IllegalStateException("Facing ports at 2 m must capture");
        }
        VesselBlockGrid merged = DockCapture.merge(host, guest);
        int links = 0;
        for (VesselConnection connection : VesselBlockCompiler.compile(merged).connections()) {
            if (connection.kind() == VesselConnection.ConnectionKind.DOCKING) {
                links++;
            }
        }
        if (links != 1) {
            throw new IllegalStateException("Captured stack must share one docking link, got " + links);
        }
        if (merged.occupants().size() != host.occupants().size() + guest.occupants().size()) {
            throw new IllegalStateException("Both airframes stay after capture");
        }
        if (!"TOO_FAR".equals(DockCapture.reject(host, guest, 10.0, 0.0))) {
            throw new IllegalStateException("10 m is past capture reach");
        }
        if (!"NO_PORT".equals(DockCapture.reject(host, noPort(), 1.0, 0.0))) {
            throw new IllegalStateException("A stack without a ring must be NO_PORT");
        }
        if (!"NOT_FACING".equals(DockCapture.reject(host, flyer(BlockFace.NORTH), 1.0, 0.0))) {
            throw new IllegalStateException("Ports looking the same way must be NOT_FACING");
        }
        if (!"CLOSING_TOO_FAST".equals(DockCapture.reject(host, guest, 1.0, 20.0))) {
            throw new IllegalStateException("20 m/s closing must be CLOSING_TOO_FAST");
        }
        if (!"OVERLAP".equals(DockCapture.reject(blockedHost(), guest, 1.0, 0.0))) {
            throw new IllegalStateException("A body in the capture cell must be OVERLAP");
        }
        GridSplit split = DockCapture.split(merged);
        if (split.continuing().occupants().size() != host.occupants().size()
                || split.booster().occupants().size() != guest.occupants().size()) {
            throw new IllegalStateException("Undock must return both airframes");
        }
        try {
            DockCapture.split(host);
            throw new IllegalStateException("A lone stack must be NO_DOCK");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains("NO_DOCK")) {
                throw exception;
            }
        }
        return new Result(true, links, true, true, true, true, true);
    }

    private static VesselBlockGrid flyer(BlockFace portFace) {
        GridPos port = new GridPos(0, 3, 0).offset(portFace);
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP),
                new VesselBlockOccupant(port, VesselPartCatalog.DOCKING, portFace));
    }

    private static VesselBlockGrid noPort() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }

    /** Opposite port, but a hull already sits in the capture cell. */
    private static VesselBlockGrid blockedHost() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, -1), VesselPartCatalog.DOCKING, BlockFace.NORTH),
                new VesselBlockOccupant(new GridPos(0, 3, -2), VesselPartCatalog.STRUCTURE, BlockFace.UP));
    }
}
