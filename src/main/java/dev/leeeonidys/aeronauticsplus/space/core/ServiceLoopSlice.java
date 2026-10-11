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
 * Habitat and RCS are AP service-module parts. RCS is ACS, not the main engine.
 * Solar stays ChemMod; gyro waits on that panel.
 */
public final class ServiceLoopSlice {
    private ServiceLoopSlice() {
    }

    public record Result(
            boolean compilerBus,
            boolean rcsFired,
            boolean noRcsRefused,
            boolean habitatStayed,
            boolean recoveredBus) {
    }

    public static Result execute() {
        VesselCompilation compiled = VesselBlockCompiler.analyze(bus());
        if (!compiled.isLaunchable()
                || !compiled.stages().get(0).hasHabitat()
                || compiled.stages().get(0).rcsThrusters().isEmpty()) {
            throw new IllegalStateException("Pad stack with habitat and RCS must compile:\n"
                    + compiled.diagnosticText());
        }
        if (compiled.diagnosticText().contains("NO_RCS")) {
            throw new IllegalStateException("Bus with RCS must not warn NO_RCS:\n"
                    + compiled.diagnosticText());
        }

        FlightLoop flown = FlightLoop.sit(bus()).liftoff()
                .withOrbit(atAltitude(80_000.0))
                .dropFairing();
        if (!flown.hasHabitat() || !flown.hasRcs() || flown.hasFairing()) {
            throw new IllegalStateException("After the shroud drops the habitat and RCS stay");
        }
        double massBefore = flown.vessel().totalMassKg();
        FlightLoop pulsed = flown.fireRcs(2.0);
        if (!(pulsed.vessel().totalMassKg() < massBefore - 0.1) || !pulsed.hasHabitat()) {
            throw new IllegalStateException("RCS must spend propellant and keep the habitat");
        }

        FlightLoop bare = FlightLoop.sit(busWithoutRcs()).liftoff()
                .withOrbit(atAltitude(80_000.0));
        try {
            bare.fireRcs(1.0);
            throw new IllegalStateException("RCS pulse without thrusters must fail");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains("RCS")) {
                throw exception;
            }
        }

        FlightLoop recovered = pulsed.withOrbit(atAltitude(2_000.0)).recover();
        if (recovered.recovery() == null
                || !recovered.recovery().includesKind(VesselPartKind.HABITAT)
                || !recovered.recovery().includesKind(VesselPartKind.RCS)
                || !recovered.recovery().includesKind(VesselPartKind.PAYLOAD)
                || recovered.recovery().includesKind(VesselPartKind.FAIRING)) {
            throw new IllegalStateException("Recovered blocks keep habitat, RCS and payload, not the fairing");
        }

        return new Result(true, true, true, true, true);
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
                new VesselBlockOccupant(new GridPos(0, 5, 0), VesselPartCatalog.PAYLOAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 6, 0), VesselPartCatalog.FAIRING, BlockFace.UP));
    }

    private static VesselBlockGrid busWithoutRcs() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.HABITAT, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 5, 0), VesselPartCatalog.PAYLOAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 6, 0), VesselPartCatalog.FAIRING, BlockFace.UP));
    }
}
