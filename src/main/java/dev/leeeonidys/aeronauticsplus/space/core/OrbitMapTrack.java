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
        Vector3d positionDirection,
        double epochSeconds,
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
        double inclinationDegrees,
        Vector3d periapsisDirection,
        Vector3d orbitNormal,
        String bodyId) {
    public OrbitMapTrack {
        if (id == null || id.isBlank() || presence == null
                || bodyId == null || bodyId.isBlank() || angularVelocityBodyRadiansPerSecond == null
                || positionDirection == null || periapsisDirection == null || orbitNormal == null) {
            throw new IllegalArgumentException("Map track identity is required");
        }
        if (!Double.isFinite(altitudeMeters) || !Double.isFinite(angleRadians) || !Double.isFinite(epochSeconds)
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
                || Double.isInfinite(inclinationDegrees)
                || (Double.isFinite(inclinationDegrees) && (inclinationDegrees < 0.0 || inclinationDegrees > 180.0))
                || Double.isNaN(apoapsisAltitudeMeters)) {
            throw new IllegalArgumentException("Map track numbers are invalid");
        }
    }

    private static double inclinationDegrees(OrbitState orbit) {
        try {
            return Math.toDegrees(orbit.inclinationRadians());
        } catch (IllegalStateException radialTrajectory) {
            return Double.NaN; // A radial path has no orbital plane/inclination.
        }
    }

    private static Vector3d periapsisDirection(OrbitState orbit) {
        Vector3d eccentricity = orbit.eccentricityVector();
        if (eccentricity.magnitudeSquared() > 1.0e-18) {
            return eccentricity.normalized();
        }
        // Circular orbits have no unique periapsis. Use the live radial direction as a stable basis.
        return orbit.positionMeters().normalized();
    }

    private static Vector3d orbitNormal(OrbitState orbit) {
        Vector3d momentum = orbit.specificAngularMomentum();
        if (momentum.magnitudeSquared() > 1.0e-18) {
            return momentum.normalized();
        }
        // A radial trajectory has no defined plane; it is not drawn as a closed conic.
        return Vector3d.UNIT_Y;
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
                orbit.positionMeters().normalized(),
                orbit.epochSeconds(),
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
                inclinationDegrees(orbit),
                periapsisDirection(orbit),
                orbitNormal(orbit),
                orbit.centralBody().id());
    }
}
