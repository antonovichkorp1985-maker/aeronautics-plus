package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;

/**
 * Compiled hopper with landing legs: skip boostback and a downrange hop is IMPACT;
 * kill the horizon, then suicide-burn, then sit back as blocks under 20 km.
 * Map stays coast. Grid fins wait on ChemMod air.
 */
public final class BoostbackLoopSlice {
    private BoostbackLoopSlice() {
    }

    public record Result(
            boolean compilerLegs,
            boolean skippedImpact,
            boolean killedDownrange,
            boolean hopperLanded,
            boolean recoveredOnPad) {
    }

    public static Result execute() {
        VesselCompilation compiled = VesselBlockCompiler.analyze(hopper());
        if (!compiled.isLaunchable() || !compiled.stages().get(0).hasLandingLegs()) {
            throw new IllegalStateException("Boostback hopper must compile with landing legs");
        }

        VesselState downrange = radial(compiled.toVesselState("boostback-hopper", downrangeOrbit()));
        double startHoriz = downrange.orbit().horizontalSpeedMetersPerSecond();
        if (!(startHoriz > 300.0)) {
            throw new IllegalStateException("Boostback hopper must start downrange, horiz=" + startHoriz);
        }

        LandingOutcome skipped = VesselDynamics.attemptLanding(downrange, 90.0, 0.05);
        if (!skipped.has(FlightFault.IMPACT) || skipped.landed()) {
            throw new IllegalStateException("Skipping boostback must hit, not land");
        }

        BoostbackOutcome boost = VesselDynamics.attemptBoostback(downrange, 40.0, 0.05);
        if (!boost.killedDownrange()) {
            throw new IllegalStateException("Compiled hopper must kill downrange speed: " + boost.faultText()
                    + " horiz=" + boost.endHorizontal());
        }

        LandingOutcome landed = VesselDynamics.attemptLanding(boost.vessel(), 90.0, 0.05);
        if (!landed.landed() || landed.has(FlightFault.IMPACT) || landed.has(FlightFault.NO_LEGS)) {
            throw new IllegalStateException("After boostback the hopper must land: " + landed.faultText());
        }

        FlightLoop recovered = FlightLoop.sit(hopper()).liftoff()
                .withOrbit(landed.vessel().orbit())
                .recover();
        if (recovered.presence() != FlightPresence.BLOCKS_ON_PAD || recovered.recovery() == null) {
            throw new IllegalStateException("Landed hopper after boostback returns as blocks under 20 km");
        }

        return new Result(
                true,
                skipped.has(FlightFault.IMPACT),
                boost.killedDownrange(),
                landed.landed(),
                recovered.recovery() != null);
    }

    private static VesselState radial(VesselState vessel) {
        return vessel.withAttitude(Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X));
    }

    private static OrbitState downrangeOrbit() {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + 8_000.0;
        return new OrbitState(
                earth,
                new Vector3d(radius, 0.0, 0.0),
                new Vector3d(0.0, 0.0, 400.0),
                0.0);
    }

    private static VesselBlockGrid hopper() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(1, 1, 0), VesselPartCatalog.LEGS, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }
}
