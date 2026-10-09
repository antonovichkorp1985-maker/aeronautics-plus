package dev.leeeonidys.aeronauticsplus.space.core;

/** Explicit connection between two freely placed vessel components. */
public record VesselConnection(String fromId, String toId, ConnectionKind kind) {
    public enum ConnectionKind { STRUCTURAL, FUEL, OXIDIZER, ELECTRIC, THERMAL, DATA, DOCKING, SEPARATION }

    public VesselConnection {
        if (fromId == null || fromId.isBlank() || toId == null || toId.isBlank() || kind == null) {
            throw new IllegalArgumentException("Vessel connection is invalid");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("A component cannot connect to itself");
        }
    }
}
