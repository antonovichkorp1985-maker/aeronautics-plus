package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;

/**
 * 20 km geometric altitude: world below, Kepler map at and above.
 * Pyro does not wait for this line.
 */
public final class HandoffSlice {
    private HandoffSlice() {
    }

    public record Result(
            boolean padInWorld,
            boolean hopperInWorld,
            boolean edgeOnMap,
            boolean parkingOnMap,
            boolean reentryInWorld) {
    }

    public static Result execute() {
        OrbitState pad = SpaceBodies.pad();
        if (!WorldHandoff.inWorld(pad) || WorldHandoff.onMap(pad)) {
            throw new IllegalStateException("Pad must stay in the Overworld");
        }

        OrbitState hopper = atAltitude(2_000.0);
        if (!WorldHandoff.inWorld(hopper)) {
            throw new IllegalStateException("A 2 km hopper must stay in the world");
        }

        OrbitState justBelow = atAltitude(WorldHandoff.MAP_ALTITUDE_METERS - 1.0);
        if (!WorldHandoff.inWorld(justBelow)) {
            throw new IllegalStateException("19 999 m must still be the world");
        }

        OrbitState edge = atAltitude(WorldHandoff.MAP_ALTITUDE_METERS);
        if (!WorldHandoff.onMap(edge) || WorldHandoff.inWorld(edge)) {
            throw new IllegalStateException("20 km is the map, not the world");
        }

        OrbitState parking = SpaceBodies.parkingOrbit();
        if (!WorldHandoff.onMap(parking)) {
            throw new IllegalStateException("Parking orbit must be on the map");
        }

        OrbitState reentry = atAltitude(19_000.0);
        if (!WorldHandoff.inWorld(reentry)) {
            throw new IllegalStateException("Coming back under 20 km must return to the world");
        }

        return new Result(
                WorldHandoff.inWorld(pad),
                WorldHandoff.inWorld(hopper),
                WorldHandoff.onMap(edge),
                WorldHandoff.onMap(parking),
                WorldHandoff.inWorld(reentry));
    }

    private static OrbitState atAltitude(double altitudeMeters) {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + altitudeMeters;
        return new OrbitState(earth, new Vector3d(radius, 0.0, 0.0), Vector3d.ZERO, 0.0);
    }
}
