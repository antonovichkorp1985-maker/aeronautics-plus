package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;

/**
 * Deterministic proof that a large plant is an assembled structure and that
 * crane install is a timed action, not a spawn animation.
 */
public final class AssemblySlice {
    private AssemblySlice() {
    }

    public record Result(
            int plantedComponents,
            double craneSeconds,
            boolean midTransitEmpty,
            boolean launchable,
            String busy,
            String overlap) {
    }

    public static Result execute() {
        VesselBlockOccupant engine = new VesselBlockOccupant(
                new GridPos(10, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN);
        VesselBlockOccupant tank = new VesselBlockOccupant(
                new GridPos(10, 1, 0), VesselPartCatalog.TANK, BlockFace.UP);
        VesselBlockOccupant frame = new VesselBlockOccupant(
                new GridPos(10, 2, 0), VesselPartCatalog.STRUCTURE, BlockFace.UP);
        PlantYard yard = PlantYard.ofStaged(engine, tank, frame);

        PlantYard.CraneOutcome startEngine = yard.startInstall(engine.componentId(), new GridPos(0, 0, 0));
        if (!startEngine.accepted()) {
            throw new IllegalStateException("Engine lift must start:\\n" + startEngine.diagnosticText());
        }
        yard = startEngine.yard();
        double craneSeconds = yard.job().durationSeconds();
        if (!(craneSeconds > 1.0)) {
            throw new IllegalStateException("A ten-metre crane lift must take real time, got " + craneSeconds);
        }
        boolean engineStillStaged = yard.staged().occupants().stream()
                .anyMatch(part -> part.spec().kind() == VesselPartKind.ENGINE);
        if (engineStillStaged || !yard.planted().isEmpty() || !yard.craneBusy()) {
            throw new IllegalStateException("After start the engine is on the crane, not spawned and not still staged");
        }

        PlantYard mid = yard.advance(craneSeconds * 0.5);
        boolean midTransitEmpty = mid.planted().isEmpty() && mid.craneBusy();
        if (!midTransitEmpty) {
            throw new IllegalStateException("Mid-lift destination must stay empty: crane is not a spawn animation");
        }
        PlantYard.CraneOutcome busy = mid.startInstall(tank.componentId(), new GridPos(0, 1, 0));
        if (busy.accepted() || !busy.diagnosticText().contains("CRANE_BUSY")) {
            throw new IllegalStateException("A second lift while the crane is moving must be CRANE_BUSY:\\n"
                    + busy.diagnosticText());
        }

        yard = mid.advance(craneSeconds);
        if (yard.craneBusy() || yard.planted().occupants().size() != 1) {
            throw new IllegalStateException("Engine must appear at the pad only after the lift finishes");
        }

        yard = complete(yard, tank.componentId(), new GridPos(0, 1, 0));
        yard = complete(yard, frame.componentId(), new GridPos(0, 2, 0));
        if (yard.planted().occupants().size() != 3 || !yard.staged().isEmpty() || yard.craneBusy()) {
            throw new IllegalStateException("Assembled plant must occupy three cells, not one decorative block");
        }
        VesselCompilation planted = yard.compilePlanted();
        if (!planted.isLaunchable() || planted.stages().size() != 1) {
            throw new IllegalStateException("Assembled stack must compile as one launchable stage:\\n"
                    + planted.diagnosticText());
        }

        VesselBlockOccupant extra = new VesselBlockOccupant(
                new GridPos(11, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN);
        PlantYard.CraneOutcome overlap = new PlantYard(
                VesselBlockGrid.of(extra), yard.planted(), null)
                .startInstall(extra.componentId(), new GridPos(0, 0, 0));
        if (overlap.accepted() || !overlap.diagnosticText().contains("OVERLAP")) {
            throw new IllegalStateException("Dropping a second engine on the same nozzle must be OVERLAP:\\n"
                    + overlap.diagnosticText());
        }

        return new Result(
                yard.planted().occupants().size(),
                craneSeconds,
                midTransitEmpty,
                planted.isLaunchable(),
                busy.diagnosticText(),
                overlap.diagnosticText());
    }

    private static PlantYard complete(PlantYard yard, String stagedComponentId, GridPos destination) {
        PlantYard.CraneOutcome start = yard.startInstall(stagedComponentId, destination);
        if (!start.accepted()) {
            throw new IllegalStateException("Lift of " + stagedComponentId + " must start:\\n"
                    + start.diagnosticText());
        }
        return start.yard().advance(start.yard().job().durationSeconds());
    }
}
