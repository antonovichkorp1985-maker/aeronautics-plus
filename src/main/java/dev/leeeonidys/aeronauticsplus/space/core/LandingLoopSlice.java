package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;

/**
 * Compiler landing legs make a Falcon suicide-burn a landing, not NO_LEGS.
 * Grid fins still wait on ChemMod air.
 */
public final class LandingLoopSlice {
    private LandingLoopSlice() {
    }

    public record Result(
            boolean compilerLegs,
            boolean hopperLanded,
            boolean leglessNotLanding,
            boolean recoveredOnPad) {
    }

    public static Result execute() {
        VesselCompilation withLegs = VesselBlockCompiler.analyze(hopper(true));
        if (!withLegs.isLaunchable() || !withLegs.stages().get(0).hasLandingLegs()) {
            throw new IllegalStateException("Landing legs must compile onto the hopper stage");
        }
        VesselCompilation noLegs = VesselBlockCompiler.analyze(hopper(false));
        if (!noLegs.isLaunchable() || noLegs.stages().get(0).hasLandingLegs()) {
            throw new IllegalStateException("A hopper without the legs block must not report legs");
        }

        LandingOutcome landed = VesselDynamics.attemptLanding(
                radial(withLegs.toVesselState("legged", falling())), 60.0, 0.05);
        if (!landed.landed() || landed.has(FlightFault.IMPACT) || landed.has(FlightFault.NO_LEGS)) {
            throw new IllegalStateException("Legged hopper must suicide-burn to the pad: " + landed.faultText());
        }

        LandingOutcome wreck = VesselDynamics.attemptLanding(
                radial(noLegs.toVesselState("bare", falling())), 60.0, 0.05);
        if (!wreck.has(FlightFault.NO_LEGS) || wreck.landed()) {
            throw new IllegalStateException("No legs is not a landing");
        }

        FlightLoop recovered = FlightLoop.sit(hopper(true)).liftoff()
                .withOrbit(landed.vessel().orbit())
                .recover();
        if (recovered.presence() != FlightPresence.BLOCKS_ON_PAD || recovered.recovery() == null) {
            throw new IllegalStateException("Landed hopper returns as blocks under 20 km");
        }

        return new Result(true, landed.landed(), wreck.has(FlightFault.NO_LEGS), recovered.recovery() != null);
    }

    private static VesselState radial(VesselState vessel) {
        return vessel.withAttitude(Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X));
    }

    private static OrbitState falling() {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + 2_000.0;
        return new OrbitState(
                earth,
                new Vector3d(radius, 0.0, 0.0),
                new Vector3d(-80.0, 0.0, 0.0),
                0.0);
    }

    private static VesselBlockGrid hopper(boolean legs) {
        if (legs) {
            return VesselBlockGrid.of(
                    new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                    new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                    new VesselBlockOccupant(new GridPos(1, 1, 0), VesselPartCatalog.LEGS, BlockFace.UP),
                    new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                    new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
        }
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }
}
