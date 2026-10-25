package dev.leeeonidys.aeronauticsplus.space.core;

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
        OrbitMapView.Projected outer = OrbitMapView.projectSystem(neptune, 0.0, 0.0, 1.0);
        double outerRadius = Math.hypot(outer.x(), outer.y());
        if (!(earthAu > 0.97 && earthAu < 1.03)
                || !(nextDayEarthAu > 0.97 && nextDayEarthAu < 1.03)
                || !(neptuneAu > 29.0 && neptuneAu < 31.0)
                || !(moonKm > 370_000.0 && moonKm < 400_000.0)
                || !(outerRadius > 0.0 && outerRadius < 1.0)
                || !(SolarSystemMap.position("ceres", 0.0).magnitude() > SolarSystem.AU_METERS * 2.0)) {
            throw new IllegalStateException("Global system map ephemeris regression");
        }
        return new Result(earthAu, neptuneAu, moonKm, outerRadius);
    }
}
