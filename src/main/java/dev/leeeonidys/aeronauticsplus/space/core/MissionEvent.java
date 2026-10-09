package dev.leeeonidys.aeronauticsplus.space.core;

/** One coast, burn or staging step on a mission map. */
public record MissionEvent(String id, Kind kind, double durationSeconds, BurnAim aim) {
    public enum Kind { COAST, BURN, SEPARATE }

    public MissionEvent {
        if (id == null || id.isBlank() || kind == null) {
            throw new IllegalArgumentException("Mission event identity is invalid");
        }
        if (kind != Kind.SEPARATE && (durationSeconds < 0.0 || !Double.isFinite(durationSeconds))) {
            throw new IllegalArgumentException("Event duration must be finite and non-negative");
        }
        if (kind == Kind.BURN && aim == null) {
            throw new IllegalArgumentException("Burn events require an aim");
        }
    }

    public static MissionEvent coast(String id, double durationSeconds) {
        return new MissionEvent(id, Kind.COAST, durationSeconds, BurnAim.PROGRADE);
    }

    public static MissionEvent burn(String id, double durationSeconds, BurnAim aim) {
        return new MissionEvent(id, Kind.BURN, durationSeconds, aim);
    }

    public static MissionEvent separate(String id) {
        return new MissionEvent(id, Kind.SEPARATE, 0.0, BurnAim.ENGINE);
    }
}
