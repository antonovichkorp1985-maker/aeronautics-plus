package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Approximate heliocentric ephemeris for the system map. Planet positions use osculating
 * J2000-class elements and Kepler propagation; moons use circular two-body tracks.
 * This is a navigation display, not an n-body ephemeris or a flight-dynamics substitute.
 */
public final class SolarSystemMap {
    private static final double LOG_RANGE_AU = 40.0;
    private static final double DEG = Math.PI / 180.0;
    private static final List<PlanetOrbit> PLANETS = List.of(
            new PlanetOrbit("mercury", 0.3871, 0.2056, 7.005, 48.331, 29.126, 174.79),
            new PlanetOrbit("venus", 0.7233, 0.0068, 3.395, 76.680, 54.922, 50.38),
            new PlanetOrbit("earth", 1.0000, 0.0167, 0.000, 0.000, 102.937, -2.48),
            new PlanetOrbit("mars", 1.5237, 0.0934, 1.850, 49.558, 286.502, 19.39),
            new PlanetOrbit("jupiter", 5.2028, 0.0489, 1.303, 100.464, 274.289, 19.65),
            new PlanetOrbit("saturn", 9.5388, 0.0565, 2.489, 113.665, 338.766, 317.65),
            new PlanetOrbit("uranus", 19.1914, 0.0463, 0.773, 74.006, 96.958, 143.09),
            new PlanetOrbit("neptune", 30.0611, 0.0095, 1.770, 131.784, 273.187, 259.38));
    private static final List<PlanetOrbit> DWARF_PLANETS = List.of(
            new PlanetOrbit("ceres", 2.767, 0.0758, 10.59, 80.3, 353.4, 96.0),
            new PlanetOrbit("vesta", 2.362, 0.0888, 7.14, 103.9, 47.0, 151.2),
            new PlanetOrbit("pallas", 2.773, 0.2310, 34.84, 173.1, 137.1, 33.0),
            new PlanetOrbit("hygiea", 3.140, 0.1120, 3.83, 283.2, 29.1, 67.7));

    private SolarSystemMap() {
    }

    private record PlanetOrbit(String id, double semiMajorAu, double eccentricity,
                               double inclinationDeg, double nodeDeg, double periapsisDeg,
                               double meanAnomalyDeg) {
    }

    public static double rangeAu() {
        return LOG_RANGE_AU;
    }

    public static List<String> planetIds() {
        return PLANETS.stream().map(PlanetOrbit::id).toList();
    }

    public static List<String> dwarfPlanetIds() {
        return DWARF_PLANETS.stream().map(PlanetOrbit::id).toList();
    }

    public static List<String> satelliteIds() {
        List<String> ids = new ArrayList<>();
        SolarSystem.majorMoons().forEach(satellite -> ids.add(satellite.body().id()));
        SolarSystem.minorMoons().forEach(satellite -> ids.add(satellite.body().id()));
        return List.copyOf(ids);
    }

    /** Heliocentric body-centred position in metres at the supplied simulation epoch. */
    public static Vector3d position(String bodyId, double epochSeconds) {
        return position(bodyId, epochSeconds, 0.0);
    }

    /** Position on the body's mapped orbit; anomalyOffsetRadians walks one full revolution. */
    public static Vector3d orbitPoint(String bodyId, double epochSeconds, double anomalyOffsetRadians) {
        return position(bodyId, epochSeconds, anomalyOffsetRadians);
    }

    private static Vector3d position(String bodyId, double epochSeconds, double anomalyOffsetRadians) {
        if (bodyId == null || bodyId.isBlank() || !Double.isFinite(epochSeconds)
                || !Double.isFinite(anomalyOffsetRadians)) {
            throw new IllegalArgumentException("System-map body and epoch are required");
        }
        if ("sun".equals(bodyId)) {
            return Vector3d.ZERO;
        }
        for (PlanetOrbit orbit : PLANETS) {
            if (orbit.id().equals(bodyId)) {
                return planetPosition(orbit, epochSeconds, anomalyOffsetRadians);
            }
        }
        for (PlanetOrbit orbit : DWARF_PLANETS) {
            if (orbit.id().equals(bodyId)) {
                return planetPosition(orbit, epochSeconds, anomalyOffsetRadians);
            }
        }
        if ("moon".equals(bodyId)) {
            return position("earth", epochSeconds, 0.0)
                    .add(PatchedConics.moonPositionInertial(epochSeconds + anomalyOffsetRadians
                            / meanMotion(SolarSystem.earth(), PatchedConics.MOON_SEMI_MAJOR_METERS)));
        }
        Satellite satellite = findSatellite(bodyId);
        if (satellite != null) {
            CelestialBody primary = SolarSystem.body(satellite.primaryId());
            double motion = meanMotion(primary, satellite.semiMajorMeters());
            double phase = stablePhase(bodyId);
            Vector3d relative = PatchedConics.secondaryPosition(
                    primary, satellite.semiMajorMeters(), epochSeconds + (phase + anomalyOffsetRadians) / motion);
            return position(satellite.primaryId(), epochSeconds, 0.0).add(relative);
        }
        throw new IllegalArgumentException("Unknown system-map body: " + bodyId);
    }

    private static Vector3d planetPosition(PlanetOrbit orbit, double epochSeconds, double offset) {
        CelestialBody sun = SolarSystem.sun();
        double a = orbit.semiMajorAu() * SolarSystem.AU_METERS;
        double meanMotion = meanMotion(sun, a);
        double mean = orbit.meanAnomalyDeg() * DEG + meanMotion * epochSeconds + offset;
        double eccentric = mean;
        for (int i = 0; i < 10; i++) {
            eccentric -= (eccentric - orbit.eccentricity() * Math.sin(eccentric) - mean)
                    / (1.0 - orbit.eccentricity() * Math.cos(eccentric));
        }
        double xOrbital = a * (Math.cos(eccentric) - orbit.eccentricity());
        double zOrbital = a * Math.sqrt(1.0 - orbit.eccentricity() * orbit.eccentricity()) * Math.sin(eccentric);
        double argument = orbit.periapsisDeg() * DEG;
        double node = orbit.nodeDeg() * DEG;
        double inclination = orbit.inclinationDeg() * DEG;
        double xPeri = Math.cos(argument) * xOrbital - Math.sin(argument) * zOrbital;
        double zPeri = Math.sin(argument) * xOrbital + Math.cos(argument) * zOrbital;
        double xInclined = Math.cos(node) * xPeri - Math.sin(node) * Math.cos(inclination) * zPeri;
        double yInclined = Math.sin(node) * Math.sin(inclination) * zPeri;
        double zInclined = Math.sin(node) * xPeri + Math.cos(node) * Math.cos(inclination) * zPeri;
        return new Vector3d(xInclined, yInclined, zInclined);
    }

    private static Satellite findSatellite(String id) {
        for (Satellite satellite : SolarSystem.majorMoons()) {
            if (satellite.body().id().equals(id)) {
                return satellite;
            }
        }
        for (Satellite satellite : SolarSystem.minorMoons()) {
            if (satellite.body().id().equals(id)) {
                return satellite;
            }
        }
        return null;
    }

    private static double stablePhase(String id) {
        return Math.floorMod(id.hashCode(), 360) * DEG;
    }

    private static double meanMotion(CelestialBody primary, double semiMajorMeters) {
        return Math.sqrt(primary.gravitationalParameter()
                / (semiMajorMeters * semiMajorMeters * semiMajorMeters));
    }
}
