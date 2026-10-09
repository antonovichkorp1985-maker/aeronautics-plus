package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Executes a mission map against a vessel. ENGINE burns follow the inertial image of
 * body-frame thrust; other aims are orbit-relative. There is still no orientation GUI.
 */
public final class MissionExecutor {
    public static final double DEFAULT_STEP_SECONDS = 1.0;

    private MissionExecutor() {
    }

    public static VesselState execute(VesselState vessel, MissionPlan plan) {
        return execute(vessel, plan, DEFAULT_STEP_SECONDS);
    }

    public static VesselState execute(VesselState vessel, MissionPlan plan, double stepSeconds) {
        if (vessel == null || plan == null) {
            throw new IllegalArgumentException("Vessel and mission plan are required");
        }
        VesselState state = vessel;
        for (MissionEvent event : plan.events()) {
            state = apply(state, event, stepSeconds);
        }
        return state;
    }

    private static VesselState apply(VesselState vessel, MissionEvent event, double stepSeconds) {
        return switch (event.kind()) {
            case COAST -> VesselDynamics.propagate(vessel, event.durationSeconds(), stepSeconds);
            case BURN -> VesselDynamics.burnActiveStage(
                    vessel, event.durationSeconds(), aim(vessel, event.aim()));
            case SEPARATE -> VesselDynamics.separateActiveStage(vessel);
        };
    }

    public static Vector3d aim(VesselState vessel, BurnAim aim) {
        Vector3d position = vessel.orbit().positionMeters();
        Vector3d velocity = vessel.orbit().velocityMetersPerSecond();
        return switch (aim) {
            case ENGINE -> {
                Vector3d thrust = vessel.inertialThrustNewtons();
                yield thrust.magnitudeSquared() > 0.0 ? thrust : velocity;
            }
            case PROGRADE -> velocity;
            case RETROGRADE -> velocity.multiply(-1.0);
            case RADIAL_OUT -> position;
            case RADIAL_IN -> position.multiply(-1.0);
            case NORMAL -> position.cross(velocity);
            case ANTINORMAL -> velocity.cross(position);
        };
    }
}
