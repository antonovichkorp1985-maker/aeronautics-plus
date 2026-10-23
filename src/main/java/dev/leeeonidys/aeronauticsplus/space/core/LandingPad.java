package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Surface landing zone (LZ-1 / ASDS class). Not ChemMod air, not grid fins.
 * A soft burn off the deck is {@code OFF_PAD}, not a landing.
 */
public record LandingPad(Vector3d surfaceMeters, double radiusMeters) {
    public static final double DEFAULT_RADIUS_METERS = 100.0;

    public LandingPad {
        if (surfaceMeters == null || !(surfaceMeters.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Landing pad needs a finite surface position");
        }
        if (!(radiusMeters > 0.0) || !Double.isFinite(radiusMeters)) {
            throw new IllegalArgumentException("Landing pad radius must be finite and positive");
        }
    }

    public static LandingPad at(Vector3d surfaceMeters) {
        return new LandingPad(surfaceMeters, DEFAULT_RADIUS_METERS);
    }

    /** Great-circle metres from {@code position} to the pad, on a sphere of {@code bodyRadiusMeters}. */
    public double groundDistanceMeters(Vector3d position, double bodyRadiusMeters) {
        if (position == null || !(position.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Position must be finite and non-zero");
        }
        if (!(bodyRadiusMeters > 0.0) || !Double.isFinite(bodyRadiusMeters)) {
            throw new IllegalArgumentException("Body radius must be finite and positive");
        }
        double dot = Math.max(-1.0, Math.min(1.0, surfaceMeters.normalized().dot(position.normalized())));
        return Math.acos(dot) * bodyRadiusMeters;
    }

    public boolean contains(Vector3d position, double bodyRadiusMeters) {
        return groundDistanceMeters(position, bodyRadiusMeters) <= radiusMeters;
    }
}
