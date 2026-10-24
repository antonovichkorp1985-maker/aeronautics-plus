package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import java.util.List;

/**
 * Kepler map at 20 km still has the ISA column. Coast drags. Parking at 200 km
 * is vacuum Kepler. Burns stay off the map. Not {@code molesAt}.
 */
public final class MapAirSlice {
    private MapAirSlice() {
    }

    public record Result(
            double airSpeed,
            double vacuumSpeed,
            double parkingDelta,
            boolean fuelHeld) {
    }

    public static Result execute() {
        FlightLoop mapped = FlightLoop.sit(hopper()).liftoff()
                .withOrbit(atAltitude(20_000.0, 800.0));
        mapped = mapped.withVessel(mapped.vessel().withLiveThrottle(1.0));
        if (mapped.presence() != FlightPresence.ORBIT_MAP) {
            throw new IllegalStateException("20 km must be the Kepler map");
        }
        OrbitMapTrack dense = OrbitMapTrack.from(mapped);
        if (!(dense.dynamicPressurePascals() > 100.0)) {
            throw new IllegalStateException(
                    "20 km 800 m/s must show q, q=" + dense.dynamicPressurePascals());
        }
        if (!Double.isFinite(dense.periapsisAltitudeMeters())) {
            throw new IllegalStateException("Map track must carry periapsis");
        }
        double fuel0 = mapped.vessel().livePropellantKg();
        double epoch0 = mapped.vessel().orbit().epochSeconds();
        OrbitMap map = new OrbitMap();
        map.enter(mapped);
        List<FlightLoop> reentered = map.advance(2.0);
        FlightLoop air = !reentered.isEmpty() ? reentered.get(0)
                : (map.size() == 1 ? map.snapshot().get(0) : null);
        if (air == null) {
            throw new IllegalStateException("Map coast must leave a vehicle");
        }
        double elapsed = air.vessel().orbit().epochSeconds() - epoch0;
        if (!(elapsed > 0.05)) {
            throw new IllegalStateException("Map coast must advance epoch, dt=" + elapsed);
        }
        VesselState vacuum = VesselDynamics.propagate(mapped.vessel(), elapsed, 0.1);
        if (Math.abs(air.vessel().livePropellantKg() - fuel0) > 0.05) {
            throw new IllegalStateException(
                    "Map must not burn, was=" + fuel0 + " now=" + air.vessel().livePropellantKg());
        }
        double airSpeed = air.vessel().orbit().velocityMetersPerSecond().magnitude();
        double vacuumSpeed = vacuum.orbit().velocityMetersPerSecond().magnitude();
        if (!(airSpeed < vacuumSpeed - 5.0)) {
            throw new IllegalStateException(
                    "20 km map coast must drag vs vacuum Kepler, air=" + airSpeed + " vac=" + vacuumSpeed);
        }

        FlightLoop parked = FlightLoop.sit(hopper()).liftoff().withOrbit(SpaceBodies.parkingOrbit());
        VesselState parkingVacuum = parked.vessel();
        OrbitMap high = new OrbitMap();
        high.enter(parked);
        for (int i = 0; i < 20; i++) {
            high.advance(0.1);
            parkingVacuum = VesselDynamics.propagate(parkingVacuum, 0.1, 0.1);
        }
        if (high.size() != 1) {
            throw new IllegalStateException("200 km parking must stay on the map");
        }
        OrbitMapTrack parkTrack = OrbitMapTrack.from(parked);
        if (parkTrack.dynamicPressurePascals() > 1.0) {
            throw new IllegalStateException(
                    "200 km parking q must be vacuum, q=" + parkTrack.dynamicPressurePascals());
        }
        if (Math.abs(parkTrack.apoapsisAltitudeMeters() - 200_000.0) > 5_000.0) {
            throw new IllegalStateException(
                    "Parking track Ap must sit near 200 km, Ap=" + parkTrack.apoapsisAltitudeMeters());
        }
        double parkingDelta = Math.abs(
                high.snapshot().get(0).vessel().orbit().velocityMetersPerSecond().magnitude()
                        - parkingVacuum.orbit().velocityMetersPerSecond().magnitude());
        if (parkingDelta > 0.05) {
            throw new IllegalStateException(
                    "200 km parking must match vacuum Kepler, dV=" + parkingDelta);
        }
        return new Result(airSpeed, vacuumSpeed, parkingDelta, true);
    }

    private static OrbitState atAltitude(double altitudeMeters, double horizontalMetersPerSecond) {
        CelestialBody earth = SpaceBodies.earth();
        double radius = earth.radiusMeters() + altitudeMeters;
        return new OrbitState(
                earth,
                new Vector3d(radius, 0.0, 0.0),
                new Vector3d(0.0, 0.0, horizontalMetersPerSecond),
                0.0);
    }

    private static VesselBlockGrid hopper() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }
}
