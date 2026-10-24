package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/**
 * Two-body catalogue. Not n-body. Spins may be retrograde (Venus, Uranus).
 * Atmospheres that are not Earth stay 2-layer parametric, same shape ChemMod uses.
 */
public final class SolarSystem {
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
