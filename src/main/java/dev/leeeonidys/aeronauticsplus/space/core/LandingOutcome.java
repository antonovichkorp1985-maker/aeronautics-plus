package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;
import java.util.stream.Collectors;

/** Result of a vacuum suicide-burn landing. Not ChemMod air, not grid fins. */
public record LandingOutcome(
        VesselState vessel,
        double requestedSeconds,
        double elapsedSeconds,
        double altitudeMeters,
        double speedMetersPerSecond,
        double thrustToWeight,
        List<FlightFault> faults) {
    public static final double TOUCHDOWN_SPEED_METERS_PER_SECOND = 5.0;
    public static final double TOUCHDOWN_ALTITUDE_METERS = 5.0;

    public LandingOutcome {
        if (vessel == null) {
            throw new IllegalArgumentException("Landing outcome requires a vessel");
        }
        if (requestedSeconds < 0.0 || elapsedSeconds < 0.0
                || !Double.isFinite(requestedSeconds) || !Double.isFinite(elapsedSeconds)
                || !Double.isFinite(altitudeMeters)
                || speedMetersPerSecond < 0.0 || !Double.isFinite(speedMetersPerSecond)
                || thrustToWeight < 0.0 || !Double.isFinite(thrustToWeight)) {
            throw new IllegalArgumentException("Landing outcome numbers are invalid");
        }
        faults = List.copyOf(faults == null ? List.of() : faults);
    }

    public boolean landed() {
        return faults.isEmpty()
                && altitudeMeters <= TOUCHDOWN_ALTITUDE_METERS
                && speedMetersPerSecond <= TOUCHDOWN_SPEED_METERS_PER_SECOND;
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
