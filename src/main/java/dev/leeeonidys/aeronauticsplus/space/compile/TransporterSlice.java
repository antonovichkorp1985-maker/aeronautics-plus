package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;

/**
 * Deterministic proof that an assembled plant reaches the pad on a crawler:
 * the haul is a timed action, not a teleport, and a 3-cell-wide stack is ~3 m
 * across rather than one decorative block.
 */
public final class TransporterSlice {
    private TransporterSlice() {
    }

    public record Result(
            double diameterMeters,
            double heightMeters,
            double haulSeconds,
            boolean midTransitEmpty,
            boolean launchableAtPad,
            int padOriginX,
            String busy,
            String nothing) {
    }

    public static Result execute() {
        PlantYard yard = assembleWideStack();
        VesselEnvelope envelope = VesselEnvelope.of(yard.planted());
        if (!(envelope.diameter() >= 2.5)) {
            throw new IllegalStateException(
                    "A 3-cell-wide stack must be at least 2.5 m across, got " + envelope.diameter());
        }
        if (!(envelope.height() >= 1.5)) {
            throw new IllegalStateException("Stack height must span engine and tank, got " + envelope.height());
        }

        GridPos pad = new GridPos(20, 0, 0);
        PlantYard.CraneOutcome start = yard.startHaul(pad);
        if (!start.accepted()) {
            throw new IllegalStateException("Crawler haul must start:\n" + start.diagnosticText());
        }
        yard = start.yard();
        double haulSeconds = yard.crawler().durationSeconds();
        if (!(haulSeconds > 1.0)) {
            throw new IllegalStateException("A 20 m crawler haul must take real time, got " + haulSeconds);
        }
        if (!yard.planted().isEmpty() || !yard.crawlerBusy()) {
            throw new IllegalStateException("After start the stack is on the crawler, not teleported to the pad");
        }

        PlantYard mid = yard.advance(haulSeconds * 0.5);
        boolean midTransitEmpty = mid.planted().isEmpty() && mid.crawlerBusy();
        if (!midTransitEmpty) {
            throw new IllegalStateException("Mid-haul pad must stay empty: crawler is not a teleport");
        }
        PlantYard.CraneOutcome busy = mid.startHaul(new GridPos(40, 0, 0));
        if (busy.accepted() || !busy.diagnosticText().contains("CRAWLER_BUSY")) {
            throw new IllegalStateException("A second haul while the crawler is moving must be CRAWLER_BUSY:\n"
                    + busy.diagnosticText());
        }
        PlantYard.CraneOutcome craneDuringHaul = mid.startInstall(
                "rocket_tank@0,1,0", new GridPos(0, 3, 0));
        if (craneDuringHaul.accepted() || !craneDuringHaul.diagnosticText().contains("CRAWLER_BUSY")) {
            throw new IllegalStateException("Crane must wait for the crawler:\n" + craneDuringHaul.diagnosticText());
        }

        yard = mid.advance(haulSeconds);
        if (yard.crawlerBusy() || yard.planted().isEmpty()) {
            throw new IllegalStateException("Stack must appear at the pad only after the haul finishes");
        }
        if (yard.planted().origin().x() != 20) {
            throw new IllegalStateException("Pad origin must be x=20, got " + yard.planted().origin());
        }
        VesselCompilation atPad = yard.compilePlanted();
        if (!atPad.isLaunchable()) {
            throw new IllegalStateException("Hauled stack must still compile as launchable:\n"
                    + atPad.diagnosticText());
        }
        VesselEnvelope atPadEnvelope = VesselEnvelope.of(yard.planted());
        if (Math.abs(atPadEnvelope.diameter() - envelope.diameter()) > 1.0e-9) {
            throw new IllegalStateException("Haul must not change envelope diameter");
        }

        PlantYard.CraneOutcome alreadyThere = yard.startHaul(yard.planted().origin());
        if (alreadyThere.accepted() || !alreadyThere.diagnosticText().contains("SAME_CELL")) {
            throw new IllegalStateException("Hauling onto the current cell must be SAME_CELL:\n"
                    + alreadyThere.diagnosticText());
        }
        PlantYard.CraneOutcome empty = new PlantYard(
                yard.staged(), new VesselBlockGrid(java.util.List.of()), null, null)
                .startHaul(new GridPos(40, 0, 0));
        if (empty.accepted() || !empty.diagnosticText().contains("NOTHING_TO_HAUL")) {
            throw new IllegalStateException("Empty bay haul must be NOTHING_TO_HAUL:\n" + empty.diagnosticText());
        }

        return new Result(
                envelope.diameter(),
                envelope.height(),
                haulSeconds,
                midTransitEmpty,
                atPad.isLaunchable(),
                yard.planted().origin().x(),
                busy.diagnosticText(),
                empty.diagnosticText());
    }

    private static PlantYard assembleWideStack() {
        VesselBlockOccupant westFoot = new VesselBlockOccupant(
                new GridPos(10, 0, 0), VesselPartCatalog.STRUCTURE, BlockFace.UP);
        VesselBlockOccupant engine = new VesselBlockOccupant(
                new GridPos(11, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN);
        VesselBlockOccupant eastFoot = new VesselBlockOccupant(
                new GridPos(12, 0, 0), VesselPartCatalog.STRUCTURE, BlockFace.UP);
        VesselBlockOccupant westTank = new VesselBlockOccupant(
                new GridPos(10, 1, 0), VesselPartCatalog.TANK, BlockFace.UP);
        VesselBlockOccupant coreTank = new VesselBlockOccupant(
                new GridPos(11, 1, 0), VesselPartCatalog.TANK, BlockFace.UP);
        VesselBlockOccupant eastTank = new VesselBlockOccupant(
                new GridPos(12, 1, 0), VesselPartCatalog.TANK, BlockFace.UP);
        PlantYard yard = PlantYard.ofStaged(westFoot, engine, eastFoot, westTank, coreTank, eastTank);
        yard = complete(yard, engine.componentId(), engine.pos().translate(-10, 0, 0));
        yard = complete(yard, westFoot.componentId(), westFoot.pos().translate(-10, 0, 0));
        yard = complete(yard, eastFoot.componentId(), eastFoot.pos().translate(-10, 0, 0));
        yard = complete(yard, westTank.componentId(), westTank.pos().translate(-10, 0, 0));
        yard = complete(yard, coreTank.componentId(), coreTank.pos().translate(-10, 0, 0));
        yard = complete(yard, eastTank.componentId(), eastTank.pos().translate(-10, 0, 0));
        return yard;
    }

    private static PlantYard complete(PlantYard yard, String stagedComponentId, GridPos destination) {
        PlantYard.CraneOutcome start = yard.startInstall(stagedComponentId, destination);
        if (!start.accepted()) {
            throw new IllegalStateException("Lift of " + stagedComponentId + " must start:\n"
                    + start.diagnosticText());
        }
        return start.yard().advance(start.yard().job().durationSeconds());
    }
}
