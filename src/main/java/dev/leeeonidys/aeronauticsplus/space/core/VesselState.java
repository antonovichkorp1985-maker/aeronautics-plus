package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/** Logical flight state of a multi-stage vessel. Stages are ordered bottom to top. */
public record VesselState(String id, OrbitState orbit, List<StageState> stages, int activeStageIndex) {
    public VesselState {
        if (id == null || id.isBlank() || orbit == null) {
            throw new IllegalArgumentException("Vessel identity and orbit are required");
        }
        stages = List.copyOf(stages == null ? List.of() : stages);
        if (stages.isEmpty()) {
            throw new IllegalArgumentException("Vessel must contain at least one stage");
        }
        if (activeStageIndex < 0 || activeStageIndex >= stages.size()) {
            throw new IllegalArgumentException("Active stage index is outside the vessel");
        }
    }

    public StageState activeStage() {
        return stages.get(activeStageIndex);
    }

    public double totalMassKg() {
        return stages.stream().mapToDouble(StageState::totalMassKg).sum();
    }

    public double remainingDeltaV() {
        return stages.subList(activeStageIndex, stages.size()).stream()
                .mapToDouble(StageState::idealDeltaV).sum();
    }

    public VesselState withOrbit(OrbitState nextOrbit) {
        return new VesselState(id, nextOrbit, stages, activeStageIndex);
    }

    /** Drops the currently active stage and activates the next one. */
    public VesselState separateActiveStage() {
        if (activeStageIndex >= stages.size() - 1) {
            throw new IllegalStateException("Cannot separate the final active stage");
        }
        List<StageState> remaining = List.copyOf(stages.subList(activeStageIndex + 1, stages.size()));
        return new VesselState(id, orbit, remaining, 0);
    }
}
