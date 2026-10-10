package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Open-loop pitch kick, then zero-lift follow of velocity.
 * Guidance only — not ChemMod world gas.
 */
public record PitchProgram(double kickSeconds, double kickRadians, Vector3d downrange) {
    public PitchProgram {
        if (kickSeconds < 0.0 || !Double.isFinite(kickSeconds)) {
            throw new IllegalArgumentException("Pitch-kick time must be finite and non-negative");
        }
        if (kickRadians < 0.0 || kickRadians > Math.PI / 2.0 || !Double.isFinite(kickRadians)) {
            throw new IllegalArgumentException("Pitch kick must be between zero and ninety degrees");
        }
        if (downrange == null || !(downrange.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Downrange direction must be finite and non-zero");
        }
        downrange = downrange.normalized();
    }

    public static PitchProgram vertical() {
        return new PitchProgram(0.0, 0.0, Vector3d.UNIT_Z);
    }

    /** Kick then follow velocity. Scale of a short first-stage program, not a full Soyuz table. */
    public static PitchProgram kickThenTurn() {
        return new PitchProgram(2.0, Math.toRadians(25.0), Vector3d.UNIT_Z);
    }

    public Vector3d aimed(Vector3d radial) {
        if (radial == null || !(radial.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Radial is required");
        }
        Vector3d r = radial.normalized();
        Vector3d east = downrange.subtract(r.multiply(downrange.dot(r)));
        if (!(east.magnitudeSquared() > 1.0e-12)) {
            east = Math.abs(r.x()) < 0.9 ? Vector3d.UNIT_X : Vector3d.UNIT_Z;
            east = east.subtract(r.multiply(east.dot(r)));
        }
        east = east.normalized();
        return r.multiply(Math.cos(kickRadians)).add(east.multiply(Math.sin(kickRadians))).normalized();
    }
}
