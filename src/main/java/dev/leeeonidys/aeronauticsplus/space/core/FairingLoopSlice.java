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
 * Registered fairing and payload fly; the shroud drops at 50 km by altitude.
 * 20 km is the map line, not jettison. ChemMod q is not this gate.
 */
public final class FairingLoopSlice {
    private FairingLoopSlice() {
    }

    public record Result(
            boolean compilerShroud,
            boolean heldAtHandoff,
            boolean droppedAtFifty,
            boolean payloadStayed,
            boolean recoveredWithoutFairing,
            boolean mapDropsFairing) {
    }

    public static Result execute() {
        VesselCompilation compiled = VesselBlockCompiler.analyze(shrouded());
        if (!compiled.isLaunchable()
                || !compiled.stages().get(0).hasFairing()
                || !compiled.stages().get(0).hasPayload()) {
            throw new IllegalStateException("Pad stack with payload and fairing must compile:\\n"
                    + compiled.diagnosticText());
        }

        FlightLoop atHandoff = FlightLoop.sit(shrouded()).liftoff()
                .withOrbit(atAltitude(WorldHandoff.MAP_ALTITUDE_METERS));
        if (atHandoff.presence() != FlightPresence.ORBIT_MAP || !atHandoff.hasFairing()) {
            throw new IllegalStateException("Fairing stays at the 20 km map line");
        }
        try {
            atHandoff.dropFairing();
            throw new IllegalStateException("Fairing must not drop at 20 km");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains("50")) {
                throw exception;
            }
        }

        FlightLoop tooLow = FlightLoop.sit(shrouded()).liftoff()
                .withOrbit(atAltitude(19_000.0));
        try {
            tooLow.dropFairing();
            throw new IllegalStateException("Fairing must not drop in the Overworld");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains("50")) {
                throw exception;
            }
        }

        FlightLoop dropped = FlightLoop.sit(shrouded()).liftoff()
                .withOrbit(atAltitude(Aerodynamics.FAIRING_MIN_ALTITUDE_METERS))
                .dropFairing();
        if (dropped.hasFairing() || !dropped.vessel().activeStage().hasPayload()) {
            throw new IllegalStateException("50 km drop must lose the shroud and keep the payload");
        }
        for (var occupant : dropped.flying().occupants()) {
            if (occupant.spec().kind() == VesselPartKind.FAIRING) {
                throw new IllegalStateException("Flying grid must not keep the fairing block");
            }
        }

        FlightLoop recovered = dropped.withOrbit(atAltitude(2_000.0)).recover();
        if (recovered.recovery() == null
                || recovered.recovery().includesKind(VesselPartKind.FAIRING)
                || !recovered.recovery().includesKind(VesselPartKind.PAYLOAD)) {
            throw new IllegalStateException("Recovered blocks keep the payload and not the fairing");
        }

        OrbitMap map = new OrbitMap();
        map.enter(FlightLoop.sit(shrouded()).liftoff().withOrbit(atAltitude(80_000.0)));
        map.advance(1.0);
        if (map.size() != 1 || map.snapshot().get(0).hasFairing()) {
            throw new IllegalStateException("Map coast at 80 km must drop the fairing");
        }

        return new Result(true, true, true, true, true, true);
    }

    private static OrbitState atAltitude(double altitudeMeters) {
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
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.PAYLOAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 5, 0), VesselPartCatalog.FAIRING, BlockFace.UP));
    }
}
