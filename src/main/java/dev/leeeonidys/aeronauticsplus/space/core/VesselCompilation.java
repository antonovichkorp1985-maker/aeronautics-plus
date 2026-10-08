package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/** Immutable result of vessel analysis. */
public record VesselCompilation(List<VesselDiagnostic> diagnostics, List<StageState> stages) {
    public VesselCompilation {
        diagnostics = List.copyOf(diagnostics == null ? List.of() : diagnostics);
        stages = List.copyOf(stages == null ? List.of() : stages);
    }

    public boolean isLaunchable() {
        return diagnostics.stream().noneMatch(diagnostic -> diagnostic.severity() == VesselDiagnostic.Severity.ERROR);
    }
}
