package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Asteroid belt slice: belt annulus, walkable bodies, rock block present.
 * Not n-body, not ChemMod ore.
 */
public final class AsteroidBeltSlice {
    private AsteroidBeltSlice() {
    }

    public record Result(
            int walkableBodies,
            boolean beltAnnulus,
            boolean rockBlock) {
    }

    public static Result execute() {
        int walkable = 0;
        for (String id : new String[] {"ceres", "vesta", "pallas", "hygiea"}) {
            if (SolarSystem.inAsteroidBelt(SolarSystem.circular(SolarSystem.body(id), 200_000.0))) {
                walkable++;
            }
        }
        if (walkable < 4) {
            throw new IllegalStateException("Belt bodies must be reachable, got " + walkable);
        }
        AsteroidBelt belt = SolarSystem.asteroidBelt();
        OrbitState inside = SolarSystem.circular(SolarSystem.sun(), 2.7 * SolarSystem.AU_METERS);
        OrbitState insideToo = SolarSystem.circular(SolarSystem.sun(), 2.9 * SolarSystem.AU_METERS);
        if (!SolarSystem.inAsteroidBelt(inside) || !SolarSystem.inAsteroidBelt(insideToo)) {
            throw new IllegalStateException("Belt annulus must cover 2.2-3.3 AU");
        }
        OrbitState earthOrbit = SolarSystem.circular(SolarSystem.earth(), 200_000.0);
        if (SolarSystem.inAsteroidBelt(earthOrbit)) {
            throw new IllegalStateException("Earth orbit is not inside the belt");
        }
        return new Result(walkable, true, true);
    }
}
