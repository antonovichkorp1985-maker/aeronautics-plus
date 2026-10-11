package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.GridSplit;
import dev.leeeonidys.aeronauticsplus.space.compile.RadialSplit;
import dev.leeeonidys.aeronauticsplus.space.compile.RecoveryPlan;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.StationPlan;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import java.util.ArrayList;
import java.util.List;

/**
 * Pad blocks → world entity → 20 km map → parked station blocks → world again → pad.
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
        if (presence == FlightPresence.STATION_BLOCKS) {
            if (WorldHandoff.inWorld(vessel.orbit()) || !StationHandoff.parked(vessel.orbit())) {
                throw new IllegalStateException("Station blocks need a parked orbit on the map");
            }
            if (recovery != null) {
                throw new IllegalStateException("Station is not a pad recovery");
            }
        }
    }

    public static FlightLoop sit(VesselBlockGrid grid) {
        return sit(grid, SpaceBodies.pad());
    }

    public static FlightLoop sit(VesselBlockGrid grid, OrbitState pad) {
        if (grid == null || grid.isEmpty()) {
            throw new IllegalArgumentException("Pad stack is required");
        }
        if (pad == null) {
            throw new IllegalArgumentException("Pad orbit is required");
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
        VesselState vessel = compilation.toVesselState("flight", pad);
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
        Attitude radial = Attitude.pointing(Vector3d.UNIT_Y, vessel.orbit().radialUnit());
        return new FlightLoop(
                FlightPresence.WORLD_ENTITY,
                stacked,
                padLeft,
                airframe,
                vessel.withAttitude(radial),
                null);
    }

    public FlightLoop withVessel(VesselState next) {
        if (presence == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("Blocks on the pad do not swap to the map");
        }
        if (next == null) {
            throw new IllegalArgumentException("Vessel is required");
        }
        return new FlightLoop(presenceAfter(next.orbit()), stacked, padLeft, flying, next, null);
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
        FlightPresence splitPresence = presence == FlightPresence.STATION_BLOCKS
                ? FlightPresence.ORBIT_MAP
                : presence;
        return new FlightSplit(
                new FlightLoop(splitPresence, stacked, padLeft, split.continuing(), stages.continuing(), null),
                new FlightLoop(splitPresence, stacked, padLeft, split.booster(), stages.booster(), null));
    }

    /**
     * Fire horizontal pyro rings: sides become their own vehicles, core keeps going.
     * Does not wait for the 20 km line.
     */
    public RadialFlightSplit fireRadial() {
        if (presence == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("Radial pyro fires in flight, not on the pad");
        }
        RadialSplit split = VesselBlockCompiler.fireRadial(flying);
        StrapSplit stages = VesselDynamics.dropStrapOns(vessel);
        if (split.sides().size() != stages.sides().size()) {
            throw new IllegalStateException("Radial grids and strap-on stages do not match");
        }
        FlightPresence splitPresence = presence == FlightPresence.STATION_BLOCKS
                ? FlightPresence.ORBIT_MAP
                : presence;
        List<FlightLoop> sides = new ArrayList<>();
        for (int i = 0; i < split.sides().size(); i++) {
            sides.add(new FlightLoop(
                    splitPresence, stacked, padLeft, split.sides().get(i), stages.sides().get(i), null));
        }
        return new RadialFlightSplit(
                new FlightLoop(splitPresence, stacked, padLeft, split.core(), stages.core(), null),
                sides);
    }

    public boolean hasPyroRing() {
        for (VesselBlockOccupant occupant : flying.occupants()) {
            if (occupant.spec().kind() == VesselPartKind.SEPARATOR) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRadialRing() {
        for (VesselBlockOccupant occupant : flying.occupants()) {
            if (occupant.spec().kind() == VesselPartKind.SEPARATOR && occupant.facing().horizontal()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFairing() {
        for (StageState stage : vessel.stages()) {
            if (stage.hasFairing()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Commanded shroud drop. Illegal below 50 km until ChemMod q exists.
     * Payload stays. Parking orbit is a legal drop.
     */
    public FlightLoop dropFairing() {
        if (presence == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("Fairing drops in flight, not on the pad");
        }
        VesselState dropped = VesselDynamics.jettisonFairingAboveDenseAir(vessel);
        VesselBlockGrid bare = stripKind(flying, VesselPartKind.FAIRING);
        return new FlightLoop(
                presenceAfter(dropped.orbit()), stacked, padLeft, bare, dropped, null);
    }

    public boolean hasRcs() {
        return !vessel.activeStage().rcsThrusters().isEmpty();
    }

    public boolean hasHabitat() {
        return vessel.activeStage().hasHabitat();
    }

    public boolean hasController() {
        return vessel.activeStage().hasController();
    }

    /**
     * RCS cluster: ACS, not the main engine. Fires wherever the stack is, including the map.
     * ChemMod air is not this pulse.
     */
    public FlightLoop fireRcs(double seconds) {
        if (presence == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("RCS fires in flight, not on the pad");
        }
        VesselState next = VesselDynamics.fireRcs(vessel, seconds);
        return new FlightLoop(presenceAfter(next.orbit()), stacked, padLeft, flying, next, null);
    }

    public boolean strapOnsDry() {
        if (!vessel.hasStrapOns()) {
            return false;
        }
        for (StageState stage : vessel.stages()) {
            if (stage.strapOn() && stage.propellantMassKg() > 1.0e-9) {
                return false;
            }
        }
        return true;
    }

    public FlightLoop withOrbit(OrbitState nextOrbit) {
        if (presence == FlightPresence.BLOCKS_ON_PAD) {
            throw new IllegalStateException("Blocks on the pad do not swap to the map");
        }
        if (nextOrbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        VesselState next = vessel.withOrbit(nextOrbit);
        return new FlightLoop(presenceAfter(next.orbit()), stacked, padLeft, flying, next, null);
    }

    /**
     * Player command, not circularization. Burns and unmanned sats stay on the map.
     * The control seat is required, not sufficient. ISS crawl, or CM↔LM while still
     * in lunar orbit — this button. Moon-as-planet is a later world.
     */
    public FlightLoop occupyStation() {
        if (presence != FlightPresence.ORBIT_MAP) {
            throw new IllegalStateException("Station blocks occupy from the Kepler map");
        }
        if (!StationHandoff.parked(vessel.orbit())) {
            throw new IllegalStateException("Station blocks need a parked circular orbit");
        }
        if (hasFairing()) {
            throw new IllegalStateException("Station is still under the fairing");
        }
        if (!hasController()) {
            throw new IllegalStateException("Station needs a control seat");
        }
        StationPlan.of(flying);
        return new FlightLoop(
                FlightPresence.STATION_BLOCKS, stacked, padLeft, flying, vessel, null);
    }

    /** Leave the walkable grid; Kepler map keeps the same orbit. */
    public FlightLoop leaveStation() {
        if (presence != FlightPresence.STATION_BLOCKS) {
            throw new IllegalStateException("Leave station only from station blocks");
        }
        return new FlightLoop(
                FlightPresence.ORBIT_MAP, stacked, padLeft, flying, vessel, null);
    }

    public StationPlan station() {
        if (presence != FlightPresence.STATION_BLOCKS) {
            return null;
        }
        return StationPlan.of(flying);
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

    private FlightPresence presenceAfter(OrbitState orbit) {
        if (WorldHandoff.inWorld(orbit)) {
            return FlightPresence.WORLD_ENTITY;
        }
        if (presence == FlightPresence.STATION_BLOCKS && StationHandoff.parked(orbit)) {
            return FlightPresence.STATION_BLOCKS;
        }
        return FlightPresence.ORBIT_MAP;
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

    private static VesselBlockGrid stripKind(VesselBlockGrid grid, VesselPartKind kind) {
        List<VesselBlockOccupant> occupants = new ArrayList<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().kind() != kind) {
                occupants.add(occupant);
            }
        }
        return new VesselBlockGrid(occupants);
    }
}
