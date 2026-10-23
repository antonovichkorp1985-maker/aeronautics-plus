package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;
import java.util.stream.Collectors;

/** Result of a vacuum boostback: kill downrange speed. Not ChemMod air. */
public record BoostbackOutcome(
        VesselState vessel,
        double requestedSeconds,
        double elapsedSeconds,
        double startHorizontal,
        double endHorizontal,
        List<FlightFault> faults) {
    public static final double DONE_HORIZONTAL_METERS_PER_SECOND = 10.0;

    public BoostbackOutcome {
        if (vessel == null) {
            throw new IllegalArgumentException("Boostback outcome requires a vessel");
        }
        if (requestedSeconds < 0.0 || elapsedSeconds < 0.0
                || !Double.isFinite(requestedSeconds) || !Double.isFinite(elapsedSeconds)
                || startHorizontal < 0.0 || !Double.isFinite(startHorizontal)
                || endHorizontal < 0.0 || !Double.isFinite(endHorizontal)) {
            throw new IllegalArgumentException("Boostback outcome numbers are invalid");
        }
        faults = List.copyOf(faults == null ? List.of() : faults);
    }

    public boolean killedDownrange() {
        return faults.isEmpty() && endHorizontal <= DONE_HORIZONTAL_METERS_PER_SECOND;
    }

    public boolean has(String code) {
        return faults.stream().anyMatch(fault -> fault.code().equals(code));
    }

    public String faultText() {
        if (faults.isEmpty()) {
            return "";
        }
        return faults.stream()
                .map(fault -> fault.code() + " @" + fault.atSeconds() + "s: " + fault.message())
                .collect(Collectors.joining("\n"));
    }
}
