package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.DockCapture;
import dev.leeeonidys.aeronauticsplus.space.compile.DockCaptureSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;

/**
 * Two world entities dock. Pad blocks and the Kepler map are not this capture.
 */
public final class DockCaptureLoopSlice {
    private DockCaptureLoopSlice() {
    }

    public record Result(
            boolean compiler,
            boolean entities,
            int parts,
            boolean stillWorld) {
    }

    public static Result execute() {
        DockCaptureSlice.Result compiler = DockCaptureSlice.execute();
        if (!compiler.captured() || compiler.dockingLinks() != 1) {
            throw new IllegalStateException("Compiler capture must join one docking link");
        }

        FlightLoop host = FlightLoop.sit(padFlyer(BlockFace.NORTH)).liftoff();
        FlightLoop guest = FlightLoop.sit(padFlyer(BlockFace.SOUTH)).liftoff();
        if (host.presence() != FlightPresence.WORLD_ENTITY
                || guest.presence() != FlightPresence.WORLD_ENTITY) {
            throw new IllegalStateException("Capture starts as two world entities");
        }
        FlightLoop joined = host.dock(guest, 2.0);
        if (joined.presence() != FlightPresence.WORLD_ENTITY) {
            throw new IllegalStateException("The joined stack stays the world entity");
        }
        int parts = 0;
        boolean hostPort = false;
        boolean guestPort = false;
        for (VesselBlockOccupant occupant : joined.flying().occupants()) {
            parts++;
            if (occupant.spec().kind() == VesselPartKind.DOCKING) {
                if (occupant.facing() == BlockFace.NORTH) {
                    hostPort = true;
                }
                if (occupant.facing() == BlockFace.SOUTH) {
                    guestPort = true;
                }
            }
        }
        if (parts != host.flying().occupants().size() + guest.flying().occupants().size()) {
            throw new IllegalStateException("Both airframes stay on the joined entity");
        }
        if (!hostPort || !guestPort) {
            throw new IllegalStateException("Both capture rings stay");
        }
        try {
            host.dock(guest, DockCapture.REACH_METERS + 6.0);
            throw new IllegalStateException("Past reach must not dock");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains("TOO_FAR")) {
                throw exception;
            }
        }
        return new Result(true, true, parts, true);
    }

    private static VesselBlockGrid padFlyer(BlockFace portFace) {
        GridPos port = new GridPos(0, 3, 0).offset(portFace);
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP),
                new VesselBlockOccupant(port, VesselPartCatalog.DOCKING, portFace));
    }
}
