package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;

/**
 * After liftoff the pile is a world entity that climbs; 20 km still swaps to the map.
 * Minecraft spawn/render is {@code VesselEntity} using existing part models.
 */
public final class EntitySlice {
    private EntitySlice() {
    }

    public record Result(
            boolean entityAfterLiftoff,
            boolean heldThenClimbed,
            double climbAltitude,
            boolean mapAtTwentyKm,
            boolean playerNotOnBoard) {
    }

    public static Result execute() {
        FlightLoop flown = FlightLoop.sit(hopperOnPad()).liftoff();
        if (flown.presence() != FlightPresence.WORLD_ENTITY) {
            throw new IllegalStateException("Liftoff must be the world entity");
        }
        if (hasKind(flown.flying(), VesselPartKind.PAD)) {
            throw new IllegalStateException("Pad does not fly with the entity");
        }

        FlightLoop holding = flown;
        for (int i = 0; i < 30; i++) {
            holding = holding.advance(0.1);
        }
        if (holding.presence() != FlightPresence.WORLD_ENTITY) {
            throw new IllegalStateException("Hold-down stays the world entity, not the map");
        }
        if (holding.vessel().orbit().altitudeMeters() > 2.0) {
            throw new IllegalStateException("Clamps must keep the entity on the table for 3 s");
        }

        FlightLoop climbing = holding;
        for (int i = 0; i < 120; i++) {
            climbing = climbing.advance(0.1);
        }
        double alt = climbing.vessel().orbit().altitudeMeters();
        if (!(alt > 50.0)) {
            throw new IllegalStateException("Entity must climb after release, alt=" + alt);
        }
        if (climbing.presence() != FlightPresence.WORLD_ENTITY) {
            throw new IllegalStateException("A hopper hop stays in the Overworld");
        }

        FlightLoop mapped = climbing.withOrbit(atAltitude(WorldHandoff.MAP_ALTITUDE_METERS));
        if (mapped.presence() != FlightPresence.ORBIT_MAP) {
            throw new IllegalStateException("20 km discards the world entity for the map");
        }
        return new Result(true, true, alt, true, true);
    }

    private static OrbitState atAltitude(double altitudeMeters) {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + altitudeMeters;
        return new OrbitState(earth, new Vector3d(radius, 0.0, 0.0), Vector3d.ZERO, 0.0);
    }

    private static VesselBlockGrid hopperOnPad() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
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
