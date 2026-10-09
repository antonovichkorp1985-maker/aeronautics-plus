package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDynamics;
import dev.leeeonidys.aeronauticsplus.space.core.VesselState;

/**
 * Crewed/service bus: habitat, RCS, gyrodyne, solar. Gyros kill residual spin without
 * propellant; RCS is a separate cluster, not the main engine.
 */
public final class SpacecraftSlice {
    private SpacecraftSlice() {
    }

    public record Result(
            boolean launchable,
            boolean habitat,
            boolean gyro,
            boolean solar,
            double spinAfter,
            double rcsMassDropped,
            String noRcs) {
    }

    public static Result execute() {
        VesselCompilation bus = VesselBlockCompiler.analyze(spacecraft());
        if (!bus.isLaunchable()) {
            throw new IllegalStateException("Spacecraft bus must be launchable:\n" + bus.diagnosticText());
        }
        if (bus.diagnosticText().contains("NO_RCS")
                || bus.diagnosticText().contains("GYRO_WITHOUT_SOLAR")
                || bus.diagnosticText().contains("NO_POWER_PATH")
                || bus.diagnosticText().contains("NO_DOCKING")
                || bus.diagnosticText().contains("NO_BATTERY")
                || bus.diagnosticText().contains("NO_RADIATOR")
                || bus.diagnosticText().contains("NO_THERMAL_PATH")
                || bus.diagnosticText().contains("NO_ANTENNA")) {
            throw new IllegalStateException("Complete bus must not warn about ACS/power/heat:\n"
                    + bus.diagnosticText());
        }
        if (!bus.stages().get(0).hasHabitat()
                || !bus.stages().get(0).hasGyro()
                || !bus.stages().get(0).hasSolar()
                || !bus.stages().get(0).hasDocking()
                || !bus.stages().get(0).hasBattery()
                || !bus.stages().get(0).hasRadiator()
                || bus.stages().get(0).rcsThrusters().isEmpty()) {
            throw new IllegalStateException("Bus must compile habitat, gyro, solar, docking, battery, radiator and RCS");
        }

        VesselState initial = bus.toVesselState("spacecraft-demo", SpaceBodies.parkingOrbit());
        VesselState spinning = initial.withAngularVelocity(new Vector3d(0.5, 0.0, 0.0));
        VesselState despun = VesselDynamics.despinWithGyro(spinning, 8.0);
        if (despun.angularVelocityBody().magnitude() > 0.05) {
            throw new IllegalStateException("Gyrodyne must kill residual spin, got "
                    + despun.angularVelocityBody().magnitude());
        }
        if (Math.abs(despun.totalMassKg() - spinning.totalMassKg()) > 1.0e-9) {
            throw new IllegalStateException("Gyrodyne must not spend propellant");
        }
        try {
            VesselDynamics.despinWithGyro(
                    VesselBlockCompiler.analyze(busWithoutGyro()).toVesselState(
                            "no-gyro", SpaceBodies.parkingOrbit()).withAngularVelocity(new Vector3d(0.5, 0.0, 0.0)),
                    1.0);
            throw new IllegalStateException("Despin without a CMG must fail");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains("gyrodyne")) {
                throw exception;
            }
        }
        try {
            VesselDynamics.despinWithGyro(
                    VesselBlockCompiler.analyze(busWithoutSolar()).toVesselState(
                            "no-solar", SpaceBodies.parkingOrbit()).withAngularVelocity(new Vector3d(0.5, 0.0, 0.0)),
                    1.0);
            throw new IllegalStateException("Despin without a panel must fail");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains("solar")) {
                throw exception;
            }
        }

        double massBefore = initial.totalMassKg();
        VesselState afterRcs = VesselDynamics.fireRcs(initial, 2.0);
        double dropped = massBefore - afterRcs.totalMassKg();
        if (!(dropped > 0.1)) {
            throw new IllegalStateException("RCS must consume propellant, dropped=" + dropped);
        }
        if (!(afterRcs.orbit().velocityMetersPerSecond().magnitude()
                > initial.orbit().velocityMetersPerSecond().magnitude())) {
            throw new IllegalStateException("RCS must add inertial speed");
        }

        String noRcs = VesselBlockCompiler.analyze(PayloadSlice.orbitalStack()).diagnosticText();
        if (!noRcs.contains("NO_RCS")) {
            throw new IllegalStateException("Payload without RCS must warn NO_RCS:\n" + noRcs);
        }
        String darkGyro = VesselBlockCompiler.analyze(busWithoutSolar()).diagnosticText();
        if (!darkGyro.contains("GYRO_WITHOUT_SOLAR")) {
            throw new IllegalStateException("Gyro without solar must warn:\n" + darkGyro);
        }

        return new Result(
                bus.isLaunchable(),
                bus.stages().get(0).hasHabitat(),
                bus.stages().get(0).hasGyro(),
                bus.stages().get(0).hasSolar(),
                despun.angularVelocityBody().magnitude(),
                dropped,
                noRcs);
    }

    public static VesselBlockGrid spacecraft() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 1, 0), VesselPartCatalog.RCS, BlockFace.EAST),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.HABITAT, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(-1, 2, 0), VesselPartCatalog.OMNI_ANTENNA, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, -1), VesselPartCatalog.DOCKING, BlockFace.NORTH),
                new VesselBlockOccupant(new GridPos(1, 2, 0), VesselPartCatalog.RADIATOR, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.GYRO, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(-1, 3, 0), VesselPartCatalog.BATTERY, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 3, 0), VesselPartCatalog.SOLAR, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.PAYLOAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 5, 0), VesselPartCatalog.FAIRING, BlockFace.UP));
    }

    private static VesselBlockGrid busWithoutGyro() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 1, 0), VesselPartCatalog.RCS, BlockFace.EAST),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.HABITAT, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 2, 0), VesselPartCatalog.SOLAR, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.PAYLOAD, BlockFace.UP));
    }

    private static VesselBlockGrid busWithoutSolar() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.HABITAT, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.GYRO, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.PAYLOAD, BlockFace.UP));
    }
}
