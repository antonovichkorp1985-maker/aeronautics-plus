package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Kepler-map vehicles. Coast only — burns stay on the world entity.
 * Crossing back under 20 km returns WORLD_ENTITY for spawn.
 */
public final class OrbitMap {
    private final List<FlightLoop> vessels = new ArrayList<>();

    public void enter(FlightLoop loop) {
        if (loop == null || loop.presence() != FlightPresence.ORBIT_MAP) {
            throw new IllegalStateException("Only a map vehicle enters the orbit map");
        }
        vessels.add(loop);
    }

    public int size() {
        return vessels.size();
    }

    public List<FlightLoop> snapshot() {
        return List.copyOf(vessels);
    }

    /** Coast every vehicle. Those that fall under 20 km are removed and returned. */
    public List<FlightLoop> advance(double dt) {
        if (!(dt > 0.0) || !Double.isFinite(dt)) {
            throw new IllegalArgumentException("Map step must be finite and positive");
        }
        List<FlightLoop> still = new ArrayList<>();
        List<FlightLoop> reentered = new ArrayList<>();
        for (FlightLoop loop : vessels) {
            VesselState next = VesselDynamics.propagate(loop.vessel(), dt, Math.min(dt, 0.1));
            FlightLoop stepped = loop.withVessel(next);
            if (stepped.presence() == FlightPresence.WORLD_ENTITY) {
                reentered.add(stepped);
            } else {
                still.add(stepped);
            }
        }
        vessels.clear();
        vessels.addAll(still);
        return List.copyOf(reentered);
    }
}
