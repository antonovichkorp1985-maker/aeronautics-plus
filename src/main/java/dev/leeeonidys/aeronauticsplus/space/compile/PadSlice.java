package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;

/**
 * Launch table is AP ground infrastructure. It does not fly and is not ChemMod.
 */
public final class PadSlice {
    private PadSlice() {
    }

    public record Result(boolean onPad, boolean notOnPad, boolean padMassExcluded, String onPadText) {
    }

    public static Result execute() {
        VesselCompilation stacked = VesselBlockCompiler.analyze(onPad());
        if (!stacked.isLaunchable()) {
            throw new IllegalStateException("Stack on the pad must be launchable:\n" + stacked.diagnosticText());
        }
        if (!stacked.diagnosticText().contains("ON_PAD")) {
            throw new IllegalStateException("Pad must report ON_PAD:\n" + stacked.diagnosticText());
        }
        if (stacked.diagnosticText().contains("ON_TRANSPORTER")) {
            throw new IllegalStateException("Pad is not the train mount:\n" + stacked.diagnosticText());
        }

        VesselCompilation flying = VesselBlockCompiler.analyze(airframe());
        if (Math.abs(stacked.stages().get(0).totalMassKg() - flying.stages().get(0).totalMassKg()) > 1.0e-9) {
            throw new IllegalStateException("Pad mass must stay on the ground");
        }
        if (!flying.diagnosticText().contains("NOT_ON_PAD")) {
            throw new IllegalStateException("Airframe off the table must warn NOT_ON_PAD:\n"
                    + flying.diagnosticText());
        }

        return new Result(
                stacked.diagnosticText().contains("ON_PAD"),
                flying.diagnosticText().contains("NOT_ON_PAD"),
                Math.abs(stacked.stages().get(0).totalMassKg() - flying.stages().get(0).totalMassKg()) < 1.0e-9,
                stacked.diagnosticText());
    }

    private static VesselBlockGrid onPad() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }

    private static VesselBlockGrid airframe() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }
}
