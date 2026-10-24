package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Moon or small companion. Two-body around {@code primaryId}. Not n-body.
 * {@code mapOnly} bodies are Kepler dots, not a later walkable world.
 */
public record Satellite(
        CelestialBody body,
        String primaryId,
        double semiMajorMeters,
        boolean mapOnly) {
    public Satellite {
        if (body == null) {
            throw new IllegalArgumentException("Satellite body is required");
        }
        if (primaryId == null || primaryId.isBlank()) {
            throw new IllegalArgumentException("Primary id is required");
        }
        if (!(semiMajorMeters > 0.0) || !Double.isFinite(semiMajorMeters)) {
            throw new IllegalArgumentException("Semi-major axis must be finite and positive");
        }
    }
}
