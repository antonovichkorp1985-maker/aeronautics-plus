package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Two-body prediction for an ideal instantaneous prograde impulse. This is a planning calculation
 * only: it neither spends propellant nor commands a vessel burn.
 */
public final class ManeuverPreview {
    private ManeuverPreview() {
    }

    public static Result progradeImpulse(
            CelestialBody body,
            double altitudeMeters,
            double radialVelocityMetersPerSecond,
            double horizontalVelocityMetersPerSecond,
            double deltaVMetersPerSecond) {
        if (body == null || !Double.isFinite(altitudeMeters)
                || !Double.isFinite(radialVelocityMetersPerSecond)
                || !Double.isFinite(horizontalVelocityMetersPerSecond)
                || horizontalVelocityMetersPerSecond < 0.0
                || !Double.isFinite(deltaVMetersPerSecond)) {
            throw new IllegalArgumentException("Maneuver preview inputs are invalid");
        }
        double radius = body.radiusMeters() + altitudeMeters;
        double tangentialSpeed = horizontalVelocityMetersPerSecond + deltaVMetersPerSecond;
        if (!(radius > 0.0) || tangentialSpeed < 0.0) {
            throw new IllegalArgumentException("Impulse reverses the prograde direction or lies inside the body");
        }
        double mu = body.gravitationalParameter();
        double speedSquared = radialVelocityMetersPerSecond * radialVelocityMetersPerSecond
                + tangentialSpeed * tangentialSpeed;
        double energy = 0.5 * speedSquared - mu / radius;
        double angularMomentum = radius * tangentialSpeed;
        double eccentricitySquared = 1.0 + 2.0 * energy * angularMomentum * angularMomentum / (mu * mu);
        double eccentricity = Math.sqrt(Math.max(0.0, eccentricitySquared));
        if (!(energy < 0.0)) {
            double periapsisAltitude = angularMomentum * angularMomentum
                    / (mu * (1.0 + eccentricity)) - body.radiusMeters();
            return new Result(true, Double.POSITIVE_INFINITY, periapsisAltitude, eccentricity, energy);
        }
        double semiMajorAxis = -mu / (2.0 * energy);
        double periapsisAltitude = semiMajorAxis * (1.0 - eccentricity) - body.radiusMeters();
        double apoapsisAltitude = semiMajorAxis * (1.0 + eccentricity) - body.radiusMeters();
        return new Result(false, apoapsisAltitude, periapsisAltitude, eccentricity, energy);
    }

    public record Result(
            boolean escape,
            double apoapsisAltitudeMeters,
            double periapsisAltitudeMeters,
            double eccentricity,
            double specificEnergyJoulesPerKg) {
        public Result {
            if (Double.isNaN(apoapsisAltitudeMeters) || !Double.isFinite(eccentricity)
                    || eccentricity < 0.0 || !Double.isFinite(specificEnergyJoulesPerKg)
                    || Double.isNaN(periapsisAltitudeMeters)) {
                throw new IllegalArgumentException("Maneuver preview result is invalid");
            }
        }
    }
}
