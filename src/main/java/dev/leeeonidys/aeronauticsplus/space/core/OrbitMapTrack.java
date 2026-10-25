package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;

/**
 * One vehicle on the Kepler map. q / Ap / Pe come from the current body's flight state;
 * not a ChemMod leak field. The client uses these values for live telemetry and map planning.
 */
public record OrbitMapTrack(
        String id,
        double altitudeMeters,
        double angleRadians,
        FlightPresence presence,
        double horizontalSpeedMetersPerSecond,
        double radialSpeedMetersPerSecond,
        double totalSpeedMetersPerSecond,
        double throttle,
        double availableThrustNewtons,
        double propellantMassKg,
        Vector3d angularVelocityBodyRadiansPerSecond,
        double dynamicPressurePascals,
        double apoapsisAltitudeMeters,
        double periapsisAltitudeMeters,
        double periapsisAngleRadians,
        String bodyId) {
    public OrbitMapTrack {
        if (id == null || id.isBlank() || presence == null
                || bodyId == null || bodyId.isBlank() || angularVelocityBodyRadiansPerSecond == null) {
            throw new IllegalArgumentException("Map track identity is required");
        }
        if (!Double.isFinite(altitudeMeters) || !Double.isFinite(angleRadians)
                || !Double.isFinite(horizontalSpeedMetersPerSecond)
                || horizontalSpeedMetersPerSecond < 0.0
                || !Double.isFinite(radialSpeedMetersPerSecond)
                || !Double.isFinite(totalSpeedMetersPerSecond) || totalSpeedMetersPerSecond < 0.0
                || !Double.isFinite(throttle) || throttle < 0.0 || throttle > 1.0
                || !Double.isFinite(availableThrustNewtons) || availableThrustNewtons < 0.0
                || !Double.isFinite(propellantMassKg) || propellantMassKg < 0.0
                || !Double.isFinite(dynamicPressurePascals)
                || dynamicPressurePascals < 0.0
                || !Double.isFinite(periapsisAltitudeMeters)
                || !Double.isFinite(periapsisAngleRadians)
                || Double.isNaN(apoapsisAltitudeMeters)) {
            throw new IllegalArgumentException("Map track numbers are invalid");
        }
    }

    public static OrbitMapTrack from(FlightLoop loop) {
        if (loop == null || loop.vessel() == null) {
            throw new IllegalArgumentException("Map track needs a flight loop");
        }
        OrbitState orbit = loop.vessel().orbit();
        GridPos pad = loop.padLeft().origin();
        String id = loop.vessel().id() + "@" + pad.x() + "," + pad.y() + "," + pad.z();
        return new OrbitMapTrack(
                id,
                orbit.altitudeMeters(),
                Math.atan2(orbit.positionMeters().z(), orbit.positionMeters().x()),
                loop.presence(),
                orbit.horizontalSpeedMetersPerSecond(),
                orbit.radialSpeedMetersPerSecond(),
                orbit.velocityMetersPerSecond().magnitude(),
                loop.vessel().liveThrottle(),
                loop.vessel().activeStage().netThrustNewtonsAt(
                        SolarSystem.atmosphereOf(orbit.centralBody()).pressurePascals(Math.max(0.0, orbit.altitudeMeters())))
                        .magnitude(),
                loop.vessel().livePropellantKg(),
                loop.vessel().angularVelocityBody(),
                SolarSystem.atmosphereOf(orbit.centralBody()).dynamicPressurePascals(orbit),
                orbit.apoapsisAltitudeMeters(),
                orbit.periapsisAltitudeMeters(),
                Math.atan2(orbit.eccentricityVector().z(), orbit.eccentricityVector().x()),
                orbit.centralBody().id());
    }
}
