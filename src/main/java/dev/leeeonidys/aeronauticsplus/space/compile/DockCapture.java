package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselDiagnostic;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    private record Fit(VesselBlockGrid merged) {
    }
}
