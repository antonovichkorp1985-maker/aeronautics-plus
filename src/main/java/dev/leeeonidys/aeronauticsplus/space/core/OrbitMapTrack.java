package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;

/**
 * One vehicle on the Kepler map display. Not ChemMod air, not an orientation GUI.
 */
public record OrbitMapTrack(
        String id,
        double altitudeMeters,
        double angleRadians,
        FlightPresence presence,
        double horizontalSpeedMetersPerSecond) {
    public OrbitMapTrack {
        if (id == null || id.isBlank() || presence == null) {
            throw new IllegalArgumentException("Map track identity is required");
        }
        if (!Double.isFinite(altitudeMeters) || !Double.isFinite(angleRadians)
                || !Double.isFinite(horizontalSpeedMetersPerSecond)
                || horizontalSpeedMetersPerSecond < 0.0) {
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
                orbit.horizontalSpeedMetersPerSecond());
    }
}
