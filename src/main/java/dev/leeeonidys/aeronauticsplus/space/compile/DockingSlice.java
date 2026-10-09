package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselBlueprint;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.core.VesselConnection;

/**
 * Docking ring, eclipse battery and vacuum radiator. Soyuz / ISS bus is not complete
 * with only a habitat and a solar wing.
 */
public final class DockingSlice {
    private DockingSlice() {
    }

    public record Result(
            boolean completeLaunchable,
            boolean docking,
            boolean battery,
            boolean radiator,
            int dockingLinks,
            String noDocking,
            String noBattery,
            String noRadiator) {
    }

    public static Result execute() {
        VesselCompilation complete = VesselBlockCompiler.analyze(SpacecraftSlice.spacecraft());
        if (!complete.isLaunchable()) {
            throw new IllegalStateException("Complete bus must stay launchable:\\n" + complete.diagnosticText());
        }
        String completeText = complete.diagnosticText();
        if (completeText.contains("NO_DOCKING")
                || completeText.contains("NO_BATTERY")
                || completeText.contains("NO_RADIATOR")
                || completeText.contains("NO_THERMAL_PATH")
                || completeText.contains("NO_POWER_PATH")
                || completeText.contains("BATTERY_WITHOUT_SOLAR")
                || completeText.contains("NO_ANTENNA")) {
            throw new IllegalStateException("Complete bus must not warn about dock/power/heat:\\n" + completeText);
        }
        if (!complete.stages().get(0).hasDocking()
                || !complete.stages().get(0).hasBattery()
                || !complete.stages().get(0).hasRadiator()) {
            throw new IllegalStateException("Bus must compile docking, battery and radiator");
        }

        int dockingLinks = count(VesselBlockCompiler.compile(facingPorts()), VesselConnection.ConnectionKind.DOCKING);
        if (dockingLinks != 1) {
            throw new IllegalStateException("Two ports looking at each other must share one docking link, got "
                    + dockingLinks);
        }
        int missed = count(VesselBlockCompiler.compile(misalignedPorts()), VesselConnection.ConnectionKind.DOCKING);
        if (missed != 0) {
            throw new IllegalStateException("Ports not facing each other must not dock, got " + missed);
        }

        String noDocking = VesselBlockCompiler.analyze(habitatWithoutDocking()).diagnosticText();
        if (!noDocking.contains("NO_DOCKING")) {
            throw new IllegalStateException("Habitat without a port must warn NO_DOCKING:\\n" + noDocking);
        }
        String noBattery = VesselBlockCompiler.analyze(solarWithoutBattery()).diagnosticText();
        if (!noBattery.contains("NO_BATTERY")) {
            throw new IllegalStateException("Solar without a battery must warn NO_BATTERY:\\n" + noBattery);
        }
        String noRadiator = VesselBlockCompiler.analyze(habitatWithoutRadiator()).diagnosticText();
        if (!noRadiator.contains("NO_RADIATOR")) {
            throw new IllegalStateException("Habitat without a radiator must warn NO_RADIATOR:\\n" + noRadiator);
        }

        return new Result(
                complete.isLaunchable(),
                complete.stages().get(0).hasDocking(),
                complete.stages().get(0).hasBattery(),
                complete.stages().get(0).hasRadiator(),
                dockingLinks,
                noDocking,
                noBattery,
                noRadiator);
    }

    /** APAS/IDSS: capture faces look at each other. */
    public static VesselBlockGrid facingPorts() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.DOCKING, BlockFace.EAST),
                new VesselBlockOccupant(new GridPos(1, 0, 0), VesselPartCatalog.DOCKING, BlockFace.WEST));
    }

    private static VesselBlockGrid misalignedPorts() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.DOCKING, BlockFace.EAST),
                new VesselBlockOccupant(new GridPos(1, 0, 0), VesselPartCatalog.DOCKING, BlockFace.EAST));
    }

    private static VesselBlockGrid habitatWithoutDocking() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.HABITAT, BlockFace.UP));
    }

    private static VesselBlockGrid solarWithoutBattery() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.GYRO, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 2, 0), VesselPartCatalog.SOLAR, BlockFace.UP));
    }

    private static VesselBlockGrid habitatWithoutRadiator() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.HABITAT, BlockFace.UP));
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
