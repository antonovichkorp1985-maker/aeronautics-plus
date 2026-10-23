package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.GridSplit;
import dev.leeeonidys.aeronauticsplus.space.compile.RecoveryPlan;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;

/**
 * The pile leaves the pad as a world entity, swaps at 20 km, and the booster
 * comes back as blocks. {@link dev.leeeonidys.aeronauticsplus.space.world.WorldLiftoff}
 * and {@link dev.leeeonidys.aeronauticsplus.space.world.WorldStageRecovery#place} are
 * the world writes.
 */
public final class FlightLoopSlice {
    private FlightLoopSlice() {
    }

    public record Result(
            boolean leftPad,
            boolean padStayed,
            boolean edgeOnMap,
            boolean reentryEntity,
            boolean recoveredOnPad,
            boolean pyroOnMap,
            int recoveredParts) {
    }

    public static Result execute() {
        try {
            FlightLoop.sit(airframe()).liftoff();
            throw new IllegalStateException("Liftoff without a pad must not run");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().contains("pad")) {
                throw expected;
            }
        }

        try {
            FlightLoop.sit(onMount());
            throw new IllegalStateException("Train mount must not sit as a launch");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().toLowerCase(java.util.Locale.ROOT).contains("mount")
                    && !expected.getMessage().contains("launchable")
                    && !expected.getMessage().contains("креплени")) {
                throw expected;
            }
        }

        FlightLoop sat = FlightLoop.sit(twoStageOnPad());
        if (sat.presence() != FlightPresence.BLOCKS_ON_PAD || sat.leftThePad()) {
            throw new IllegalStateException("Sat stack must still be blocks on the pad");
        }
        if (!sat.flying().isEmpty()) {
            throw new IllegalStateException("Before liftoff the pile is still blocks, not the entity");
        }
        if (sat.padLeft().isEmpty()) {
            throw new IllegalStateException("Pad must remain after sit");
        }
        if (!hasKind(sat.padLeft(), VesselPartKind.PAD) || hasKind(sat.padLeft(), VesselPartKind.ENGINE)) {
            throw new IllegalStateException("Pad grid is the table, not the engine");
        }

        FlightLoop flown = sat.liftoff();
        if (flown.presence() != FlightPresence.WORLD_ENTITY) {
            throw new IllegalStateException("Liftoff must spawn the world entity");
        }
        if (hasKind(flown.flying(), VesselPartKind.PAD) || !hasKind(flown.flying(), VesselPartKind.ENGINE)) {
            throw new IllegalStateException("Flying pile is the vehicle, pad stays");
        }
        if (!WorldHandoff.inWorld(flown.vessel().orbit())) {
            throw new IllegalStateException("Just after liftoff the entity is still in the Overworld");
        }

        FlightLoop mapped = flown.withOrbit(atAltitude(WorldHandoff.MAP_ALTITUDE_METERS));
        if (mapped.presence() != FlightPresence.ORBIT_MAP || WorldHandoff.inWorld(mapped.vessel().orbit())) {
            throw new IllegalStateException("20 km must swap the entity for the Kepler map");
        }
        try {
            mapped.recover();
            throw new IllegalStateException("Map vehicle must not place blocks");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().contains("20")) {
                throw expected;
            }
        }

        GridSplit split = VesselBlockCompiler.firePyro(mapped.flying());
        if (split.booster().isEmpty() || split.continuing().isEmpty()) {
            throw new IllegalStateException("Pyro must still cut on the map");
        }

        FlightLoop reentry = mapped.withOrbit(atAltitude(19_000.0));
        if (reentry.presence() != FlightPresence.WORLD_ENTITY) {
            throw new IllegalStateException("Coming back under 20 km must be the world entity");
        }

        FlightLoop booster = new FlightLoop(
                reentry.presence(),
                reentry.stacked(),
                reentry.padLeft(),
                split.booster(),
                reentry.vessel(),
                null);
        FlightLoop recovered = booster.recover();
        if (recovered.presence() != FlightPresence.BLOCKS_ON_PAD || recovered.recovery() == null) {
            throw new IllegalStateException("Recovery must sit the booster back as blocks");
        }
        RecoveryPlan plan = recovered.recovery();
        if (plan.includesKind(VesselPartKind.PAD) || !plan.includes(VesselPartCatalog.ENGINE.id())) {
            throw new IllegalStateException("Recovered plan is engine on the existing pad");
        }
        if (plan.placements().isEmpty()) {
            throw new IllegalStateException("WorldStageRecovery.place needs a non-empty plan");
        }

        return new Result(
                flown.leftThePad(),
                hasKind(flown.padLeft(), VesselPartKind.PAD),
                mapped.presence() == FlightPresence.ORBIT_MAP,
                reentry.presence() == FlightPresence.WORLD_ENTITY,
                recovered.recovery() != null,
                !split.booster().isEmpty(),
                plan.placements().size());
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

    private static VesselBlockGrid airframe() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }

    private static VesselBlockGrid onMount() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.MOUNT, BlockFace.UP),
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
