package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.CellOccupancy;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDiagnostic;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Packing of several objects into one 1 m world cell.
 * The cell is only storage; size and collision belong to each object.
 * Chisels & Bits can carve fine geometry but is not this container.
 */
public final class CellPacking {
    private CellPacking() {
    }

    public static List<VesselDiagnostic> diagnostics(VesselBlockGrid grid) {
        List<VesselDiagnostic> diagnostics = new ArrayList<>();
        if (grid == null || grid.isEmpty()) {
            return diagnostics;
        }
        Map<GridPos, List<VesselBlockOccupant>> byPos = new LinkedHashMap<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            byPos.computeIfAbsent(occupant.pos(), ignored -> new ArrayList<>()).add(occupant);
        }
        for (Map.Entry<GridPos, List<VesselBlockOccupant>> entry : byPos.entrySet()) {
            List<VesselBlockOccupant> cell = entry.getValue();
            double volume = 0.0;
            for (VesselBlockOccupant occupant : cell) {
                volume += occupant.spec().occupancy().volume();
            }
            if (volume > 1.0 + 1.0e-9) {
                diagnostics.add(new VesselDiagnostic(
                        VesselDiagnostic.Severity.ERROR,
                        "OVERFILL",
                        "Ячейка " + entry.getKey() + " переполнена: суммарный объём "
                                + String.format(java.util.Locale.ROOT, "%.3f", volume)
                                + " м³ > 1 м³"));
            }
            for (int i = 0; i < cell.size(); i++) {
                CellOccupancy a = cell.get(i).spec().occupancy();
                for (int j = i + 1; j < cell.size(); j++) {
                    if (a.intersects(cell.get(j).spec().occupancy())) {
                        diagnostics.add(new VesselDiagnostic(
                                VesselDiagnostic.Severity.ERROR,
                                "OVERLAP",
                                "В ячейке " + entry.getKey() + " пересекаются "
                                        + cell.get(i).componentId() + " и "
                                        + cell.get(j).componentId()));
                    }
                }
            }
        }
        return diagnostics;
    }
}
