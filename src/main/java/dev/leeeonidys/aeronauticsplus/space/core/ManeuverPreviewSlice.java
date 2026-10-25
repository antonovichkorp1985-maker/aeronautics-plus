package dev.leeeonidys.aeronauticsplus.space.core;

/** Startup regression checks for the map's prograde impulse prediction. */
public final class ManeuverPreviewSlice {
    private ManeuverPreviewSlice() {
    }

    public record Result(double circularErrorMeters, double raisedApoapsisMeters, boolean escapePredicted) {
    }

    public static Result execute() {
        CelestialBody earth = SolarSystem.earth();
        double altitude = 200_000.0;
        double radius = earth.radiusMeters() + altitude;
        double circularSpeed = Math.sqrt(earth.gravitationalParameter() / radius);
        ManeuverPreview.Result coast = ManeuverPreview.progradeImpulse(earth, altitude, 0.0, circularSpeed, 0.0);
        double circularError = Math.max(
                Math.abs(coast.apoapsisAltitudeMeters() - altitude),
                Math.abs(coast.periapsisAltitudeMeters() - altitude));
        if (circularError > 0.1) {
            throw new IllegalStateException("Zero-dV circular preview must preserve its orbit, error=" + circularError);
        }
        ManeuverPreview.Result raised = ManeuverPreview.progradeImpulse(earth, altitude, 0.0, circularSpeed, 100.0);
        if (raised.escape() || !(raised.apoapsisAltitudeMeters() > altitude + 100_000.0)
                || Math.abs(raised.periapsisAltitudeMeters() - altitude) > 0.1) {
            throw new IllegalStateException("Prograde impulse must raise Ap while keeping current point at Pe");
        }
        ManeuverPreview.Result escape = ManeuverPreview.progradeImpulse(earth, altitude, 0.0, circularSpeed, 4_000.0);
        if (!escape.escape() || !Double.isFinite(escape.periapsisAltitudeMeters())) {
            throw new IllegalStateException("Escape preview must report escape and a finite periapsis");
        }
        return new Result(circularError, raised.apoapsisAltitudeMeters(), escape.escape());
    }
}
