package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.DockingSlice;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;

/**
 * Docking ring is AP. Capture faces look at each other.
 * Radiator and solar stay ChemMod.
 */
public final class DockingLoopSlice {
    private DockingLoopSlice() {
    }

    public record Result(
            boolean compilerBus,
            boolean facingPorts,
            boolean stayedAfterFairing,
            boolean recovered) {
    }

    public static Result execute() {
        VesselCompilation compiled = VesselBlockCompiler.analyze(bus());
        if (!compiled.isLaunchable()
                || !compiled.stages().get(0).hasHabitat()
                || !compiled.stages().get(0).hasDocking()) {
            throw new IllegalStateException("Pad stack with docking must compile:\n"
                    + compiled.diagnosticText());
        }
        if (compiled.diagnosticText().contains("NO_DOCKING")) {
            throw new IllegalStateException("Bus with a port must not warn NO_DOCKING:\n"
                    + compiled.diagnosticText());
        }

        int links = 0;
        for (VesselConnection connection : VesselBlockCompiler.compile(DockingSlice.facingPorts()).connections()) {
            if (connection.kind() == VesselConnection.ConnectionKind.DOCKING) {
                links++;
            }
        }
        if (links != 1) {
            throw new IllegalStateException("Two ports looking at each other must share one docking link, got "
                    + links);
        }

        FlightLoop flown = FlightLoop.sit(bus()).liftoff()
                .withOrbit(atAltitude(80_000.0))
                .dropFairing();
        if (!flown.vessel().activeStage().hasDocking() || flown.hasFairing()) {
            throw new IllegalStateException("After the shroud drops the docking port stays");
        }

        FlightLoop recovered = flown.withOrbit(atAltitude(2_000.0)).recover();
        if (recovered.recovery() == null
                || !recovered.recovery().includesKind(VesselPartKind.DOCKING)
                || !recovered.recovery().includesKind(VesselPartKind.HABITAT)
                || recovered.recovery().includesKind(VesselPartKind.FAIRING)
                || recovered.recovery().includesKind(VesselPartKind.RADIATOR)) {
            throw new IllegalStateException("Recovered blocks keep the port and habitat, not the fairing");
        }

        return new Result(true, true, true, true);
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
                new VesselBlockOccupant(new GridPos(1, 2, 0), VesselPartCatalog.RCS, BlockFace.EAST),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.HABITAT, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 4, -1), VesselPartCatalog.DOCKING, BlockFace.NORTH),
                new VesselBlockOccupant(new GridPos(0, 5, 0), VesselPartCatalog.FAIRING, BlockFace.UP));
    }
}
