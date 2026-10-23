package dev.leeeonidys.aeronauticsplus.space.core;

/** Explainable result of compiling a freeform vessel, instead of a hidden recipe failure. */
public record VesselDiagnostic(Severity severity, String code, String message) {
    public enum Severity { INFO, WARNING, ERROR }
}
