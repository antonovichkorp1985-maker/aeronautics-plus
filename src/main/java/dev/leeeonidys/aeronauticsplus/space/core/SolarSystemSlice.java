package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Two-body catalogue: Kepler period on Moon/Mars, retrograde Venus, Mars air is thin.
 * Not n-body. Not a Moon world.
 */
public final class SolarSystemSlice {
    private SolarSystemSlice() {
    }

    public record Result(
            int planets,
            int majorMoons,
            int minorMoons,
            int beltNamed,
            double moonPeriodHours,
            double marsQ,
            boolean venusRetro,
            boolean belt) {
    }

    public static Result execute() {
        if (SolarSystem.planets().size() != 8) {
            throw new IllegalStateException("Catalogue must list 8 planets");
        }
        if (SolarSystem.majorMoons().size() < 14) {
            throw new IllegalStateException("Need Galilean + Saturn majors + Triton, n="
                    + SolarSystem.majorMoons().size());
        }
        if (SolarSystem.minorMoons().stream().noneMatch(moon -> "deimos".equals(moon.body().id()) && moon.mapOnly())) {
            throw new IllegalStateException("Deimos is a map dot, not a world");
        }
        AsteroidBelt belt = SolarSystem.asteroidBelt();
        if (belt.named().size() < 4) {
            throw new IllegalStateException("Belt must name Ceres/Vesta/Pallas/Hygiea");
        }
        OrbitState ceres = SolarSystem.circular(SolarSystem.body("ceres"), 200_000.0);
        OrbitState inBelt = SolarSystem.circular(SolarSystem.sun(), 2.7 * SolarSystem.AU_METERS);
        if (!SolarSystem.inAsteroidBelt(inBelt) || SolarSystem.inAsteroidBelt(ceres)) {
            throw new IllegalStateException("Belt is sun-centred; Ceres parking is not the annulus");
        }
        FlightLoop ganymede = CreativeOrbit.parkedAround(SolarSystem.body("ganymede"));
        if (!"ganymede".equals(ganymede.vessel().orbit().centralBody().id())
                || ganymede.occupyStation().presence() != FlightPresence.STATION_BLOCKS) {
            throw new IllegalStateException("Creative Ganymede park must open the station pocket");
        }
        if (SolarSystem.sun().gravitationalParameter() <= SolarSystem.earth().gravitationalParameter()) {
            throw new IllegalStateException("Sun μ must exceed Earth");
        }
        OrbitState lunar = SolarSystem.circular(SolarSystem.moon(), 100_000.0);
        double a = lunar.positionMeters().magnitude();
        double period = 2.0 * Math.PI * Math.sqrt(a * a * a / lunar.centralBody().gravitationalParameter());
        double hours = period / 3600.0;
        if (hours < 1.8 || hours > 2.2) {
            throw new IllegalStateException("100 km lunar circular period ~2 h, got " + hours);
        }
        if (SolarSystem.venus().siderealAngularVelocityRadiansPerSecond() >= 0.0) {
            throw new IllegalStateException("Venus must spin retrograde");
        }
        Atmosphere mars = SolarSystem.marsAir();
        OrbitState pad = new OrbitState(
                SolarSystem.mars(),
                new Vector3d(SolarSystem.mars().radiusMeters(), 0.0, 0.0),
                new Vector3d(0.0, 0.0, 200.0),
                0.0);
        double q = mars.dynamicPressurePascals(pad);
        double earthQ = Atmosphere.earth().dynamicPressurePascals(new OrbitState(
                SolarSystem.earth(),
                new Vector3d(SolarSystem.earth().radiusMeters(), 0.0, 0.0),
                new Vector3d(0.0, 0.0, 200.0),
                0.0));
        if (!(q < earthQ / 50.0)) {
            throw new IllegalStateException("Mars pad q must be thin vs Earth, mars=" + q + " earth=" + earthQ);
        }
        double moonG = SolarSystem.moon().surfaceGravityMetersPerSecond2();
        if (Math.abs(moonG - 1.62) > 0.05) {
            throw new IllegalStateException("Moon g ~1.62, g=" + moonG);
        }
        return new Result(
                SolarSystem.planets().size(),
                SolarSystem.majorMoons().size(),
                SolarSystem.minorMoons().size(),
                belt.named().size(),
                hours,
                q,
                true,
                true);
    }
}
