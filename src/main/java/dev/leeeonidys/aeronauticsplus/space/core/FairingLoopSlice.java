package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;

/**
 * Fairing covers the apparatus. Payload is a property, not a block.
 * Drop is commanded: illegal in dense air, legal on parking orbit (satellites).
 * 20 km is the map line. Coast does not peel the shroud.
 */
public final class FairingLoopSlice {
    private FairingLoopSlice() {
    }

    public record Result(
            boolean compilerShroud,
            boolean heldAtHandoff,
            boolean heldAtFifty,
            boolean droppedOnCommand,
            boolean droppedOnOrbit,
            boolean payloadStayed,
            boolean recoveredWithoutFairing,
            boolean mapKeepsFairing) {
    }

    public static Result execute() {
        VesselCompilation compiled = VesselBlockCompiler.analyze(shrouded());
        if (!compiled.isLaunchable()
                || !compiled.stages().get(0).hasFairing()
                || !compiled.stages().get(0).hasPayload()) {
            throw new IllegalStateException("Pad stack with habitat under the fairing must compile:\n"
                    + compiled.diagnosticText());
        }

        FlightLoop atHandoff = FlightLoop.sit(shrouded()).liftoff()
                .withOrbit(atAltitude(WorldHandoff.MAP_ALTITUDE_METERS));
        if (atHandoff.presence() != FlightPresence.ORBIT_MAP || !atHandoff.hasFairing()) {
            throw new IllegalStateException("Fairing stays at the 20 km map line");
        }
        try {
            atHandoff.dropFairing();
            throw new IllegalStateException("Fairing must not drop at 20 km while q is still high");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains(FlightFault.FAIRING_ATMOSPHERE)) {
                throw exception;
            }
        }

        FlightLoop tooLow = FlightLoop.sit(shrouded()).liftoff()
                .withOrbit(atAltitude(19_000.0));
        try {
            tooLow.dropFairing();
            throw new IllegalStateException("Fairing must not drop in dense air");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains(FlightFault.FAIRING_ATMOSPHERE)) {
                throw exception;
            }
        }

        FlightLoop atFifty = FlightLoop.sit(shrouded()).liftoff()
                .withOrbit(quiet(Aerodynamics.FAIRING_MIN_ALTITUDE_METERS));
        if (!atFifty.hasFairing()) {
            throw new IllegalStateException("Crossing 50 km must not peel the shroud by itself");
        }

        FlightLoop dropped = atFifty.dropFairing();
        if (dropped.hasFairing() || !dropped.vessel().activeStage().hasPayload()) {
            throw new IllegalStateException("Commanded drop must lose the shroud and keep the apparatus");
        }
        for (var occupant : dropped.flying().occupants()) {
            if (occupant.spec().kind() == VesselPartKind.FAIRING) {
                throw new IllegalStateException("Flying grid must not keep the fairing block");
            }
        }

        FlightLoop orbital = FlightLoop.sit(shrouded()).liftoff()
                .withOrbit(SpaceBodies.parkingOrbit());
        if (!orbital.hasFairing()) {
            throw new IllegalStateException("Parking orbit may still wear the fairing");
        }
        FlightLoop deployed = orbital.dropFairing();
        if (deployed.hasFairing() || !deployed.vessel().activeStage().hasPayload()) {
            throw new IllegalStateException("Satellite deploy on orbit must drop the shroud and keep the apparatus");
        }

        FlightLoop recovered = dropped.withOrbit(atAltitude(2_000.0)).recover();
        if (recovered.recovery() == null
                || recovered.recovery().includesKind(VesselPartKind.FAIRING)
                || !recovered.recovery().includesKind(VesselPartKind.CONTROLLER)) {
            throw new IllegalStateException("Recovered blocks keep the seat and not the fairing");
        }

        OrbitMap map = new OrbitMap();
        FlightLoop coastMap = FlightLoop.sit(shrouded()).liftoff().withOrbit(atAltitude(80_000.0));
        coastMap = coastMap.withVessel(coastMap.vessel().withLiveThrottle(0.0));
        map.enter(coastMap);
        map.advance(1.0);
        if (map.size() != 1 || !map.snapshot().get(0).hasFairing()) {
            throw new IllegalStateException("Map coast must keep the fairing until commanded");
        }

        return new Result(true, true, true, true, true, true, true, true);
    }

    private static OrbitState atAltitude(double altitudeMeters) {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + altitudeMeters;
        return new OrbitState(earth, new Vector3d(radius, 0.0, 0.0), new Vector3d(0.0, 0.0, 1_000.0), 0.0);
    }

    /** Coasting: q ≈ 0 so the commanded drop is legal. */
    private static OrbitState quiet(double altitudeMeters) {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + altitudeMeters;
        return new OrbitState(earth, new Vector3d(radius, 0.0, 0.0), Vector3d.ZERO, 0.0);
    }

    private static VesselBlockGrid shrouded() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.FAIRING, BlockFace.UP));
    }
}
