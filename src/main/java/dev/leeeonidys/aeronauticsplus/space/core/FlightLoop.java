package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.GridSplit;
import dev.leeeonidys.aeronauticsplus.space.compile.RecoveryPlan;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import java.util.ArrayList;
import java.util.List;

/**
 * Pad blocks → world entity → 20 km map → world again → blocks on the pad.
 * Pyro does not wait for the handoff line. ChemMod air is not this loop.
 */
public record FlightLoop(
        FlightPresence presence,
        VesselBlockGrid stacked,
        VesselBlockGrid padLeft,
        VesselBlockGrid flying,
        VesselState vessel,
        RecoveryPlan recovery) {
    public FlightLoop {
        if (presence == null || stacked == null || padLeft == null || flying == null) {
            throw new IllegalArgumentException("Flight loop grids and presence are required");
        }
        if (vessel == null) {
            throw new IllegalArgumentException("Flight loop needs a compiled vessel");
        }
        if (presence == FlightPresence.ORBIT_MAP && WorldHandoff.inWorld(vessel.orbit())) {
            throw new IllegalStateException("Orbit map presence requires altitude at or above 20 km");
        }
        if (presence == FlightPresence.WORLD_ENTITY && WorldHandoff.onMap(vessel.orbit())) {
            throw new IllegalStateException("World entity presence requires altitude below 20 km");
        }
    }

    public static FlightLoop sit(VesselBlockGrid grid) {
        if (grid == null || grid.isEmpty()) {
            throw new IllegalArgumentException("Pad stack is required");
        }
        VesselCompilation compilation = VesselBlockCompiler.analyze(grid);
        if (compilation.diagnosticText().contains("ON_TRANSPORTER")) {
            throw new IllegalStateException("Packet on the train mount cannot leave the pad");
        }
        if (!compilation.isLaunchable()) {
            String text = compilation.diagnosticText();
            throw new IllegalStateException(text.isBlank()
                    ? "Stack is not launchable"
                    : "Stack is not launchable:\n" + text);
        }
        VesselState vessel = compilation.toVesselState("flight", SpaceBodies.pad());
        return new FlightLoop(
                FlightPresence.BLOCKS_ON_PAD,
                grid,
                filter(grid, true),
                new VesselBlockGrid(List.of()),
                vessel,
                null);
    }

    public FlightLoop liftoff() {
        if (presence != FlightPresence.BLOCKS_ON_PAD || recovery != null) {
            throw new IllegalStateException("Liftoff starts from blocks on the pad");
        }
        if (padLeft.isEmpty()) {
            throw new IllegalStateException("Liftoff needs an AP pad; the table stays on the ground");
        }
        VesselBlockGrid airframe = filter(stacked, false);
        if (airframe.isEmpty()) {
            throw new IllegalStateException("Nothing to fly; pad is not a vehicle");
        }
        if (!WorldHandoff.inWorld(vessel.orbit())) {
            throw new IllegalStateException("Pad altitude must stay in the Overworld");
        }
        return new FlightLoop(
                FlightPresence.WORLD_ENTITY,
                stacked,
                padLeft,
                airframe,
                vessel,
                null);
    }

    public FlightLoop withVessel(VesselState next) {
        if (presence == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("Blocks on the pad do not swap to the map");
        }
        if (next == null) {
            throw new IllegalArgumentException("Vessel is required");
        }
        FlightPresence nextPresence = WorldHandoff.inWorld(next.orbit())
                ? FlightPresence.WORLD_ENTITY
                : FlightPresence.ORBIT_MAP;
        return new FlightLoop(nextPresence, stacked, padLeft, flying, next, null);
    }

    /** One physics step while the pile is already the world entity or on the map. */
    public FlightLoop advance(double dt) {
        AscentOutcome outcome = VesselDynamics.advanceAscent(
                vessel, Atmosphere.earth(), dt, Aerodynamics.ROCKET_CD, 1.0);
        return withVessel(outcome.vessel());
    }

    /** Coast without thrust — burnout must not hang in the air. */
    public FlightLoop coast(double dt) {
        if (presence == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("Blocks on the pad do not coast");
        }
        if (!(dt > 0.0) || !Double.isFinite(dt)) {
            throw new IllegalArgumentException("Coast step must be finite and positive");
        }
        return withVessel(VesselDynamics.propagate(vessel, dt, Math.min(dt, 0.1)));
    }

    /**
     * Fire the pyro ring: parts split like KSP, wherever the stack is.
     * Upper keeps going; booster is its own vehicle on the same trajectory.
     */
    public FlightSplit firePyro() {
        if (presence == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("Pyro fires in flight, not on the pad");
        }
        GridSplit split = VesselBlockCompiler.firePyro(flying);
        if (!vessel.canSeparateActive()) {
            throw new IllegalStateException("Pyro ring did not leave an upper stage");
        }
        StageSplit stages = VesselDynamics.splitActiveStage(vessel);
        return new FlightSplit(
                new FlightLoop(presence, stacked, padLeft, split.continuing(), stages.continuing(), null),
                new FlightLoop(presence, stacked, padLeft, split.booster(), stages.booster(), null));
    }

    public boolean hasPyroRing() {
        for (VesselBlockOccupant occupant : flying.occupants()) {
            if (occupant.spec().kind() == VesselPartKind.SEPARATOR) {
                return true;
            }
        }
        return false;
    }

    public FlightLoop withOrbit(OrbitState nextOrbit) {
        if (presence == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("Blocks on the pad do not swap to the map");
        }
        if (nextOrbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        VesselState next = vessel.withOrbit(nextOrbit);
        FlightPresence nextPresence = WorldHandoff.inWorld(next.orbit())
                ? FlightPresence.WORLD_ENTITY
                : FlightPresence.ORBIT_MAP;
        return new FlightLoop(nextPresence, stacked, padLeft, flying, next, null);
    }

    public FlightLoop recover() {
        if (presence != FlightPresence.WORLD_ENTITY) {
            throw new IllegalStateException("Recovered booster returns as blocks only below 20 km");
        }
        if (flying.isEmpty()) {
            throw new IllegalStateException("No flying parts to put back on the pad");
        }
        RecoveryPlan plan = RecoveryPlan.sitOnPad(flying);
        return new FlightLoop(
                FlightPresence.BLOCKS_ON_PAD,
                stacked,
                padLeft,
                flying,
                vessel,
                plan);
    }

    public boolean leftThePad() {
        return presence != FlightPresence.BLOCKS_ON_PAD || recovery != null;
    }

    private static VesselBlockGrid filter(VesselBlockGrid grid, boolean pad) {
        List<VesselBlockOccupant> occupants = new ArrayList<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            boolean isPad = occupant.spec().kind() == VesselPartKind.PAD;
            if (isPad == pad) {
                occupants.add(occupant);
            }
        }
        return new VesselBlockGrid(occupants);
    }
}
