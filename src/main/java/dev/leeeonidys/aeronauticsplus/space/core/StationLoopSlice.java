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
 * Enter-station is a command. Parking with a habitat stays on the Kepler map
 * until that command. Unmanned sats cannot enter. Pocket islands are spaced.
 */
public final class StationLoopSlice {
    private StationLoopSlice() {
    }

    public record Result(
            boolean compilerBus,
            boolean parkedStaysOnMap,
            boolean parkedOccupies,
            boolean transferRefused,
            boolean hopperRefused,
            boolean boosterRefused,
            boolean leftToMap,
            boolean noPad,
            boolean habitatWalkable,
            boolean islandStride) {
    }

    public static Result execute() {
        VesselCompilation compiled = VesselBlockCompiler.analyze(bus());
        if (!compiled.isLaunchable() || !compiled.stages().get(0).hasHabitat()) {
            throw new IllegalStateException("Pad stack with habitat must compile:\n"
                    + compiled.diagnosticText());
        }

        FlightLoop coasting = FlightLoop.sit(bus()).liftoff()
                .withOrbit(atAltitude(Aerodynamics.FAIRING_MIN_ALTITUDE_METERS))
                .dropFairing()
                .withOrbit(SpaceBodies.parkingOrbit());
        if (coasting.presence() != FlightPresence.ORBIT_MAP || !coasting.hasHabitat()) {
            throw new IllegalStateException("A burn/coast with a habitat stays on the Kepler map");
        }

        FlightLoop parked = coasting.occupyStation();
        if (parked.presence() != FlightPresence.STATION_BLOCKS) {
            throw new IllegalStateException("The enter-station command must open the walkable grid");
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

        if (StationHandoff.islandX(0) != 0 || StationHandoff.islandX(2) != 2 * StationHandoff.ISLAND_STRIDE) {
            throw new IllegalStateException("Station islands must be spaced, not stacked");
        }

        return new Result(true, true, true, true, true, true, true, true, true, true);
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
