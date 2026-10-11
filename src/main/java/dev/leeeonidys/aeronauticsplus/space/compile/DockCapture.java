package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselConnection;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDiagnostic;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Two flying grids become one when capture faces look at each other.
 * Not a pad stack and not the Kepler map.
 */
public final class DockCapture {
    public static final double REACH_METERS = 4.0;
    public static final double MAX_CLOSING_METERS_PER_SECOND = 5.0;

    private DockCapture() {
    }

    public static String reject(
            VesselBlockGrid host,
            VesselBlockGrid guest,
            double distanceMeters,
            double closingMetersPerSecond) {
        if (host == null || guest == null) {
            throw new IllegalArgumentException("Both vehicles are required");
        }
        if (!Double.isFinite(distanceMeters) || distanceMeters < 0.0) {
            throw new IllegalArgumentException("Dock distance must be finite and non-negative");
        }
        if (!Double.isFinite(closingMetersPerSecond) || closingMetersPerSecond < 0.0) {
            throw new IllegalArgumentException("Closing speed must be finite and non-negative");
        }
        if (ports(host).isEmpty() || ports(guest).isEmpty()) {
            return "NO_PORT";
        }
        if (distanceMeters > REACH_METERS + 1.0e-9) {
            return "TOO_FAR";
        }
        if (closingMetersPerSecond > MAX_CLOSING_METERS_PER_SECOND + 1.0e-9) {
            return "CLOSING_TOO_FAST";
        }
        Fit fit = firstFit(host, guest);
        if (fit == null) {
            return anyOpposite(host, guest) ? "OVERLAP" : "NOT_FACING";
        }
        return "";
    }

    public static VesselBlockGrid merge(VesselBlockGrid host, VesselBlockGrid guest) {
        String reason = reject(host, guest, 0.0, 0.0);
        if (!reason.isEmpty()) {
            throw new IllegalStateException(reason);
        }
        Fit fit = firstFit(host, guest);
        if (fit == null) {
            throw new IllegalStateException("NOT_FACING");
        }
        return fit.merged();
    }

    /**
     * Cut the first capture pair. Host keeps the controller island when one side has a seat.
     */
    public static GridSplit split(VesselBlockGrid grid) {
        if (grid == null || grid.isEmpty()) {
            throw new IllegalArgumentException("A docked stack is required");
        }
        Pair pair = firstPair(grid);
        if (pair == null) {
            throw new IllegalStateException("NO_DOCK");
        }
        Set<GridPos> hostCells = flood(grid, pair.host().pos());
        if (hostCells.contains(pair.guest().pos())) {
            throw new IllegalStateException("NO_DOCK");
        }
        List<VesselBlockOccupant> host = new ArrayList<>();
        List<VesselBlockOccupant> guest = new ArrayList<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (hostCells.contains(occupant.pos())) {
                host.add(occupant);
            } else {
                guest.add(occupant);
            }
        }
        if (host.isEmpty() || guest.isEmpty()) {
            throw new IllegalStateException("NO_DOCK");
        }
        boolean hostSeat = hasController(host);
        boolean guestSeat = hasController(guest);
        if (guestSeat && !hostSeat) {
            return new GridSplit(new VesselBlockGrid(guest), new VesselBlockGrid(host));
        }
        return new GridSplit(new VesselBlockGrid(host), new VesselBlockGrid(guest));
    }

    private static Fit firstFit(VesselBlockGrid host, VesselBlockGrid guest) {
        Set<GridPos> hostCells = cells(host);
        for (VesselBlockOccupant portA : ports(host)) {
            GridPos capture = portA.pos().offset(portA.facing());
            for (VesselBlockOccupant portB : ports(guest)) {
                if (portB.facing() != portA.facing().opposite()) {
                    continue;
                }
                int dx = capture.x() - portB.pos().x();
                int dy = capture.y() - portB.pos().y();
                int dz = capture.z() - portB.pos().z();
                List<VesselBlockOccupant> occupants = new ArrayList<>(host.occupants());
                boolean overlap = false;
                for (VesselBlockOccupant part : guest.occupants()) {
                    GridPos at = part.pos().translate(dx, dy, dz);
                    if (hostCells.contains(at)) {
                        overlap = true;
                        break;
                    }
                    occupants.add(new VesselBlockOccupant(at, part.spec(), part.facing()));
                }
                if (overlap) {
                    continue;
                }
                VesselBlockGrid merged = new VesselBlockGrid(occupants);
                boolean packingFail = false;
                for (VesselDiagnostic diagnostic : CellPacking.diagnostics(merged)) {
                    if (diagnostic.severity() == VesselDiagnostic.Severity.ERROR) {
                        packingFail = true;
                        break;
                    }
                }
                if (packingFail) {
                    continue;
                }
                return new Fit(merged);
            }
        }
        return null;
    }

    private static boolean anyOpposite(VesselBlockGrid host, VesselBlockGrid guest) {
        for (VesselBlockOccupant portA : ports(host)) {
            for (VesselBlockOccupant portB : ports(guest)) {
                if (portB.facing() == portA.facing().opposite()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<VesselBlockOccupant> ports(VesselBlockGrid grid) {
        List<VesselBlockOccupant> ports = new ArrayList<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().kind() == VesselPartKind.DOCKING) {
                ports.add(occupant);
            }
        }
        return ports;
    }

    private static Set<GridPos> cells(VesselBlockGrid grid) {
        Set<GridPos> cells = new HashSet<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            cells.add(occupant.pos());
        }
        return cells;
    }

    private static Pair firstPair(VesselBlockGrid grid) {
        Map<GridPos, List<VesselBlockOccupant>> byPos = new HashMap<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            byPos.computeIfAbsent(occupant.pos(), ignored -> new ArrayList<>()).add(occupant);
        }
        for (VesselBlockOccupant portA : ports(grid)) {
            GridPos capture = portA.pos().offset(portA.facing());
            List<VesselBlockOccupant> there = byPos.get(capture);
            if (there == null) {
                continue;
            }
            for (VesselBlockOccupant portB : there) {
                if (portB.spec().kind() == VesselPartKind.DOCKING
                        && portB.facing() == portA.facing().opposite()) {
                    return new Pair(portA, portB);
                }
            }
        }
        return null;
    }

    private static Set<GridPos> flood(VesselBlockGrid grid, GridPos start) {
        Map<GridPos, List<VesselBlockOccupant>> byPos = new HashMap<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            byPos.computeIfAbsent(occupant.pos(), ignored -> new ArrayList<>()).add(occupant);
        }
        Set<GridPos> visited = new HashSet<>();
        ArrayDeque<GridPos> pending = new ArrayDeque<>();
        pending.add(start);
        visited.add(start);
        while (!pending.isEmpty()) {
            GridPos current = pending.remove();
            List<VesselBlockOccupant> here = byPos.get(current);
            if (here == null) {
                continue;
            }
            for (BlockFace face : BlockFace.values()) {
                GridPos next = current.offset(face);
                if (visited.contains(next) || !byPos.containsKey(next)) {
                    continue;
                }
                if (structurallyLinked(here, byPos.get(next), face)) {
                    visited.add(next);
                    pending.add(next);
                }
            }
        }
        return visited;
    }

    private static boolean structurallyLinked(
            List<VesselBlockOccupant> here, List<VesselBlockOccupant> neighbours, BlockFace face) {
        for (VesselBlockOccupant from : here) {
            for (VesselBlockOccupant to : neighbours) {
                if (from.spec().ports(face, from.facing()).contains(VesselConnection.ConnectionKind.STRUCTURAL)
                        && to.spec().ports(face.opposite(), to.facing())
                                .contains(VesselConnection.ConnectionKind.STRUCTURAL)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasController(List<VesselBlockOccupant> occupants) {
        for (VesselBlockOccupant occupant : occupants) {
            if (occupant.spec().kind() == VesselPartKind.CONTROLLER) {
                return true;
            }
        }
        return false;
    }

    private record Fit(VesselBlockGrid merged) {
    }

    private record Pair(VesselBlockOccupant host, VesselBlockOccupant guest) {
    }
}
