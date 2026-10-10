package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Logical engine with a stage-local position and installed thrust axis.
 * {@code mixtureRatio}, thrust and Isp are declared stand-ins until ChemMod can
 * say what is in the tank. AP does not solve chamber gas. ChemMod remains the
 * chemistry source. {@code specificImpulseSeconds} is vacuum; sea-level Isp is
 * lower because ambient pressure sits on the nozzle exit (Merlin ~282/311 s).
 */
public record EngineState(
        String id,
        String fuelId,
        String oxidizerId,
        double dryMassKg,
        double thrustNewtons,
        double specificImpulseSeconds,
        double mixtureRatio,
        double throttle,
        double gimbalDegrees,
        boolean enabled,
        Vector3d localPositionMeters,
        Vector3d thrustAxis,
        boolean rcs,
        double seaLevelSpecificImpulseSeconds) {
    public EngineState {
        requireName(id, "Engine");
        requireName(fuelId, "Fuel");
        requireName(oxidizerId, "Oxidizer");
        if (dryMassKg < 0.0 || !Double.isFinite(dryMassKg)) {
            throw new IllegalArgumentException("Engine dry mass must be finite and non-negative");
        }
        if (!(thrustNewtons > 0.0) || !Double.isFinite(thrustNewtons)) {
            throw new IllegalArgumentException("Thrust must be finite and positive");
        }
        if (!(specificImpulseSeconds > 0.0) || !Double.isFinite(specificImpulseSeconds)) {
            throw new IllegalArgumentException("Specific impulse must be finite and positive");
        }
        if (!(mixtureRatio > 0.0) || !Double.isFinite(mixtureRatio)) {
            throw new IllegalArgumentException("Mixture ratio must be finite and positive");
        }
        if (throttle < 0.0 || throttle > 1.0 || !Double.isFinite(throttle)) {
            throw new IllegalArgumentException("Throttle must be between zero and one");
        }
        if (gimbalDegrees < 0.0 || gimbalDegrees > 90.0 || !Double.isFinite(gimbalDegrees)) {
            throw new IllegalArgumentException("Gimbal range must be between zero and ninety degrees");
        }
        if (localPositionMeters == null) {
            throw new IllegalArgumentException("Engine position must not be null");
        }
        if (thrustAxis == null || !(thrustAxis.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Engine thrust axis must be finite and non-zero");
        }
        thrustAxis = thrustAxis.normalized();
        if (!(seaLevelSpecificImpulseSeconds > 0.0) || !Double.isFinite(seaLevelSpecificImpulseSeconds)) {
            throw new IllegalArgumentException("Sea-level specific impulse must be finite and positive");
        }
        if (seaLevelSpecificImpulseSeconds > specificImpulseSeconds + 1.0e-12) {
            throw new IllegalArgumentException("Sea-level Isp cannot exceed vacuum Isp");
        }
    }

    public EngineState(
            String id,
            String fuelId,
            String oxidizerId,
            double dryMassKg,
            double thrustNewtons,
            double specificImpulseSeconds,
            double mixtureRatio,
            double throttle,
            double gimbalDegrees,
            boolean enabled,
            Vector3d localPositionMeters,
            Vector3d thrustAxis) {
        this(
                id, fuelId, oxidizerId, dryMassKg, thrustNewtons, specificImpulseSeconds,
                mixtureRatio, throttle, gimbalDegrees, enabled, localPositionMeters, thrustAxis,
                false, specificImpulseSeconds);
    }

    public EngineState(
            String id,
            String fuelId,
            String oxidizerId,
            double dryMassKg,
            double thrustNewtons,
            double specificImpulseSeconds,
            double mixtureRatio,
            double throttle,
            double gimbalDegrees,
            boolean enabled,
            Vector3d localPositionMeters,
            Vector3d thrustAxis,
            boolean rcs) {
        this(
                id, fuelId, oxidizerId, dryMassKg, thrustNewtons, specificImpulseSeconds,
                mixtureRatio, throttle, gimbalDegrees, enabled, localPositionMeters, thrustAxis,
                rcs, specificImpulseSeconds);
    }

    /**
     * Kerolox booster class ~0.88 of vacuum (Merlin 282/311, F-1 263/304).
     * LH2 vacuum nozzles (RL10) are much worse at the pad.
     */
    public static double defaultSeaLevelIsp(String fuelId, double vacuumIspSeconds) {
        if (!(vacuumIspSeconds > 0.0) || !Double.isFinite(vacuumIspSeconds)) {
            throw new IllegalArgumentException("Vacuum Isp must be finite and positive");
        }
        if (fuelId != null && fuelId.equalsIgnoreCase("lh2")) {
            return vacuumIspSeconds * 0.40;
        }
        return vacuumIspSeconds * 0.88;
    }

    public double activeThrustNewtons() {
        return enabled ? thrustNewtons * throttle : 0.0;
    }

    public double specificImpulseSecondsAt(double ambientPascals) {
        return PropulsionMath.ispAtPressure(
                specificImpulseSeconds, seaLevelSpecificImpulseSeconds, ambientPascals);
    }

    /** Constant throat mass flow: thrust scales with Isp(p) / Isp_vac. */
    public double activeThrustNewtonsAt(double ambientPascals) {
        if (!enabled) {
            return 0.0;
        }
        return activeThrustNewtons()
                * specificImpulseSecondsAt(ambientPascals) / specificImpulseSeconds;
    }

    public double exhaustVelocityMetersPerSecond() {
        return PropulsionMath.exhaustVelocityFromSpecificImpulse(specificImpulseSeconds);
    }

    public double propellantFlowKgPerSecond() {
        return activeThrustNewtons() / exhaustVelocityMetersPerSecond();
    }

    public double fuelFlowKgPerSecond() {
        return propellantFlowKgPerSecond() / (1.0 + mixtureRatio);
    }

    public double oxidizerFlowKgPerSecond() {
        return propellantFlowKgPerSecond() * mixtureRatio / (1.0 + mixtureRatio);
    }

    public Vector3d activeThrustVectorNewtons() {
        return thrustAxis.multiply(activeThrustNewtons());
    }

    public Vector3d activeThrustVectorNewtonsAt(double ambientPascals) {
        return thrustAxis.multiply(activeThrustNewtonsAt(ambientPascals));
    }

    public EngineState withEnabled(boolean nextEnabled) {
        return new EngineState(
                id, fuelId, oxidizerId, dryMassKg, thrustNewtons, specificImpulseSeconds,
                mixtureRatio, throttle, gimbalDegrees, nextEnabled, localPositionMeters, thrustAxis,
                rcs, seaLevelSpecificImpulseSeconds);
    }

    public EngineState withThrustAxis(Vector3d nextAxis) {
        return new EngineState(
                id, fuelId, oxidizerId, dryMassKg, thrustNewtons, specificImpulseSeconds,
                mixtureRatio, throttle, gimbalDegrees, enabled, localPositionMeters, nextAxis,
                rcs, seaLevelSpecificImpulseSeconds);
    }

    /**
     * Deflect the nozzle toward {@code bodyDesired}, clamped to {@code gimbalDegrees}.
     * Merlin / RS-25 class is ~8–15°. Zero gimbal is a fixed nozzle.
     */
    public Vector3d gimballedAxisToward(Vector3d bodyDesired) {
        if (bodyDesired == null || !(bodyDesired.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Gimbal command must be finite and non-zero");
        }
        Vector3d rest = thrustAxis;
        Vector3d desired = bodyDesired.normalized();
        double maxAngle = Math.toRadians(gimbalDegrees);
        if (maxAngle <= 1.0e-12) {
            return rest;
        }
        double dot = Math.max(-1.0, Math.min(1.0, rest.dot(desired)));
        double angle = Math.acos(dot);
        if (angle <= maxAngle + 1.0e-12) {
            return desired;
        }
        Vector3d hinge = rest.cross(desired);
        if (!(hinge.magnitudeSquared() > 1.0e-16)) {
            Vector3d ortho = Math.abs(rest.x()) < 0.9 ? Vector3d.UNIT_X : Vector3d.UNIT_Y;
            hinge = rest.cross(ortho);
        }
        return Quaternion.fromAxisAngle(hinge, maxAngle).rotate(rest).normalized();
    }

    public EngineState gimbalToward(Vector3d bodyDesired) {
        return withThrustAxis(gimballedAxisToward(bodyDesired));
    }

    public double angleFrom(Vector3d otherAxis) {
        if (otherAxis == null || !(otherAxis.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Comparison axis must be finite and non-zero");
        }
        double dot = Math.max(-1.0, Math.min(1.0, thrustAxis.dot(otherAxis.normalized())));
        return Math.acos(dot);
    }

    private static void requireName(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " id must not be blank");
        }
    }
}
