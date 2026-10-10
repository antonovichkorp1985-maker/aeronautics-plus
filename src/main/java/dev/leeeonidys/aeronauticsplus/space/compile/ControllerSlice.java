package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;

/**
 * The control seat is what makes a ChemMod pile a rocket. Tanks stay ChemMod.
 */
public final class ControllerSlice {
    private ControllerSlice() {
    }

    public record Result(boolean designated, boolean unmarked, String noController) {
    }

    public static Result execute() {
        VesselCompilation marked = VesselBlockCompiler.analyze(engineWithSeat());
        if (!marked.isLaunchable()) {
            throw new IllegalStateException("Engine + tank + seat must launch:\n" + marked.diagnosticText());
        }
        if (marked.diagnosticText().contains("NO_CONTROLLER") || !marked.stages().get(0).hasController()) {
            throw new IllegalStateException("Seat must designate the pile as a rocket:\n" + marked.diagnosticText());
        }

        String noController = VesselBlockCompiler.analyze(engineWithoutSeat()).diagnosticText();
        if (!noController.contains("NO_CONTROLLER")) {
            throw new IllegalStateException("Engine without a seat is not a rocket yet:\n" + noController);
        }

        return new Result(marked.isLaunchable(), noController.contains("NO_CONTROLLER"), noController);
    }

    private static VesselBlockGrid engineWithSeat() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }

    private static VesselBlockGrid engineWithoutSeat() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP));
    }
}
