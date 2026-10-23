package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;
import java.util.stream.Collectors;

/** Result of a vacuum gravity turn: pitch kick, then follow velocity. */
public record GravityTurnOutcome(
        VesselState vessel,
        double requestedSeconds,
        double elapsedSeconds,
        double horizontalSpeedMetersPerSecond,
        double flightPathAngleRadians,
        double altitudeMeters,
        double padThrustToWeight,
        List<FlightFault> faults) {
    public GravityTurnOutcome {
        if (vessel == null) {
            throw new IllegalArgumentException("Gravity-turn outcome requires a vessel");
        }
        if (requestedSeconds < 0.0 || elapsedSeconds < 0.0
                || !Double.isFinite(requestedSeconds) || !Double.isFinite(elapsedSeconds)
                || horizontalSpeedMetersPerSecond < 0.0 || !Double.isFinite(horizontalSpeedMetersPerSecond)
                || !Double.isFinite(flightPathAngleRadians)
                || !Double.isFinite(altitudeMeters)
                || padThrustToWeight < 0.0 || !Double.isFinite(padThrustToWeight)) {
            throw new IllegalArgumentException("Gravity-turn outcome numbers are invalid");
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
