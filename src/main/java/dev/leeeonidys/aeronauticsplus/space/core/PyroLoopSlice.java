package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;

/**
 * In-flight pyro cuts the flying pile wherever it is. Booster can sit back as blocks
 * only under 20 km. Not ChemMod air.
 */
public final class PyroLoopSlice {
    private PyroLoopSlice() {
    }

    public record Result(
            boolean pyroInWorld,
            boolean pyroOnMap,
            boolean boosterHasEngine,
            boolean upperHasSeat,
            boolean recoveredOnPad,
            int recoveredParts) {
    }

    public static Result execute() {
        FlightLoop flown = FlightLoop.sit(twoStageOnPad()).liftoff();
        try {
            FlightLoop.sit(twoStageOnPad()).firePyro();
            throw new IllegalStateException("Pyro must not fire while the pile is still blocks");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().toLowerCase(java.util.Locale.ROOT).contains("flight")
                    && !expected.getMessage().contains("pad")) {
                throw expected;
            }
        }

        FlightSplit world = flown.firePyro();
        if (world.continuing().presence() != FlightPresence.WORLD_ENTITY
                || world.booster().presence() != FlightPresence.WORLD_ENTITY) {
            throw new IllegalStateException("Pad-altitude pyro stays the world entity");
        }
        if (!hasKind(world.booster().flying(), VesselPartKind.ENGINE)
                || hasKind(world.booster().flying(), VesselPartKind.CONTROLLER)) {
            throw new IllegalStateException("Booster is engine+tank, not the seat");
        }
        if (!hasKind(world.continuing().flying(), VesselPartKind.CONTROLLER)
                || hasKind(world.continuing().flying(), VesselPartKind.SEPARATOR)) {
            throw new IllegalStateException("Upper keeps the seat; the ring stays on the booster");
        }

        FlightLoop mapped = flown.withOrbit(atAltitude(WorldHandoff.MAP_ALTITUDE_METERS));
        FlightSplit onMap = mapped.firePyro();
        if (onMap.continuing().presence() != FlightPresence.ORBIT_MAP
                || onMap.booster().presence() != FlightPresence.ORBIT_MAP) {
            throw new IllegalStateException("Pyro on the map must not wait for 20 km");
        }
        try {
            onMap.booster().recover();
            throw new IllegalStateException("Map booster must not place blocks");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().contains("20")) {
                throw expected;
            }
        }

        FlightLoop reentry = onMap.booster().withOrbit(atAltitude(19_000.0));
        FlightLoop recovered = reentry.recover();
        if (recovered.presence() != FlightPresence.BLOCKS_ON_PAD || recovered.recovery() == null) {
            throw new IllegalStateException("Under 20 km the booster sits back as blocks");
        }
        if (recovered.recovery().includesKind(VesselPartKind.PAD)
                || !recovered.recovery().includes(VesselPartCatalog.ENGINE.id())) {
            throw new IllegalStateException("Recovered plan is engine on the existing pad");
        }

        return new Result(
                world.booster().presence() == FlightPresence.WORLD_ENTITY,
                onMap.booster().presence() == FlightPresence.ORBIT_MAP,
                hasKind(world.booster().flying(), VesselPartKind.ENGINE),
                hasKind(world.continuing().flying(), VesselPartKind.CONTROLLER),
                recovered.recovery() != null,
                recovered.recovery().placements().size());
    }

    private static OrbitState atAltitude(double altitudeMeters) {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + altitudeMeters;
        return new OrbitState(earth, new Vector3d(radius, 0.0, 0.0), Vector3d.ZERO, 0.0);
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
