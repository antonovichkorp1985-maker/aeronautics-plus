package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Stick hold without an orientation GUI. Not ChemMod air.
 */
public final class HoldAim {
    public enum Mode {
        OFF,
        RADIAL,
        PROGRADE,
        RETRO
    }

    private HoldAim() {
    }

    public static Mode cycle(Mode current) {
        Mode[] values = Mode.values();
        return values[(current.ordinal() + 1) % values.length];
    }

    public static Vector3d inertialAim(OrbitState orbit, Mode mode) {
        if (orbit == null || mode == null) {
            throw new IllegalArgumentException("Orbit and hold mode are required");
        }
        if (mode == Mode.OFF) {
            return orbit.radialUnit();
        }
        if (mode == Mode.RADIAL) {
            return orbit.radialUnit();
        }
        Vector3d velocity = orbit.velocityMetersPerSecond();
        if (!(velocity.magnitudeSquared() > 1.0e-8)) {
            return orbit.radialUnit();
        }
        Vector3d prograde = velocity.normalized();
        return mode == Mode.RETRO ? prograde.multiply(-1.0) : prograde;
    }

    public static Attitude attitude(OrbitState orbit, Mode mode) {
        return Attitude.pointing(Vector3d.UNIT_Y, inertialAim(orbit, mode));
    }

    public static String label(Mode mode) {
        return switch (mode) {
            case OFF -> "off";
            case RADIAL -> "radial";
            case PROGRADE -> "prograde";
            case RETRO -> "retro";
        };
    }
}
