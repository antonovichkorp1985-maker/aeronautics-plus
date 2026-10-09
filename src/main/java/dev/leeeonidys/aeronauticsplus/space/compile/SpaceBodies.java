package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.CelestialBody;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitState;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;

/** Shared celestial fixtures for compiler and core slices. */
public final class SpaceBodies {
    private SpaceBodies() {
    }

    public static OrbitState parkingOrbit() {
        CelestialBody earth = new CelestialBody("earth", 3.986004418e14, 6_371_000.0);
        double radius = earth.radiusMeters() + 200_000.0;
        double circularSpeed = Math.sqrt(earth.gravitationalParameter() / radius);
        return new OrbitState(
                earth,
                new Vector3d(radius, 0.0, 0.0),
                new Vector3d(0.0, 0.0, circularSpeed),
                0.0);
    }
}
