package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/**
 * Plot of the Kepler map. The disk is the focused body, not always Earth.
 * Altitude is linear out to parking (200 km) so nearby orbits are visible.
 * Not geometric scale, not a Moon dimension, not ChemMod air.
 */
public final class OrbitMapView {
    /** Fraction of the plot radius that is the central-body disk. */
    public static final double EARTH_FRACTION = 0.72;
    /** Outer ring of the plot: parking-orbit altitude. */
    public static final double VIEW_ALTITUDE_METERS = 200_000.0;

    private OrbitMapView() {
    }

    public record Plot(double x, double y) {
        public Plot {
            if (!Double.isFinite(x) || !Double.isFinite(y)) {
                throw new IllegalArgumentException("Plot coordinates must be finite");
            }
        }

        public double radius() {
            return Math.hypot(x, y);
        }
    }

    /** Orthographic camera-space point. Depth is positive toward the viewer. */
    public record Projected(double x, double y, double depth) {
        public Projected {
            if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(depth)) {
                throw new IllegalArgumentException("Projected coordinates must be finite");
            }
        }
    }

    /** Which body's disk the map is drawing. Handoff ring is Earth-only. */
    public record Frame(String bodyId, boolean handoff) {
        public Frame {
            if (bodyId == null || bodyId.isBlank()) {
                throw new IllegalArgumentException("Frame body is required");
            }
        }
    }

    public static Frame frame(List<OrbitMapTrack> tracks) {
        if (tracks != null) {
            for (OrbitMapTrack track : tracks) {
                if (track != null && track.bodyId() != null && !track.bodyId().isBlank()) {
                    return new Frame(track.bodyId(), handoffVisible(track.bodyId()));
                }
            }
        }
        return new Frame("earth", true);
    }

    public static boolean handoffVisible(String bodyId) {
        return "earth".equals(bodyId);
    }

    public static boolean inFrame(OrbitMapTrack track, String bodyId) {
        if (track == null || bodyId == null || bodyId.isBlank()) {
            throw new IllegalArgumentException("Track and body are required");
        }
        return bodyId.equals(track.bodyId());
    }

    public static Plot plot(double altitudeMeters, double angleRadians) {
        if (!Double.isFinite(altitudeMeters) || !Double.isFinite(angleRadians)) {
            throw new IllegalArgumentException("Plot altitude and angle must be finite");
        }
        double t = Math.max(0.0, altitudeMeters) / VIEW_ALTITUDE_METERS;
        if (t > 1.0) {
            t = 1.0;
        }
        double r = EARTH_FRACTION + (1.0 - EARTH_FRACTION) * t;
        return new Plot(r * Math.cos(angleRadians), r * Math.sin(angleRadians));
    }

    /**
     * Project a true 3D two-body conic into the map's XZ-facing azimuth/altitude plot.
     * The radial scale is intentionally not geometric, but the azimuth now reflects the
     * orbit plane instead of treating every orbit as equatorial.
     */
    public static Plot conicPoint(double bodyRadiusMeters, double periapsisAltitudeMeters,
                                  double apoapsisAltitudeMeters, Vector3d periapsisDirection,
                                  Vector3d orbitNormal, double trueAnomalyRadians) {
        if (!(bodyRadiusMeters > 0.0) || !Double.isFinite(bodyRadiusMeters)
                || !(bodyRadiusMeters + periapsisAltitudeMeters > 0.0)
                || !(bodyRadiusMeters + apoapsisAltitudeMeters >= bodyRadiusMeters + periapsisAltitudeMeters)
                || !Double.isFinite(apoapsisAltitudeMeters)
                || periapsisDirection == null || orbitNormal == null
                || !Double.isFinite(trueAnomalyRadians)) {
            throw new IllegalArgumentException("Conic projection inputs are invalid");
        }
        Vector3d position = conicPosition(bodyRadiusMeters, periapsisAltitudeMeters,
                apoapsisAltitudeMeters, periapsisDirection, orbitNormal, trueAnomalyRadians);
        double altitude = position.magnitude() - bodyRadiusMeters;
        return plot(altitude, Math.atan2(position.z(), position.x()));
    }

    /** Position on a bound conic in the body-centred inertial frame. */
    public static Vector3d conicPosition(double bodyRadiusMeters, double periapsisAltitudeMeters,
                                         double apoapsisAltitudeMeters, Vector3d periapsisDirection,
                                         Vector3d orbitNormal, double trueAnomalyRadians) {
        if (!(bodyRadiusMeters > 0.0) || !Double.isFinite(bodyRadiusMeters)
                || !(bodyRadiusMeters + periapsisAltitudeMeters > 0.0)
                || !(bodyRadiusMeters + apoapsisAltitudeMeters >= bodyRadiusMeters + periapsisAltitudeMeters)
                || !Double.isFinite(apoapsisAltitudeMeters)
                || periapsisDirection == null || orbitNormal == null
                || !Double.isFinite(trueAnomalyRadians)) {
            throw new IllegalArgumentException("Conic projection inputs are invalid");
        }
        double rp = bodyRadiusMeters + periapsisAltitudeMeters;
        double ra = bodyRadiusMeters + apoapsisAltitudeMeters;
        double eccentricity = (ra - rp) / (ra + rp);
        double semilatus = rp * (1.0 + eccentricity);
        double denominator = 1.0 + eccentricity * Math.cos(trueAnomalyRadians);
        if (!(denominator > 0.0)) {
            throw new IllegalArgumentException("Conic anomaly is outside its valid branch");
        }
        Vector3d periapsis = periapsisDirection.normalized();
        Vector3d normal = orbitNormal.normalized();
        Vector3d transverse = normal.cross(periapsis).normalized();
        double radius = semilatus / denominator;
        return periapsis.multiply(radius * Math.cos(trueAnomalyRadians))
                .add(transverse.multiply(radius * Math.sin(trueAnomalyRadians)));
    }

    /** Project a heliocentric point with logarithmic AU scaling for the whole-system view. */
    public static Projected projectSystem(Vector3d heliocentricPositionMeters,
                                          double yawRadians, double pitchRadians, double zoom) {
        if (heliocentricPositionMeters == null || !Double.isFinite(yawRadians)
                || !Double.isFinite(pitchRadians) || !(zoom > 0.0) || !Double.isFinite(zoom)) {
            throw new IllegalArgumentException("System projection inputs are invalid");
        }
        double distance = heliocentricPositionMeters.magnitude();
        if (!(distance > 1.0e-9)) {
            return new Projected(0.0, 0.0, 0.0);
        }
        Vector3d unit = heliocentricPositionMeters.multiply(1.0 / distance);
        double radius = Math.log1p(distance / SolarSystem.AU_METERS)
                / Math.log1p(SolarSystemMap.rangeAu()) * zoom;
        return rotate(unit, radius, yawRadians, pitchRadians);
    }

    /** Project a body-centred position on the local map's compressed altitude scale. */
    public static Projected project3d(double altitudeMeters, Vector3d direction,
                                      double yawRadians, double pitchRadians, double zoom) {
        if (!Double.isFinite(altitudeMeters) || direction == null
                || !Double.isFinite(yawRadians) || !Double.isFinite(pitchRadians)
                || !(zoom > 0.0) || !Double.isFinite(zoom)) {
            throw new IllegalArgumentException("3D projection inputs are invalid");
        }
        Vector3d unit = direction.normalized();
        double radius = (EARTH_FRACTION + (1.0 - EARTH_FRACTION)
                * Math.max(0.0, Math.min(1.0, altitudeMeters / VIEW_ALTITUDE_METERS))) * zoom;
        return rotate(unit, radius, yawRadians, pitchRadians);
    }

    private static Projected rotate(Vector3d unit, double radius, double yawRadians, double pitchRadians) {
        double cy = Math.cos(yawRadians);
        double sy = Math.sin(yawRadians);
        double xYaw = cy * unit.x() - sy * unit.z();
        double zYaw = sy * unit.x() + cy * unit.z();
        double cp = Math.cos(pitchRadians);
        double sp = Math.sin(pitchRadians);
        double yView = cp * unit.y() - sp * zYaw;
        double depth = sp * unit.y() + cp * zYaw;
        return new Projected(radius * xYaw, radius * yView, depth);
    }

    /** Which body's disk the map is drawing. Handoff ring is Earth-only. */
    public static Plot plot(OrbitMapTrack track) {
        if (track == null) {
            throw new IllegalArgumentException("Plot needs a track");
        }
        return plot(track.altitudeMeters(), track.angleRadians());
    }

    /** Radius of the 20 km handoff ring in plot units (0..1). Earth only. */
    public static double handoffRadius() {
        return EARTH_FRACTION
                + (1.0 - EARTH_FRACTION) * (WorldHandoff.MAP_ALTITUDE_METERS / VIEW_ALTITUDE_METERS);
    }

    /** Geometric altitude of the Moon SOI above the lunar surface, km. */
    public static double moonSoiAltitudeKm() {
        return (PatchedConics.moonSphereOfInfluenceMeters() - SolarSystem.moon().radiusMeters()) / 1000.0;
    }
}
