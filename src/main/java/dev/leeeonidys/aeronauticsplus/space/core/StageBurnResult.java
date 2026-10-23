package dev.leeeonidys.aeronauticsplus.space.core;

/** Result of one deterministic stage burn. */
public record StageBurnResult(StageState stage, double elapsedSeconds,
                              double fuelConsumedKg, double oxidizerConsumedKg,
                              boolean propellantDepleted) {
    public StageBurnResult {
        if (stage == null || elapsedSeconds < 0.0 || !Double.isFinite(elapsedSeconds)
                || fuelConsumedKg < 0.0 || oxidizerConsumedKg < 0.0
                || !Double.isFinite(fuelConsumedKg) || !Double.isFinite(oxidizerConsumedKg)) {
            throw new IllegalArgumentException("Invalid stage burn result");
        }
    }
}
