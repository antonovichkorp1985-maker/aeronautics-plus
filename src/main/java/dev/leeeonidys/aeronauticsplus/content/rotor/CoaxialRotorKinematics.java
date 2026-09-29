package dev.leeeonidys.aeronauticsplus.content.rotor;

/**
 * Pure animation math for a contra-rotating coaxial pair.
 *
 * <p>The two rotor meshes are intentionally independent runtime objects. They
 * share the kinetic speed supplied by Create, but the lower rotor receives the
 * inverse visual angle. Keeping this calculation client-agnostic lets the same
 * convention be reused by a fallback block-entity renderer and a Flywheel
 * visual.</p>
 */
public final class CoaxialRotorKinematics {
    private CoaxialRotorKinematics() {
    }

    public static RotorAngles interpolateDegrees(float previousAngle, float angle, float partialTick) {
        float baseAngle = 2.0f * (previousAngle * (1.0f - partialTick) + angle * partialTick);
        return new RotorAngles(baseAngle, -baseAngle);
    }

    public record RotorAngles(float upperDegrees, float lowerDegrees) {
    }
}
