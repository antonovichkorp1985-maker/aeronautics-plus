package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/** Startup regression checks for the global system-map ephemeris and logarithmic projection. */
public final class SystemMapSlice {
    private SystemMapSlice() {
    }

    public record Result(double earthDistanceAu, double neptuneDistanceAu,
                         double moonDistanceKm, double outerProjectionRadius) {
    }

    public static Result execute() {
        Vector3d earth = SolarSystemMap.position("earth", 0.0);
        Vector3d neptune = SolarSystemMap.position("neptune", 0.0);
        Vector3d moon = SolarSystemMap.position("moon", 0.0);
        double earthAu = earth.magnitude() / SolarSystem.AU_METERS;
        double neptuneAu = neptune.magnitude() / SolarSystem.AU_METERS;
        double moonKm = moon.subtract(earth).magnitude() / 1000.0;
        double nextDayEarthAu = SolarSystemMap.position("earth", 86_400.0).magnitude() / SolarSystem.AU_METERS;
        double outerRadius = projectedRadius(40.0);
        boolean orderedAuRings = projectedRadius(1.0) < projectedRadius(5.0)
                && projectedRadius(5.0) < projectedRadius(10.0)
                && projectedRadius(10.0) < projectedRadius(20.0)
                && projectedRadius(20.0) < outerRadius
                && Math.abs(outerRadius - 1.0) < 1.0e-12;
        validateOrbitRadii();
        validateSatellites();
        if (!(earthAu > 0.97 && earthAu < 1.03)
                || !(nextDayEarthAu > 0.97 && nextDayEarthAu < 1.03)
                || !(neptuneAu > 29.0 && neptuneAu < 31.0)
                || !(moonKm > 370_000.0 && moonKm < 400_000.0)
                || !orderedAuRings
                || !(SolarSystemMap.position("ceres", 0.0).magnitude() > SolarSystem.AU_METERS * 2.0)) {
            throw new IllegalStateException("Global system map ephemeris regression");
        }
        return new Result(earthAu, neptuneAu, moonKm, outerRadius);
    }

    private static double projectedRadius(double au) {
        Vector3d point = new Vector3d(au * SolarSystem.AU_METERS, 0.0, 0.0);
        OrbitMapView.Projected projected = OrbitMapView.projectSystem(point, 0.0, 0.0, 1.0);
        return Math.hypot(projected.x(), projected.y());
    }

    /** Rotation into an inclined orbital plane must preserve the Keplerian radius. */
    private static void validateOrbitRadii() {
        List<ExpectedOrbit> catalogue = List.of(
                new ExpectedOrbit("mercury", 0.3871, 0.2056, 174.79),
                new ExpectedOrbit("venus", 0.7233, 0.0068, 50.38),
                new ExpectedOrbit("earth", 1.0000, 0.0167, -2.48),
                new ExpectedOrbit("mars", 1.5237, 0.0934, 19.39),
                new ExpectedOrbit("jupiter", 5.2028, 0.0489, 19.65),
                new ExpectedOrbit("saturn", 9.5388, 0.0565, 317.65),
                new ExpectedOrbit("uranus", 19.1914, 0.0463, 143.09),
                new ExpectedOrbit("neptune", 30.0611, 0.0095, 259.38),
                new ExpectedOrbit("ceres", 2.767, 0.0758, 96.0),
                new ExpectedOrbit("vesta", 2.362, 0.0888, 151.2),
                new ExpectedOrbit("pallas", 2.773, 0.2310, 33.0),
                new ExpectedOrbit("hygiea", 3.140, 0.1120, 67.7));
        for (ExpectedOrbit orbit : catalogue) {
            double a = orbit.semiMajorAu() * SolarSystem.AU_METERS;
            for (int sample = 0; sample < 16; sample++) {
                double offset = 2.0 * Math.PI * sample / 16.0;
                double mean = Math.toRadians(orbit.meanAnomalyDeg()) + offset;
                double eccentric = mean;
                for (int iteration = 0; iteration < 12; iteration++) {
                    eccentric -= (eccentric - orbit.eccentricity() * Math.sin(eccentric) - mean)
                            / (1.0 - orbit.eccentricity() * Math.cos(eccentric));
                }
                double x = a * (Math.cos(eccentric) - orbit.eccentricity());
                double z = a * Math.sqrt(1.0 - orbit.eccentricity() * orbit.eccentricity())
                        * Math.sin(eccentric);
                double expectedRadius = Math.hypot(x, z);
                double actualRadius = SolarSystemMap.orbitPoint(orbit.id(), 0.0, offset).magnitude();
                if (Math.abs(actualRadius - expectedRadius) > expectedRadius * 1.0e-10) {
                    throw new IllegalStateException("Inclined orbit rotation changes radius for " + orbit.id());
                }
            }
        }
    }

    private static void validateSatellites() {
        for (Satellite satellite : SolarSystem.majorMoons()) {
            validateSatellite(satellite);
        }
        for (Satellite satellite : SolarSystem.minorMoons()) {
            validateSatellite(satellite);
        }
    }

    private static void validateSatellite(Satellite satellite) {
        Vector3d position = SolarSystemMap.position(satellite.body().id(), 12_345.0);
        Vector3d primary = SolarSystemMap.position(satellite.primaryId(), 12_345.0);
        double distance = position.subtract(primary).magnitude();
        if (Math.abs(distance - satellite.semiMajorMeters()) > satellite.semiMajorMeters() * 1.0e-10) {
            throw new IllegalStateException("Satellite orbit radius regression for " + satellite.body().id());
        }
    }

    private record ExpectedOrbit(String id, double semiMajorAu, double eccentricity, double meanAnomalyDeg) {
    }
}
