package dev.leeeonidys.aeronauticsplus.space.core;

/** A lumped structural mass in the stage-local frame. */
public record MassElement(String id, double massKg, Vector3d localPositionMeters) {
    public MassElement {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Mass element id must not be blank");
        }
        if (massKg < 0.0 || !Double.isFinite(massKg)) {
            throw new IllegalArgumentException("Mass must be finite and non-negative");
        }
        if (localPositionMeters == null) {
            throw new IllegalArgumentException("Mass element position must not be null");
        }
    }
}
