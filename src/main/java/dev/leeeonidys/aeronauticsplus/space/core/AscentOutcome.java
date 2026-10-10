package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;
import java.util.stream.Collectors;

/** Result of a pad ascent: hold-down, powered flight, Max-Q. */
public record AscentOutcome(
        VesselState vessel,
        double requestedSeconds,
        double elapsedSeconds,
        double peakDynamicPressurePascals,
        double peakAltitudeMeters,
        double padThrustToWeight,
        List<FlightFault> faults) {
    public AscentOutcome {
        if (vessel == null) {
            throw new IllegalArgumentException("Ascent outcome requires a vessel");
        }
        if (requestedSeconds < 0.0 || elapsedSeconds < 0.0
                || !Double.isFinite(requestedSeconds) || !Double.isFinite(elapsedSeconds)
                || peakDynamicPressurePascals < 0.0 || !Double.isFinite(peakDynamicPressurePascals)
                || !Double.isFinite(peakAltitudeMeters)
                || padThrustToWeight < 0.0 || !Double.isFinite(padThrustToWeight)) {
            throw new IllegalArgumentException("Ascent outcome numbers are invalid");
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
