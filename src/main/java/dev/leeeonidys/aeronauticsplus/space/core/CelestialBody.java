package dev.leeeonidys.aeronauticsplus.space.core;

/** Central body for the first two-body orbital model. */
public record CelestialBody(String id, double gravitationalParameter, double radiusMeters) {
    public CelestialBody {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Celestial body id must not be blank");
        }
        if (!(gravitationalParameter > 0.0) || !Double.isFinite(gravitationalParameter)) {
            throw new IllegalArgumentException("Gravitational parameter must be finite and positive");
        }
        if (!(radiusMeters > 0.0) || !Double.isFinite(radiusMeters)) {
            throw new IllegalArgumentException("Radius must be finite and positive");
        }
    }
}
