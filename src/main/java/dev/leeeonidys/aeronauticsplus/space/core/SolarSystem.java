package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Two-body catalogue. Not n-body. Spins may be retrograde (Venus, Uranus).
 * Atmospheres that are not Earth stay 2-layer parametric, same shape ChemMod uses.
 * Major moons are parking targets. Tiny moons are map dots. Belt is an annulus, not ore.
 */
public final class SolarSystem {
    public static final double AU_METERS = 149_597_870_700.0;

    private SolarSystem() {
    }

    public static CelestialBody sun() {
        return new CelestialBody("sun", 1.32712440018e20, 6.957e8, 2.865e-6);
    }

    public static CelestialBody mercury() {
        return new CelestialBody("mercury", 2.2032e13, 2.4397e6, 1.240e-6);
    }

    public static CelestialBody venus() {
        return new CelestialBody("venus", 3.24859e14, 6.0518e6, -2.9926e-7);
    }

    public static CelestialBody earth() {
        return new CelestialBody(
                "earth", 3.986004418e14, 6_371_000.0, CelestialBody.EARTH_SIDEREAL_RADIANS_PER_SECOND);
    }

    public static CelestialBody moon() {
        return new CelestialBody("moon", 4.9048695e12, 1.7374e6, 2.6617e-6);
    }

    public static CelestialBody mars() {
        return new CelestialBody("mars", 4.282837e13, 3.3895e6, 7.088218e-5);
    }

    public static CelestialBody jupiter() {
        return new CelestialBody("jupiter", 1.26686534e17, 6.9911e7, 1.7585e-4);
    }

    public static CelestialBody saturn() {
        return new CelestialBody("saturn", 3.7931187e16, 5.8232e7, 1.6378e-4);
    }

    public static CelestialBody uranus() {
        return new CelestialBody("uranus", 5.793939e15, 2.5362e7, -1.012e-4);
    }

    public static CelestialBody neptune() {
        return new CelestialBody("neptune", 6.836529e15, 2.4622e7, 1.0833e-4);
    }

    public static List<CelestialBody> planets() {
        return List.of(mercury(), venus(), earth(), mars(), jupiter(), saturn(), uranus(), neptune());
    }

    public static List<Satellite> majorMoons() {
        return List.of(
                new Satellite(moon(), "earth", PatchedConics.MOON_SEMI_MAJOR_METERS, false),
                new Satellite(body("io"), "jupiter", 4.218e8, false),
                new Satellite(body("europa"), "jupiter", 6.711e8, false),
                new Satellite(body("ganymede"), "jupiter", 1.0704e9, false),
                new Satellite(body("callisto"), "jupiter", 1.8827e9, false),
                new Satellite(body("titan"), "saturn", 1.2219e9, false),
                new Satellite(body("rhea"), "saturn", 5.27108e8, false),
                new Satellite(body("iapetus"), "saturn", 3.5613e9, false),
                new Satellite(body("dione"), "saturn", 3.77396e8, false),
                new Satellite(body("tethys"), "saturn", 2.94619e8, false),
                new Satellite(body("enceladus"), "saturn", 2.37948e8, false),
                new Satellite(body("titania"), "uranus", 4.3591e8, false),
                new Satellite(body("oberon"), "uranus", 5.8352e8, false),
                new Satellite(body("triton"), "neptune", 3.54759e8, false));
    }

    public static List<Satellite> minorMoons() {
        return List.of(
                new Satellite(body("phobos"), "mars", 9.376e6, true),
                new Satellite(body("deimos"), "mars", 2.3463e7, true),
                new Satellite(body("amalthea"), "jupiter", 1.814e8, true),
                new Satellite(body("mimas"), "saturn", 1.8552e8, true),
                new Satellite(body("hyperion"), "saturn", 1.481e9, true));
    }

    public static AsteroidBelt asteroidBelt() {
        return new AsteroidBelt(
                2.2 * AU_METERS,
                3.3 * AU_METERS,
                List.of(body("ceres"), body("vesta"), body("pallas"), body("hygiea")));
    }

    public static boolean inAsteroidBelt(OrbitState orbit) {
        if (orbit == null) {
            throw new IllegalArgumentException("Orbit is required");
        }
        if (!"sun".equals(orbit.centralBody().id())) {
            return false;
        }
        return asteroidBelt().containsRadius(orbit.positionMeters().magnitude());
    }

    public static List<CelestialBody> bodies() {
        List<CelestialBody> all = new ArrayList<>();
        all.add(sun());
        all.addAll(planets());
        for (Satellite moon : majorMoons()) {
            all.add(moon.body());
        }
        for (Satellite moon : minorMoons()) {
            all.add(moon.body());
        }
        all.addAll(asteroidBelt().named());
        return List.copyOf(all);
    }

    public static List<String> ids() {
        return bodies().stream().map(CelestialBody::id).toList();
    }

    public static CelestialBody body(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Body id is required");
        }
        return switch (id) {
            case "sun" -> sun();
            case "mercury" -> mercury();
            case "venus" -> venus();
            case "earth" -> earth();
            case "moon" -> moon();
            case "mars" -> mars();
            case "jupiter" -> jupiter();
            case "saturn" -> saturn();
            case "uranus" -> uranus();
            case "neptune" -> neptune();
            case "io" -> new CelestialBody("io", 5.9599e12, 1.8216e6);
            case "europa" -> new CelestialBody("europa", 3.2027e12, 1.5608e6);
            case "ganymede" -> new CelestialBody("ganymede", 9.8878e12, 2.6341e6);
            case "callisto" -> new CelestialBody("callisto", 7.1795e12, 2.4103e6);
            case "titan" -> new CelestialBody("titan", 8.978e12, 2.5747e6);
            case "rhea" -> new CelestialBody("rhea", 1.5394e11, 7.638e5);
            case "iapetus" -> new CelestialBody("iapetus", 1.205e11, 7.345e5);
            case "dione" -> new CelestialBody("dione", 7.3116e10, 5.614e5);
            case "tethys" -> new CelestialBody("tethys", 4.121e10, 5.311e5);
            case "enceladus" -> new CelestialBody("enceladus", 1.0805e10, 2.521e5);
            case "titania" -> new CelestialBody("titania", 2.269e11, 7.889e5);
            case "oberon" -> new CelestialBody("oberon", 2.053e11, 7.614e5);
            case "triton" -> new CelestialBody("triton", 1.4279e12, 1.3534e6);
            case "phobos" -> new CelestialBody("phobos", 7.11e5, 1.127e4);
            case "deimos" -> new CelestialBody("deimos", 9.62e4, 6.2e3);
            case "amalthea" -> new CelestialBody("amalthea", 1.38e8, 8.35e4);
            case "mimas" -> new CelestialBody("mimas", 2.503e9, 1.982e5);
            case "hyperion" -> new CelestialBody("hyperion", 3.7e9, 1.35e5);
            case "ceres" -> new CelestialBody("ceres", 6.263e10, 4.70e5);
            case "vesta" -> new CelestialBody("vesta", 1.73e10, 2.627e5);
            case "pallas" -> new CelestialBody("pallas", 1.37e10, 2.56e5);
            case "hygiea" -> new CelestialBody("hygiea", 7.0e9, 2.15e5);
            default -> throw new IllegalArgumentException("Unknown body: " + id);
        };
    }

    /** CO₂ column. Surface 610 Pa / 210 K. Not ChemMod. */
    public static Atmosphere marsAir() {
        return new Atmosphere(
                "mars",
                610.0,
                210.0,
                0.0025,
                0.04334,
                3.71,
                3_389_500.0,
                16_000.0);
    }

    public static Atmosphere atmosphereOf(CelestialBody body) {
        if (body == null) {
            throw new IllegalArgumentException("Body is required");
        }
        return switch (body.id()) {
            case "earth" -> Atmosphere.earth();
            case "mars" -> marsAir();
            default -> Atmosphere.vacuum();
        };
    }

    public static OrbitState circular(CelestialBody body, double altitudeMeters) {
        if (body == null) {
            throw new IllegalArgumentException("Body is required");
        }
        if (!Double.isFinite(altitudeMeters) || altitudeMeters < 0.0) {
            throw new IllegalArgumentException("Altitude must be finite and non-negative");
        }
        double radius = body.radiusMeters() + altitudeMeters;
        double speed = Math.sqrt(body.gravitationalParameter() / radius);
        return new OrbitState(
                body,
                new Vector3d(radius, 0.0, 0.0),
                new Vector3d(0.0, 0.0, speed),
                0.0);
    }
}
