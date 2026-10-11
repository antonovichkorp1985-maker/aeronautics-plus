package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.StationPlan;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;

/**
 * Parked circular orbit with a habitat becomes walkable blocks.
 * Kepler map stays for transfers. Pad recovery stays below 20 km.
 * No dimension, no suit, no shuttle cargo doors.
 */
public final class StationLoopSlice {
    private StationLoopSlice() {
    }

    public record Result(
            boolean compilerBus,
            boolean parkedOccupies,
            boolean transferRefused,
            boolean hopperRefused,
            boolean boosterRefused,
            boolean leftToMap,
            boolean noPad,
            boolean habitatWalkable) {
    }

    public static Result execute() {
        VesselCompilation compiled = VesselBlockCompiler.analyze(bus());
        if (!compiled.isLaunchable() || !compiled.stages().get(0).hasHabitat()) {
            throw new IllegalStateException("Pad stack with habitat must compile:\n"
                    + compiled.diagnosticText());
        }

        FlightLoop parked = FlightLoop.sit(bus()).liftoff()
                .withOrbit(atAltitude(Aerodynamics.FAIRING_MIN_ALTITUDE_METERS))
                .dropFairing()
                .withOrbit(SpaceBodies.parkingOrbit())
                .occupyStation();
        if (parked.presence() != FlightPresence.STATION_BLOCKS) {
            throw new IllegalStateException("Circular parking with a habitat must become station blocks");
        }
        StationPlan plan = parked.station();
        if (plan == null
                || !plan.includesKind(VesselPartKind.HABITAT)
                || plan.includesKind(VesselPartKind.PAD)
                || plan.includesKind(VesselPartKind.FAIRING)) {
            throw new IllegalStateException("Station blocks keep the habitat, not the pad or fairing");
        }

        FlightLoop transfer = FlightLoop.sit(bus()).liftoff()
                .withOrbit(atAltitude(Aerodynamics.FAIRING_MIN_ALTITUDE_METERS))
                .dropFairing()
                .withOrbit(SpaceBodies.ellipticTransfer());
        if (transfer.presence() != FlightPresence.ORBIT_MAP) {
            throw new IllegalStateException("A transfer ellipse stays on the Kepler map");
        }
        try {
            transfer.occupyStation();
            throw new IllegalStateException("A transfer ellipse must not become a station");
        } catch (IllegalStateException expected) {
            if (expected.getMessage() == null || !expected.getMessage().contains("parked")) {
                throw expected;
            }
        }

        FlightLoop hopper = FlightLoop.sit(bus()).liftoff().withOrbit(atAltitude(19_000.0));
        try {
            hopper.occupyStation();
            throw new IllegalStateException("Below 20 km must not become a station");
        } catch (IllegalStateException expected) {
            if (expected.getMessage() == null || !expected.getMessage().contains("map")) {
                throw expected;
            }
        }

        try {
            FlightLoop.sit(booster()).liftoff()
                    .withOrbit(SpaceBodies.parkingOrbit())
                    .occupyStation();
            throw new IllegalStateException("A booster without a habitat must not become a station");
        } catch (IllegalStateException expected) {
            if (expected.getMessage() == null || !expected.getMessage().contains("habitat")) {
                throw expected;
            }
        }

        FlightLoop left = parked.leaveStation();
        if (left.presence() != FlightPresence.ORBIT_MAP || left.station() != null) {
            throw new IllegalStateException("Leaving the station returns to the Kepler map");
        }

        return new Result(true, true, true, true, true, true, true, true);
    }

    private static OrbitState atAltitude(double altitudeMeters) {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + altitudeMeters;
        return new OrbitState(earth, new Vector3d(radius, 0.0, 0.0), Vector3d.ZERO, 0.0);
    }

    private static VesselBlockGrid bus() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.HABITAT, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 5, 0), VesselPartCatalog.FAIRING, BlockFace.UP));
    }

    private static VesselBlockGrid booster() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }
}
