package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;

/**
 * Kepler map plot: pad on the Earth disk, 20 km on the handoff ring, parking outside it.
 * Display math only — the Minecraft screen is the client. Not ChemMod air.
 */
public final class MapViewSlice {
    private MapViewSlice() {
    }

    public record Result(
            double padRadius,
            double hopperRadius,
            double handoffRadius,
            double parkingRadius,
            boolean hopperInWorld,
            boolean parkingOnMap) {
    }

    public static Result execute() {
        double padR = OrbitMapView.plot(0.0, 0.0).radius();
        if (Math.abs(padR - OrbitMapView.EARTH_FRACTION) > 1.0e-9) {
            throw new IllegalStateException("Pad must sit on the Earth disk, r=" + padR);
        }
        double handoffR = OrbitMapView.handoffRadius();
        if (!(handoffR > padR)) {
            throw new IllegalStateException("20 km ring must sit outside the Earth disk");
        }
        OrbitMapView.Plot edge = OrbitMapView.plot(WorldHandoff.MAP_ALTITUDE_METERS, 0.0);
        if (Math.abs(edge.radius() - handoffR) > 1.0e-9) {
            throw new IllegalStateException("Handoff altitude must land on the handoff ring");
        }
        double hopperR = OrbitMapView.plot(2_000.0, 0.0).radius();
        if (!(hopperR < handoffR)) {
            throw new IllegalStateException("A 2 km hopper must plot inside the 20 km ring");
        }
        double parkingR = OrbitMapView.plot(OrbitMapView.VIEW_ALTITUDE_METERS, 0.0).radius();
        if (Math.abs(parkingR - 1.0) > 1.0e-9 || !(parkingR > handoffR)) {
            throw new IllegalStateException("Parking orbit must sit on the outer ring");
        }

        FlightLoop flown = FlightLoop.sit(hopper()).liftoff();
        OrbitMapTrack world = OrbitMapTrack.from(flown);
        if (world.presence() != FlightPresence.WORLD_ENTITY || world.altitudeMeters() >= 20_000.0) {
            throw new IllegalStateException("Just after liftoff the track is the world entity");
        }
        FlightLoop mapped = flown.withOrbit(atAltitude(WorldHandoff.MAP_ALTITUDE_METERS));
        OrbitMapTrack map = OrbitMapTrack.from(mapped);
        if (map.presence() != FlightPresence.ORBIT_MAP) {
            throw new IllegalStateException("20 km track must be the Kepler map");
        }
        if (!(OrbitMapView.plot(map).radius() > OrbitMapView.plot(world).radius())) {
            throw new IllegalStateException("Map track must plot outside the world hopper");
        }

        return new Result(padR, hopperR, handoffR, parkingR, true, true);
    }

    private static OrbitState atAltitude(double altitudeMeters) {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + altitudeMeters;
        return new OrbitState(earth, new Vector3d(radius, 0.0, 0.0), Vector3d.ZERO, 0.0);
    }

    private static VesselBlockGrid hopper() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }
}
