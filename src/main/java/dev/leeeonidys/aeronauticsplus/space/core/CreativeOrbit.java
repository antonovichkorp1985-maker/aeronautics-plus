package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;

/**
 * Cheat parked stack for {@code /ap state}. Not a launch, not a planet world.
 */
public final class CreativeOrbit {
    public static final double PARK_ALTITUDE_METERS = 200_000.0;

    private CreativeOrbit() {
    }

    public static FlightLoop parkedAround(CelestialBody body) {
        if (body == null) {
            throw new IllegalArgumentException("Body is required");
        }
        FlightLoop loop = FlightLoop.sit(bus()).liftoff()
                .withOrbit(SolarSystem.circular(body, PARK_ALTITUDE_METERS));
        if (loop.hasFairing()) {
            loop = loop.dropFairing();
        }
        if (!StationHandoff.parked(loop.vessel().orbit())) {
            throw new IllegalStateException("Creative park must be circular on the map, body=" + body.id());
        }
        return loop;
    }

    private static VesselBlockGrid bus() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.FAIRING, BlockFace.UP));
    }
}
