package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;
import java.util.stream.Collectors;

/** Ordered mission map. There is no GUI; the plan is data executed by {@link MissionExecutor}. */
public record MissionPlan(String id, List<MissionEvent> events) {
    public MissionPlan {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Mission plan id must not be blank");
        }
        events = List.copyOf(events == null ? List.of() : events);
        if (events.isEmpty()) {
            throw new IllegalArgumentException("Mission plan must contain at least one event");
        }
    }

    public String describe() {
        return events.stream()
                .map(event -> event.kind() + " " + event.id()
                        + (event.kind() == MissionEvent.Kind.SEPARATE
                        || event.kind() == MissionEvent.Kind.JETTISON_FAIRING
                        ? ""
                        : " t=" + event.durationSeconds() + "s")
                        + (event.kind() == MissionEvent.Kind.BURN ? " aim=" + event.aim() : ""))
                .collect(Collectors.joining(" → "));
    }
}
