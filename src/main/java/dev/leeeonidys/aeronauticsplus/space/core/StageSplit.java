package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * After staging both stacks keep flying: upper continues, booster can land.
 * Falcon-class recovery, not a discarded mass point.
 */
public record StageSplit(VesselState continuing, VesselState booster) {
    public StageSplit {
        if (continuing == null || booster == null) {
            throw new IllegalArgumentException("Split requires both the continuing stack and the booster");
        }
    }
}
