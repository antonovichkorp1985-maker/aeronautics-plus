package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;

/**
 * Commanded pyro with propellant still in the booster. KSP staging, not a dry-only timer.
 * Not ChemMod air.
 */
public final class CrewStageSlice {
    private CrewStageSlice() {
    }

    public record Result(double boosterPropellantKg, double upperPropellantKg, int upperStages) {
    }

    public static Result execute() {
        FlightLoop flown = FlightLoop.sit(twoStageOnPad()).liftoff();
        double live = flown.vessel().activeStage().propellantMassKg();
        if (!(live > 1.0)) {
            throw new IllegalStateException("Wet staging needs fuel in the booster, got " + live);
        }
        FlightSplit split = flown.firePyro();
        double booster = split.booster().vessel().livePropellantKg();
        double upper = split.continuing().vessel().livePropellantKg();
        if (!(booster > 1.0)) {
            throw new IllegalStateException("Commanded pyro must not dump booster fuel, got " + booster);
        }
        if (split.continuing().vessel().stages().size() != 1) {
            throw new IllegalStateException("Upper after a wet stage must be one stage");
        }
        if (hasKind(split.booster().flying(), VesselPartKind.CONTROLLER)) {
            throw new IllegalStateException("Seat stays on the upper");
        }
        if (!hasKind(split.continuing().flying(), VesselPartKind.CONTROLLER)) {
            throw new IllegalStateException("Upper keeps the seat");
        }
        return new Result(booster, upper, split.continuing().vessel().stages().size());
    }

    private static VesselBlockGrid twoStageOnPad() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.SEPARATOR, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 5, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 6, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }

    private static boolean hasKind(VesselBlockGrid grid, VesselPartKind kind) {
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().kind() == kind) {
                return true;
            }
        }
        return false;
    }
}
