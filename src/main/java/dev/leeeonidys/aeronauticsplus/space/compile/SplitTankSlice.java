package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselBlueprint;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.core.VesselConnection;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDynamics;
import dev.leeeonidys.aeronauticsplus.space.core.VesselState;

/**
 * Dedicated RP-1 and LOX barrels. Oxidizer does not travel through the fuel hull;
 * Falcon / Soyuz feed the engine from the side, not through a 1-wide stack.
 */
public final class SplitTankSlice {
    private SplitTankSlice() {
    }

    public record Result(
            boolean sideBySideLaunchable,
            boolean commonBulkheadLaunchable,
            int fuelLinks,
            int oxidizerLinks,
            double massDroppedKg,
            String fuelOnly,
            String stackedThroughFuel) {
    }

    public static Result execute() {
        VesselCompilation side = VesselBlockCompiler.compile(sideBySide()).analyze();
        if (!side.isLaunchable()) {
            throw new IllegalStateException("Side-by-side RP-1/LOX must feed the engine:\n"
                    + side.diagnosticText());
        }
        var blueprint = VesselBlockCompiler.compile(sideBySide());
        int fuel = count(blueprint, VesselConnection.ConnectionKind.FUEL);
        int oxidizer = count(blueprint, VesselConnection.ConnectionKind.OXIDIZER);
        if (fuel < 1 || oxidizer < 1) {
            throw new IllegalStateException(
                    "Split tanks must each expose their own feed: fuel=" + fuel
                            + " oxidizer=" + oxidizer);
        }

        VesselState before = side.toVesselState("split-tanks", SpaceBodies.parkingOrbit());
        VesselState after = VesselDynamics.burnActiveStage(before, 3.0);
        double dropped = before.totalMassKg() - after.totalMassKg();
        if (!(dropped > 0.5)) {
            throw new IllegalStateException("Split RP-1 and LOX must both drain during a burn");
        }

        VesselCompilation common = VesselBlockCompiler.compile(commonBulkhead()).analyze();
        if (!common.isLaunchable()) {
            throw new IllegalStateException("Common-bulkhead tank must still launch:\n"
                    + common.diagnosticText());
        }

        String fuelOnly = VesselBlockCompiler.compile(fuelOnly()).analyze().diagnosticText();
        if (!fuelOnly.contains("NO_OXIDIZER_PATH") || fuelOnly.contains("NO_FUEL_PATH")) {
            throw new IllegalStateException(
                    "RP-1 barrel next to the engine is not a LOX path:\n" + fuelOnly);
        }

        String stacked = VesselBlockCompiler.compile(stackedThroughFuel()).analyze().diagnosticText();
        if (!stacked.contains("NO_OXIDIZER_PATH")) {
            throw new IllegalStateException(
                    "LOX must not travel through the RP-1 hull:\n" + stacked);
        }

        return new Result(
                side.isLaunchable(),
                common.isLaunchable(),
                fuel,
                oxidizer,
                dropped,
                fuelOnly,
                stacked);
    }

    /** Engine between an RP-1 barrel and a LOX barrel. */
    public static VesselBlockGrid sideBySide() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(-1, 0, 0), VesselPartCatalog.FUEL_TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 0, 0), VesselPartCatalog.OXIDIZER_TANK, BlockFace.UP));
    }

    private static VesselBlockGrid commonBulkhead() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP));
    }

    private static VesselBlockGrid fuelOnly() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(-1, 0, 0), VesselPartCatalog.FUEL_TANK, BlockFace.UP));
    }

    /** 1-wide stack: LOX sits on RP-1, so the engine never sees oxidizer ports. */
    private static VesselBlockGrid stackedThroughFuel() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.FUEL_TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.OXIDIZER_TANK, BlockFace.UP));
    }

    private static int count(VesselBlueprint blueprint, VesselConnection.ConnectionKind kind) {
        int total = 0;
        for (VesselConnection connection : blueprint.connections()) {
            if (connection.kind() == kind) {
                total++;
            }
        }
        return total;
    }
}
