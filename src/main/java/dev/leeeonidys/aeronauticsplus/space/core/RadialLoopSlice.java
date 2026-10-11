package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;

/**
 * Radial pyro in flight drops strap-ons wherever the stack is.
 * Axial firePyro is unchanged. Not ChemMod air.
 */
public final class RadialLoopSlice {
    private RadialLoopSlice() {
    }

    public record Result(
            int sideCount,
            boolean radialInWorld,
            boolean radialOnMap,
            boolean coreKeepsSeat,
            boolean sidesAreBoosters,
            boolean recoveredOnPad) {
    }

    public static Result execute() {
        FlightLoop flown = FlightLoop.sit(heavy()).liftoff();
        try {
            FlightLoop.sit(heavy()).fireRadial();
            throw new IllegalStateException("Radial pyro must not fire while the pile is still blocks");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().toLowerCase(java.util.Locale.ROOT).contains("flight")
                    && !expected.getMessage().contains("pad")) {
                throw expected;
            }
        }
        try {
            FlightLoop.sit(axial()).liftoff().fireRadial();
            throw new IllegalStateException("An axial stack must not fire as radial");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().toLowerCase(java.util.Locale.ROOT).contains("radial")
                    && !expected.getMessage().contains("strap")) {
                throw expected;
            }
        }

        RadialFlightSplit world = flown.fireRadial();
        if (world.sides().size() != 2
                || world.core().presence() != FlightPresence.WORLD_ENTITY
                || world.sides().stream().anyMatch(side -> side.presence() != FlightPresence.WORLD_ENTITY)) {
            throw new IllegalStateException("Pad-altitude radial pyro stays the world entity, sides="
                    + world.sides().size());
        }
        if (!hasKind(world.core().flying(), VesselPartKind.CONTROLLER)
                || !hasKind(world.core().flying(), VesselPartKind.SEPARATOR)) {
            throw new IllegalStateException("Core keeps the seat and the radial rings");
        }
        for (FlightLoop side : world.sides()) {
            if (!hasKind(side.flying(), VesselPartKind.ENGINE)
                    || hasKind(side.flying(), VesselPartKind.CONTROLLER)
                    || hasKind(side.flying(), VesselPartKind.SEPARATOR)) {
                throw new IllegalStateException("A side booster is engine+tank, not the core");
            }
        }

        RadialFlightSplit onMap = flown.withOrbit(atAltitude(WorldHandoff.MAP_ALTITUDE_METERS)).fireRadial();
        if (onMap.core().presence() != FlightPresence.ORBIT_MAP
                || onMap.sides().stream().anyMatch(side -> side.presence() != FlightPresence.ORBIT_MAP)) {
            throw new IllegalStateException("Radial pyro on the map must not wait for 20 km");
        }

        FlightLoop recovered = onMap.sides().get(0).withOrbit(atAltitude(19_000.0)).recover();
        if (recovered.presence() != FlightPresence.BLOCKS_ON_PAD || recovered.recovery() == null) {
            throw new IllegalStateException("Under 20 km a side booster sits back as blocks");
        }

        return new Result(
                world.sides().size(),
                world.core().presence() == FlightPresence.WORLD_ENTITY,
                onMap.core().presence() == FlightPresence.ORBIT_MAP,
                hasKind(world.core().flying(), VesselPartKind.CONTROLLER),
                world.sides().stream().allMatch(side -> hasKind(side.flying(), VesselPartKind.ENGINE)),
                recovered.recovery() != null);
    }

    private static OrbitState atAltitude(double altitudeMeters) {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + altitudeMeters;
        return new OrbitState(earth, new Vector3d(radius, 0.0, 0.0), Vector3d.ZERO, 0.0);
    }

    private static VesselBlockGrid heavy() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(-1, 2, 0), VesselPartCatalog.SEPARATOR, BlockFace.WEST),
                new VesselBlockOccupant(new GridPos(-2, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(-2, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 2, 0), VesselPartCatalog.SEPARATOR, BlockFace.EAST),
                new VesselBlockOccupant(new GridPos(2, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(2, 2, 0), VesselPartCatalog.TANK, BlockFace.UP));
    }

    private static VesselBlockGrid axial() {
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
