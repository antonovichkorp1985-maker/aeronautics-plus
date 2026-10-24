package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Kepler-map vehicles. Coast only — burns stay on the world entity.
 * Coast uses the ISA column (same {@link FlightLoop#coast}), not vacuum Kepler.
 * 20 km still has air; 200 km parking is Kepler. Not {@code molesAt}.
 * Crossing back under 20 km returns WORLD_ENTITY for spawn.
 * Fairing stays until commanded. Coast does not peel the shroud.
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

    /** Capture the first pair of map vehicles whose ports look at each other. */
    public boolean capture() {
        for (int i = 0; i < vessels.size(); i++) {
            for (int j = i + 1; j < vessels.size(); j++) {
                FlightLoop a = vessels.get(i);
                FlightLoop b = vessels.get(j);
                double dist = a.vessel().orbit().positionMeters()
                        .subtract(b.vessel().orbit().positionMeters())
                        .magnitude();
                try {
                    FlightLoop joined = a.dock(b, dist);
                    vessels.set(i, joined);
                    vessels.remove(j);
                    return true;
                } catch (RuntimeException ignored) {
                    // not this pair
                }
            }
        }
        return false;
    }

    /**
     * Player command: take the first parked habitat off the map into station blocks.
     * Transfers and unmanned sats stay. Fairing must already be gone.
     */
    public FlightLoop takeParkedHabitat() {
        for (int i = 0; i < vessels.size(); i++) {
            FlightLoop loop = vessels.get(i);
            if (loop.hasFairing()) {
                try {
                    loop = loop.dropFairing();
                } catch (IllegalStateException ignored) {
                    continue;
                }
            }
            try {
                FlightLoop occupied = loop.occupyStation();
                vessels.remove(i);
                return occupied;
            } catch (IllegalStateException ignored) {
                // not parked or no habitat
            }
        }
        return null;
    }

    /**
     * Coast every vehicle through the column. Those that fall under 20 km are
     * removed and returned. Substeps match vacuum propagate (≤ 0.1 s).
     */
    public List<FlightLoop> advance(double dt) {
        if (!(dt > 0.0) || !Double.isFinite(dt)) {
            throw new IllegalArgumentException("Map step must be finite and positive");
        }
        List<FlightLoop> still = new ArrayList<>();
        List<FlightLoop> reentered = new ArrayList<>();
        for (FlightLoop loop : vessels) {
            FlightLoop stepped = coast(loop, dt);
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

    private static FlightLoop coast(FlightLoop loop, double dt) {
        double remaining = dt;
        FlightLoop stepped = loop;
        while (remaining > 1.0e-12) {
            double step = Math.min(remaining, 0.1);
            stepped = stepped.coast(step);
            remaining -= step;
            if (stepped.presence() == FlightPresence.WORLD_ENTITY) {
                return stepped;
            }
        }
        return stepped;
    }
}
