package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;

/**
 * Pad + engine + seat is a working rocket without an AP tank block.
 * ChemMod v1.0.0 did not publish tank feed; the 1 m engine carries the load.
 */
public final class IntegralFeedSlice {
    private IntegralFeedSlice() {
    }

    public record Result(boolean padStackFlies, boolean tankStillWins, boolean disconnectedStillFails) {
    }

    public static Result execute() {
        VesselCompilation bare = VesselBlockCompiler.analyze(padEngineSeat());
        if (!bare.isLaunchable()) {
            throw new IllegalStateException("Pad + engine + seat must fly without a tank block:\\n"
                    + bare.diagnosticText());
        }
        if (!bare.diagnosticText().contains("INTEGRAL_FEED")) {
            throw new IllegalStateException("Integral feed must be visible:\\n" + bare.diagnosticText());
        }
        if (!(bare.stages().get(0).propellantMassKg() > 100.0)) {
            throw new IllegalStateException("1 m engine must carry a working load, propellant="
                    + bare.stages().get(0).propellantMassKg());
        }
        FlightLoop loop = FlightLoop.sit(padEngineSeat()).liftoff();
        if (!(loop.vessel().totalMassKg() > 200.0) || loop.vessel().activeStage().tanks().isEmpty()) {
            throw new IllegalStateException("Liftoff must keep the integral load");
        }

        VesselCompilation withTank = VesselBlockCompiler.analyze(padEngineTankSeat());
        if (!withTank.isLaunchable() || withTank.diagnosticText().contains("INTEGRAL_FEED")) {
            throw new IllegalStateException("A real tank must win over integral feed:\\n"
                    + withTank.diagnosticText());
        }

        String disconnected = VesselBlockCompiler.analyze(engineFarFromTank()).diagnosticText();
        if (!disconnected.contains("NO_FUEL_PATH") || disconnected.contains("INTEGRAL_FEED")) {
            throw new IllegalStateException("A tank on the pile still has to sit on a feed port:\\n"
                    + disconnected);
        }

        return new Result(true, true, true);
    }

    private static VesselBlockGrid padEngineSeat() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }

    private static VesselBlockGrid padEngineTankSeat() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }

    private static VesselBlockGrid engineFarFromTank() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 0, 0), VesselPartCatalog.STRUCTURE, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(2, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(2, 1, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }
}
