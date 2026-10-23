package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Minecraft look → inertial nose. Not a GUI: the seated player's camera is the stick.
 * xRot −90 looks at the sky (radial). xRot 0 looks at the horizon (pitch-over).
 * yRot 0 (south) pitches over toward inertial +Z, the parking-orbit east at the pad.
 */
public final class LookGuidance {
    private LookGuidance() {
    }

    public static Vector3d inertialAim(OrbitState orbit, double yRotDegrees, double xRotDegrees) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        if (!Double.isFinite(yRotDegrees) || !Double.isFinite(xRotDegrees)) {
            throw new IllegalArgumentException("Look angles must be finite");
        }
        Vector3d radial = orbit.radialUnit();
        Vector3d east = eastOf(radial);
        Vector3d north = radial.cross(east).normalized();
        double fromUp = Math.toRadians(xRotDegrees + 90.0);
        double azimuth = Math.toRadians(-yRotDegrees);
        Vector3d horizon = east.multiply(Math.sin(azimuth)).add(north.multiply(Math.cos(azimuth)));
        Vector3d aim = radial.multiply(Math.cos(fromUp)).add(horizon.multiply(Math.sin(fromUp)));
        if (!(aim.magnitudeSquared() > 0.0)) {
            return radial;
        }
        return aim.normalized();
    }

    public static Attitude attitudeFromLook(OrbitState orbit, double yRotDegrees, double xRotDegrees) {
        return Attitude.pointing(Vector3d.UNIT_Y, inertialAim(orbit, yRotDegrees, xRotDegrees));
    }

    /** Inverse of {@link #inertialAim}: yRot then xRot, Minecraft degrees. */
    public static double[] lookDegreesFromAim(OrbitState orbit, Vector3d aim) {
        if (orbit == null || aim == null) {
            throw new IllegalArgumentException("Orbit and aim are required");
        }
        Vector3d radial = orbit.radialUnit();
        Vector3d east = eastOf(radial);
        Vector3d north = radial.cross(east).normalized();
        Vector3d unit = aim.magnitudeSquared() > 0.0 ? aim.normalized() : radial;
        double radialDot = Math.max(-1.0, Math.min(1.0, unit.dot(radial)));
        double xRot = Math.toDegrees(Math.acos(radialDot)) - 90.0;
        Vector3d horizon = unit.subtract(radial.multiply(radialDot));
        double yRot = 0.0;
        if (horizon.magnitudeSquared() > 1.0e-12) {
            horizon = horizon.normalized();
            yRot = -Math.toDegrees(Math.atan2(horizon.dot(east), horizon.dot(north)));
        }
        return new double[] {yRot, xRot};
    }

    static Vector3d eastOf(Vector3d radial) {
        Vector3d ref = Math.abs(radial.dot(Vector3d.UNIT_Z)) < 0.9 ? Vector3d.UNIT_Z : Vector3d.UNIT_Y;
        Vector3d east = ref.subtract(radial.multiply(ref.dot(radial)));
        if (!(east.magnitudeSquared() > 0.0)) {
            east = Vector3d.UNIT_Y.subtract(radial.multiply(Vector3d.UNIT_Y.dot(radial)));
        }
        return east.normalized();
    }
}
