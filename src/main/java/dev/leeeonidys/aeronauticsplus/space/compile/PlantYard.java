package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDiagnostic;
import java.util.ArrayList;
import java.util.List;

/**
 * Staging yard and planted structure for a large assembled machine.
 * The plant is a graph of cells, not one decorative block. The crane is the
 * only way a staged part becomes planted; there is no spawn.
 */
public record PlantYard(VesselBlockGrid staged, VesselBlockGrid planted, CraneJob job) {
    public PlantYard {
        staged = staged == null ? new VesselBlockGrid(List.of()) : staged;
        planted = planted == null ? new VesselBlockGrid(List.of()) : planted;
    }

    public static PlantYard ofStaged(VesselBlockOccupant... occupants) {
        return new PlantYard(VesselBlockGrid.of(occupants), new VesselBlockGrid(List.of()), null);
    }

    public boolean craneBusy() {
        return job != null && !job.complete();
    }

    public CraneOutcome startInstall(String stagedComponentId, GridPos destination) {
        if (stagedComponentId == null || stagedComponentId.isBlank() || destination == null) {
            throw new IllegalArgumentException("Install requires a staged part id and a destination");
        }
        if (craneBusy()) {
            return CraneOutcome.rejected(this, List.of(new VesselDiagnostic(
                    VesselDiagnostic.Severity.ERROR,
                    "CRANE_BUSY",
                    "Кран занят: идёт установка " + job.spec().id() + " на " + job.to())));
        }
        VesselBlockOccupant occupant = find(staged, stagedComponentId);
        if (occupant == null) {
            return CraneOutcome.rejected(this, List.of(new VesselDiagnostic(
                    VesselDiagnostic.Severity.ERROR,
                    "MISSING_PART",
                    "На площадке нет детали " + stagedComponentId)));
        }
        if (occupant.pos().equals(destination)) {
            return CraneOutcome.rejected(this, List.of(new VesselDiagnostic(
                    VesselDiagnostic.Severity.ERROR,
                    "SAME_CELL",
                    "Кран не спавнит деталь на месте: " + destination)));
        }
        VesselBlockOccupant placed = new VesselBlockOccupant(destination, occupant.spec(), occupant.facing());
        List<VesselBlockOccupant> proposed = new ArrayList<>(planted.occupants());
        proposed.add(placed);
        List<VesselDiagnostic> packing = CellPacking.diagnostics(new VesselBlockGrid(proposed));
        if (!packing.isEmpty()) {
            return CraneOutcome.rejected(this, packing);
        }
        List<VesselBlockOccupant> remaining = new ArrayList<>();
        for (VesselBlockOccupant candidate : staged.occupants()) {
            if (!candidate.componentId().equals(stagedComponentId)) {
                remaining.add(candidate);
            }
        }
        PlantYard next = new PlantYard(
                new VesselBlockGrid(remaining),
                planted,
                CraneJob.start(occupant, destination));
        return CraneOutcome.accepted(next);
    }

    public PlantYard advance(double dt) {
        if (job == null) {
            return this;
        }
        CraneJob next = job.advance(dt);
        if (!next.complete()) {
            return new PlantYard(staged, planted, next);
        }
        List<VesselBlockOccupant> occupants = new ArrayList<>(planted.occupants());
        occupants.add(next.placed());
        return new PlantYard(staged, new VesselBlockGrid(occupants), null);
    }

    public VesselCompilation compilePlanted() {
        return VesselBlockCompiler.analyze(planted);
    }

    private static VesselBlockOccupant find(VesselBlockGrid grid, String componentId) {
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.componentId().equals(componentId)) {
                return occupant;
            }
        }
        return null;
    }

    public record CraneOutcome(PlantYard yard, List<VesselDiagnostic> diagnostics) {
        public CraneOutcome {
            if (yard == null) {
                throw new IllegalArgumentException("Crane outcome requires a yard");
            }
            diagnostics = List.copyOf(diagnostics == null ? List.of() : diagnostics);
        }

        public static CraneOutcome accepted(PlantYard yard) {
            return new CraneOutcome(yard, List.of());
        }

        public static CraneOutcome rejected(PlantYard yard, List<VesselDiagnostic> diagnostics) {
            return new CraneOutcome(yard, diagnostics);
        }

        public boolean accepted() {
            return diagnostics.stream().noneMatch(
                    diagnostic -> diagnostic.severity() == VesselDiagnostic.Severity.ERROR);
        }

        public String diagnosticText() {
            if (diagnostics.isEmpty()) {
                return "";
            }
            StringBuilder text = new StringBuilder();
            for (VesselDiagnostic diagnostic : diagnostics) {
                if (!text.isEmpty()) {
                    text.append('\n');
                }
                text.append(diagnostic.severity())
                        .append(' ')
                        .append(diagnostic.code())
                        .append(": ")
                        .append(diagnostic.message());
            }
            return text.toString();
        }
    }
}
