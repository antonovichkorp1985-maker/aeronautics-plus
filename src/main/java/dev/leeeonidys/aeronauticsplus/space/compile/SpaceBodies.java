package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.CelestialBody;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitState;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;

/** Shared celestial fixtures for compiler and core slices. */
public final class SpaceBodies {
    private SpaceBodies() {
    }

    public static CelestialBody earth() {
        return new CelestialBody("earth", 3.986004418e14, 6_371_000.0);
    }

    /** Pad rest: on the surface, no Earth rotation yet. */
    public static OrbitState pad() {
        CelestialBody earth = earth();
        return new OrbitState(
                earth,
                new Vector3d(earth.radiusMeters(), 0.0, 0.0),
                Vector3d.ZERO,
                0.0);
    }

    public static OrbitState parkingOrbit() {
        CelestialBody earth = earth();
        double radius = earth.radiusMeters() + 200_000.0;
        double circularSpeed = Math.sqrt(earth.gravitationalParameter() / radius);
        return new OrbitState(
                earth,
                new Vector3d(radius, 0.0, 0.0),
                new Vector3d(0.0, 0.0, circularSpeed),
                0.0);
    }

    /** Periapsis 200 km, apoapsis 500 km, start at periapsis. Vacuum ellipse, not ChemMod air. */
    public static OrbitState ellipticTransfer() {
        CelestialBody earth = earth();
        double periapsis = earth.radiusMeters() + 200_000.0;
        double apoapsis = earth.radiusMeters() + 500_000.0;
        double semiMajor = 0.5 * (periapsis + apoapsis);
        double periapsisSpeed = Math.sqrt(earth.gravitationalParameter() * (2.0 / periapsis - 1.0 / semiMajor));
        return new OrbitState(
                earth,
                new Vector3d(periapsis, 0.0, 0.0),
                new Vector3d(0.0, 0.0, periapsisSpeed),
                0.0);
    }
}
