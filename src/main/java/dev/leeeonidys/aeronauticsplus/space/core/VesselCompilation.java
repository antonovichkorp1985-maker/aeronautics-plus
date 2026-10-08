package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;
import java.util.stream.Collectors;

/** Immutable result of vessel analysis. */
public record VesselCompilation(List<VesselDiagnostic> diagnostics, List<StageState> stages) {
    public VesselCompilation {
        diagnostics = List.copyOf(diagnostics == null ? List.of() : diagnostics);
        stages = List.copyOf(stages == null ? List.of() : stages);
    }

    public boolean isLaunchable() {
        return !stages.isEmpty()
                && diagnostics.stream().noneMatch(diagnostic -> diagnostic.severity() == VesselDiagnostic.Severity.ERROR);
    }

    public String diagnosticText() {
        if (diagnostics.isEmpty()) {
            return "";
        }
        return diagnostics.stream()
                .map(diagnostic -> diagnostic.severity() + " " + diagnostic.code() + ": " + diagnostic.message())
                .collect(Collectors.joining("\n"));
    }

    public VesselState toVesselState(String vesselId, OrbitState orbit) {
        if (!isLaunchable()) {
            String text = diagnosticText();
            throw new IllegalStateException(text.isBlank()
                    ? "Vessel is not launchable"
                    : "Vessel is not launchable:\n" + text);
        }
        return new VesselState(vesselId, orbit, stages, 0);
    }
}
