package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;
import java.util.stream.Collectors;

/** Result of an attempted burn, including any in-flight faults. */
public record BurnOutcome(
        VesselState vessel,
        double requestedSeconds,
        double elapsedSeconds,
        double deltaVMetersPerSecond,
        List<FlightFault> faults) {
    public BurnOutcome {
        if (vessel == null) {
            throw new IllegalArgumentException("Burn outcome requires a vessel");
        }
        if (requestedSeconds < 0.0 || elapsedSeconds < 0.0
                || !Double.isFinite(requestedSeconds) || !Double.isFinite(elapsedSeconds)
                || deltaVMetersPerSecond < 0.0 || !Double.isFinite(deltaVMetersPerSecond)) {
            throw new IllegalArgumentException("Burn outcome numbers are invalid");
        }
        faults = List.copyOf(faults == null ? List.of() : faults);
    }

    public boolean completed() {
        return faults.isEmpty() && elapsedSeconds + 1.0e-9 >= requestedSeconds;
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
