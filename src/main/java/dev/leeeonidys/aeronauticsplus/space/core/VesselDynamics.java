package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.ArrayList;
import java.util.List;

/** Operations that change a vessel flight state without rendering or Minecraft dependencies. */
public final class VesselDynamics {
    /** Falcon-class ignition on the clamps before release. Saturn V held longer. */
    public static final double HOLD_DOWN_SECONDS = 3.0;
    public static final double DEFAULT_ASCENT_STEP_SECONDS = 0.1;

    private VesselDynamics() {
    }

    public static VesselState applyImpulse(VesselState vessel, Vector3d deltaVelocity) {
        if (deltaVelocity == null) {
            throw new IllegalArgumentException("Delta-v must not be null");
        }
        return vessel.withOrbit(OrbitalSimulator.applyImpulse(
                vessel.orbit(),
                new Maneuver(vessel.orbit().epochSeconds(), deltaVelocity, 0.0)));
    }

    public static VesselState propagate(VesselState vessel, double durationSeconds, double stepSeconds) {
        VesselState orbited = vessel.withOrbit(
                OrbitalSimulator.propagate(vessel.orbit(), durationSeconds, stepSeconds));
        return orbited.withAttitude(vessel.attitude().integrateBodyRate(
                vessel.angularVelocityBody(), durationSeconds));
    }

    /**
     * Burns the active stage along the inertial image of the body-frame net thrust.
     * Offset thrust changes attitude and leaves residual body-frame spin.
     */
    public static VesselState burnActiveStage(VesselState vessel, double durationSeconds) {
        return attemptBurn(vessel, durationSeconds).vessel();
    }

    /** Burns and applies the rocket-equation delta-v in a supplied inertial thrust direction. */
    public static VesselState burnActiveStage(VesselState vessel, double durationSeconds,
                                               Vector3d thrustDirection) {
        return attemptBurn(vessel, durationSeconds, thrustDirection, Double.NaN).vessel();
    }

    public static BurnOutcome attemptBurn(VesselState vessel, double requestedSeconds) {
        Vector3d inertialThrust = vessel.inertialThrustNewtons();
        Vector3d direction = inertialThrust.magnitudeSquared() > 0.0 ? inertialThrust : Vector3d.UNIT_Y;
        return attemptBurn(vessel, requestedSeconds, direction, Double.NaN);
    }

    /**
     * Attempts a burn. Dry tank, zero thrust, wrong propellant and a mid-burn feed break
     * become named faults instead of silent no-ops. {@code feedBreakAtSeconds} is ignored
     * when NaN or negative.
     */
    public static BurnOutcome attemptBurn(VesselState vessel, double requestedSeconds,
                                          Vector3d thrustDirection, double feedBreakAtSeconds) {
        if (requestedSeconds < 0.0 || !Double.isFinite(requestedSeconds)) {
            throw new IllegalArgumentException("Burn duration must be finite and non-negative");
        }
        List<FlightFault> faults = new ArrayList<>();
        if (!(vessel.activeStage().thrustNewtons() > 0.0)) {
            faults.add(FlightFault.zeroThrust());
            return new BurnOutcome(vessel, requestedSeconds, 0.0, 0.0, faults);
        }
        if (!vessel.activeStage().hasCompatibleFeed()) {
            faults.add(FlightFault.wrongPropellant());
            return new BurnOutcome(vessel, requestedSeconds, 0.0, 0.0, faults);
        }
        if (thrustDirection == null || !(thrustDirection.magnitudeSquared() > 0.0)) {
            throw new IllegalArgumentException("Thrust direction must be finite and non-zero");
        }

        boolean feedBreak = Double.isFinite(feedBreakAtSeconds) && feedBreakAtSeconds >= 0.0;
        if (feedBreak && feedBreakAtSeconds == 0.0) {
            VesselState starved = replaceActive(vessel, vessel.activeStage().withDisabledEngines());
            faults.add(FlightFault.feedBreak(0.0));
            return new BurnOutcome(starved, requestedSeconds, 0.0, 0.0, faults);
        }

        double allowed = requestedSeconds;
        if (feedBreak) {
            allowed = Math.min(allowed, feedBreakAtSeconds);
        }

        double initialMass = vessel.totalMassKg();
        double exhaustVelocity = vessel.activeStage().effectiveExhaustVelocityMetersPerSecond();
        ThrustGeometry geometry = vessel.activeThrustGeometry();
        StageBurnResult burn = vessel.activeStage().burn(allowed);
        StageState nextStage = burn.stage();
        if (feedBreak && burn.elapsedSeconds() + 1.0e-9 >= feedBreakAtSeconds
                && burn.elapsedSeconds() + 1.0e-9 < requestedSeconds
                && !burn.propellantDepleted()) {
            nextStage = nextStage.withDisabledEngines();
            faults.add(FlightFault.feedBreak(burn.elapsedSeconds()));
        }
        if (burn.propellantDepleted()) {
            faults.add(FlightFault.dryTank(burn.elapsedSeconds()));
        }

        VesselState consumed = replaceActive(vessel, nextStage);
        double finalMass = consumed.totalMassKg();
        double deltaV = exhaustVelocity > 0.0 && finalMass > 0.0 && initialMass > finalMass
                ? exhaustVelocity * Math.log(initialMass / finalMass) : 0.0;
        VesselState flown = deltaV > 0.0
                ? applyImpulse(consumed, thrustDirection.normalized().multiply(deltaV))
                : consumed;
        flown = applyBurnSpin(vessel, flown, geometry, burn.elapsedSeconds());
        return new BurnOutcome(flown, requestedSeconds, burn.elapsedSeconds(), deltaV, faults);
    }

    public static VesselState consumeActiveStage(VesselState vessel, double durationSeconds) {
        return replaceActive(vessel, vessel.activeStage().burn(durationSeconds).stage());
    }

    public static VesselState separateActiveStage(VesselState vessel) {
        return vessel.separateActiveStage();
    }

    /**
     * Powered ascent from the pad: hold-down if T/W ≤ 1 at sea level, then integrate
     * gravity + ambient thrust + drag. Vacuum impulsive burns stay on {@link #attemptBurn}.
     */
    public static AscentOutcome attemptAscent(
            VesselState vessel, Atmosphere atmosphere, double durationSeconds,
            double stepSeconds, double dragCoefficient, double referenceAreaM2) {
        if (vessel == null || atmosphere == null) {
            throw new IllegalArgumentException("Vessel and atmosphere are required");
        }
        if (durationSeconds < 0.0 || !Double.isFinite(durationSeconds)) {
            throw new IllegalArgumentException("Ascent duration must be finite and non-negative");
        }
        if (!(stepSeconds > 0.0) || !Double.isFinite(stepSeconds)) {
            throw new IllegalArgumentException("Ascent step must be finite and positive");
        }
        if (dragCoefficient < 0.0 || !Double.isFinite(dragCoefficient)
                || referenceAreaM2 < 0.0 || !Double.isFinite(referenceAreaM2)) {
            throw new IllegalArgumentException("Drag coefficient and area must be finite and non-negative");
        }

        List<FlightFault> faults = new ArrayList<>();
        double padPressure = atmosphere.pressurePascals(Math.max(0.0, vessel.orbit().altitudeMeters()));
        double padThrust = vessel.activeStage().thrustNewtonsAt(padPressure);
        double padWeight = vessel.totalMassKg() * vessel.orbit().centralBody().surfaceGravityMetersPerSecond2();
        double padTw = padWeight > 0.0 ? padThrust / padWeight : 0.0;
        if (!(padThrust > padWeight)) {
            faults.add(FlightFault.holdDown(padTw));
            return new AscentOutcome(
                    vessel, durationSeconds, 0.0, 0.0, vessel.orbit().altitudeMeters(), padTw, faults);
        }
        if (!(vessel.activeStage().hasCompatibleFeed())) {
            faults.add(FlightFault.wrongPropellant());
            return new AscentOutcome(
                    vessel, durationSeconds, 0.0, 0.0, vessel.orbit().altitudeMeters(), padTw, faults);
        }
        if (!(padThrust > 0.0)) {
            faults.add(FlightFault.zeroThrust());
            return new AscentOutcome(
                    vessel, durationSeconds, 0.0, 0.0, vessel.orbit().altitudeMeters(), padTw, faults);
        }

        VesselState state = vessel;
        double peakQ = atmosphere.dynamicPressurePascals(state.orbit());
        double peakAlt = state.orbit().altitudeMeters();

        StageBurnResult hold = state.activeStage().burn(HOLD_DOWN_SECONDS);
        state = replaceActive(state, hold.stage()).withOrbit(new OrbitState(
                state.orbit().centralBody(),
                state.orbit().positionMeters(),
                state.orbit().velocityMetersPerSecond(),
                state.orbit().epochSeconds() + hold.elapsedSeconds()));
        if (hold.propellantDepleted()) {
            faults.add(FlightFault.dryTank(hold.elapsedSeconds()));
            return new AscentOutcome(
                    state, durationSeconds, 0.0, peakQ, peakAlt, padTw, faults);
        }

        double remaining = durationSeconds;
        double flown = 0.0;
        while (remaining > 0.0) {
            double dt = Math.min(stepSeconds, remaining);
            OrbitState orbit = state.orbit();
            double mass = state.totalMassKg();
            if (!(mass > 0.0)) {
                break;
            }
            double pressure = atmosphere.pressureAt(orbit);
            double density = atmosphere.densityAt(orbit);
            Vector3d thrust = state.inertialThrustNewtonsAt(pressure);
            if (!(thrust.magnitudeSquared() > 0.0)) {
                faults.add(FlightFault.zeroThrust());
                break;
            }
            Vector3d drag = Aerodynamics.dragForceNewtons(
                    density, orbit.velocityMetersPerSecond(), dragCoefficient, referenceAreaM2);
            Vector3d gravity = OrbitalSimulator.gravitationalAcceleration(
                    orbit.centralBody(), orbit.positionMeters());
            Vector3d acceleration = gravity
                    .add(thrust.multiply(1.0 / mass))
                    .add(drag.multiply(1.0 / mass));
            Vector3d nextPosition = orbit.positionMeters()
                    .add(orbit.velocityMetersPerSecond().multiply(dt))
                    .add(acceleration.multiply(0.5 * dt * dt));
            double nextRadius = nextPosition.magnitude();
            if (nextRadius < orbit.centralBody().radiusMeters()) {
                faults.add(FlightFault.impact(flown + dt));
                break;
            }
            Vector3d nextVelocity = orbit.velocityMetersPerSecond().add(acceleration.multiply(dt));
            StageBurnResult burn = state.activeStage().burn(dt);
            state = replaceActive(state, burn.stage()).withOrbit(new OrbitState(
                    orbit.centralBody(), nextPosition, nextVelocity, orbit.epochSeconds() + dt));
            flown += burn.elapsedSeconds();
            remaining -= burn.elapsedSeconds();
            peakQ = Math.max(peakQ, atmosphere.dynamicPressurePascals(state.orbit()));
            peakAlt = Math.max(peakAlt, state.orbit().altitudeMeters());
            if (burn.propellantDepleted()) {
                faults.add(FlightFault.dryTank(flown));
                break;
            }
            if (burn.elapsedSeconds() + 1.0e-12 < dt) {
                break;
            }
        }

        return new AscentOutcome(state, durationSeconds, flown, peakQ, peakAlt, padTw, faults);
    }

    /**
     * Vacuum gravity turn: declared thrust, no ChemMod air.
     * Vertical, pitch kick, then thrust follows velocity.
     */
    public static GravityTurnOutcome attemptGravityTurn(
            VesselState vessel, PitchProgram program, double durationSeconds, double stepSeconds) {
        if (vessel == null || program == null) {
            throw new IllegalArgumentException("Vessel and pitch program are required");
        }
        if (durationSeconds < 0.0 || !Double.isFinite(durationSeconds)) {
            throw new IllegalArgumentException("Gravity-turn duration must be finite and non-negative");
        }
        if (!(stepSeconds > 0.0) || !Double.isFinite(stepSeconds)) {
            throw new IllegalArgumentException("Gravity-turn step must be finite and positive");
        }

        List<FlightFault> faults = new ArrayList<>();
        double padThrust = vessel.activeStage().thrustNewtons();
        double padWeight = vessel.totalMassKg() * vessel.orbit().centralBody().surfaceGravityMetersPerSecond2();
        double padTw = padWeight > 0.0 ? padThrust / padWeight : 0.0;
        if (!(padThrust > padWeight)) {
            faults.add(FlightFault.holdDown(padTw));
            return snapshotTurn(vessel, durationSeconds, 0.0, padTw, faults);
        }
        if (!(vessel.activeStage().hasCompatibleFeed())) {
            faults.add(FlightFault.wrongPropellant());
            return snapshotTurn(vessel, durationSeconds, 0.0, padTw, faults);
        }
        if (!(padThrust > 0.0)) {
            faults.add(FlightFault.zeroThrust());
            return snapshotTurn(vessel, durationSeconds, 0.0, padTw, faults);
        }

        VesselState state = vessel;
        StageBurnResult hold = state.activeStage().burn(HOLD_DOWN_SECONDS);
        state = replaceActive(state, hold.stage()).withOrbit(new OrbitState(
                state.orbit().centralBody(),
                state.orbit().positionMeters(),
                state.orbit().velocityMetersPerSecond(),
                state.orbit().epochSeconds() + hold.elapsedSeconds()));
        if (hold.propellantDepleted()) {
            faults.add(FlightFault.dryTank(hold.elapsedSeconds()));
            return snapshotTurn(state, durationSeconds, 0.0, padTw, faults);
        }

        double remaining = durationSeconds;
        double flown = 0.0;
        boolean kicked = false;
        while (remaining > 0.0) {
            double dt = Math.min(stepSeconds, remaining);
            OrbitState orbit = state.orbit();
            double mass = state.totalMassKg();
            if (!(mass > 0.0)) {
                break;
            }
            Vector3d radial = orbit.radialUnit();
            Attitude attitude;
            if (!kicked && flown + 1.0e-12 >= program.kickSeconds()) {
                attitude = Attitude.pointing(Vector3d.UNIT_Y, program.aimed(radial));
                kicked = true;
            } else if (kicked && orbit.velocityMetersPerSecond().magnitude() > 5.0) {
                attitude = Attitude.pointing(Vector3d.UNIT_Y, orbit.velocityMetersPerSecond());
            } else {
                attitude = Attitude.pointing(Vector3d.UNIT_Y, radial);
            }
            state = state.withAttitude(attitude);
            Vector3d thrust = state.inertialThrustNewtons();
            if (!(thrust.magnitudeSquared() > 0.0)) {
                faults.add(FlightFault.zeroThrust());
                break;
            }
            Vector3d gravity = OrbitalSimulator.gravitationalAcceleration(
                    orbit.centralBody(), orbit.positionMeters());
            Vector3d acceleration = gravity.add(thrust.multiply(1.0 / mass));
            Vector3d nextPosition = orbit.positionMeters()
                    .add(orbit.velocityMetersPerSecond().multiply(dt))
                    .add(acceleration.multiply(0.5 * dt * dt));
            if (nextPosition.magnitude() < orbit.centralBody().radiusMeters()) {
                faults.add(FlightFault.impact(flown + dt));
                break;
            }
            Vector3d nextVelocity = orbit.velocityMetersPerSecond().add(acceleration.multiply(dt));
            StageBurnResult burn = state.activeStage().burn(dt);
            state = replaceActive(state, burn.stage()).withOrbit(new OrbitState(
                    orbit.centralBody(), nextPosition, nextVelocity, orbit.epochSeconds() + dt))
                    .withAttitude(attitude);
            flown += burn.elapsedSeconds();
            remaining -= burn.elapsedSeconds();
            if (burn.propellantDepleted()) {
                faults.add(FlightFault.dryTank(flown));
                break;
            }
            if (burn.elapsedSeconds() + 1.0e-12 < dt) {
                break;
            }
        }
        return snapshotTurn(state, durationSeconds, flown, padTw, faults);
    }

    private static GravityTurnOutcome snapshotTurn(
            VesselState vessel, double requested, double elapsed, double padTw, List<FlightFault> faults) {
        OrbitState orbit = vessel.orbit();
        return new GravityTurnOutcome(
                vessel, requested, elapsed,
                orbit.horizontalSpeedMetersPerSecond(),
                orbit.flightPathAngleRadians(),
                orbit.altitudeMeters(),
                padTw, faults);
    }

    /**
     * CMG/gyrodyne despin: torque without propellant. Needs a gyro and solar power
     * (ISS гиродины, Soyuz has no CMGs — RCS instead).
     */
    public static VesselState despinWithGyro(VesselState vessel, double seconds) {
        if (vessel == null) {
            throw new IllegalArgumentException("Vessel is required");
        }
        if (seconds < 0.0 || !Double.isFinite(seconds)) {
            throw new IllegalArgumentException("Despin duration must be finite and non-negative");
        }
        if (!vessel.activeStage().hasGyro()) {
            throw new IllegalStateException("No gyrodyne");
        }
        if (!vessel.activeStage().hasSolar()) {
            throw new IllegalStateException("Gyrodyne needs solar power");
        }
        Vector3d omega0 = vessel.angularVelocityBody();
        double rate = omega0.magnitude();
        if (!(rate > 1.0e-12) || seconds == 0.0) {
            return vessel;
        }
        double inertia = vessel.momentOfInertiaKgM2(omega0);
        double torque = StageState.GYRO_TORQUE_NEWTON_METERS * vessel.activeStage().gyroCount();
        double maxDelta = torque / Math.max(inertia, 1.0e-6) * seconds;
        Vector3d omega1 = maxDelta >= rate
                ? Vector3d.ZERO
                : omega0.multiply((rate - maxDelta) / rate);
        Vector3d omegaAvg = omega0.add(omega1).multiply(0.5);
        return vessel.withAttitude(vessel.attitude().integrateBodyRate(omegaAvg, seconds))
                .withAngularVelocity(omega1);
    }

    /** RCS cluster: small thrusters, not the main engine. Consumes propellant. */
    public static VesselState fireRcs(VesselState vessel, double seconds) {
        if (vessel == null) {
            throw new IllegalArgumentException("Vessel is required");
        }
        if (vessel.activeStage().rcsThrusters().isEmpty()) {
            throw new IllegalStateException("No RCS");
        }
        if (!vessel.activeStage().hasCompatibleRcsFeed()) {
            throw new IllegalStateException("RCS has no matching propellant");
        }
        Vector3d bodyThrust = vessel.activeStage().netRcsThrustNewtons();
        if (!(bodyThrust.magnitudeSquared() > 0.0)) {
            throw new IllegalStateException("RCS thrust is zero");
        }
        double initialMass = vessel.totalMassKg();
        StageBurnResult burn = vessel.activeStage().burnRcs(seconds);
        VesselState consumed = replaceActive(vessel, burn.stage());
        double finalMass = consumed.totalMassKg();
        double exhaust = 0.0;
        double thrust = 0.0;
        for (EngineState engine : vessel.activeStage().rcsThrusters()) {
            double t = engine.activeThrustNewtons();
            if (t > 0.0) {
                thrust += t;
                exhaust += t * engine.exhaustVelocityMetersPerSecond();
            }
        }
        double ve = thrust > 0.0 ? exhaust / thrust : 0.0;
        double deltaV = ve > 0.0 && finalMass > 0.0 && initialMass > finalMass
                ? ve * Math.log(initialMass / finalMass) : 0.0;
        Vector3d inertial = vessel.attitude().toInertial(bodyThrust);
        return deltaV > 0.0
                ? applyImpulse(consumed, inertial.normalized().multiply(deltaV))
                : consumed;
    }

    /**
     * Jettisons remaining fairings on every remaining stage. Payload stays.
     * Historical sequence: after leaving dense atmosphere (Soyuz, Falcon 9, Saturn V).
     */
    public static VesselState jettisonFairing(VesselState vessel) {
        return jettisonFairing(vessel, Atmosphere.earth());
    }

    public static VesselState jettisonFairing(VesselState vessel, Atmosphere atmosphere) {
        if (vessel == null) {
            throw new IllegalArgumentException("Vessel is required");
        }
        if (atmosphere == null) {
            throw new IllegalArgumentException("Atmosphere is required");
        }
        boolean hasFairing = false;
        for (StageState stage : vessel.stages()) {
            if (stage.hasFairing()) {
                hasFairing = true;
                break;
            }
        }
        if (!hasFairing) {
            throw new IllegalStateException("No fairing to jettison");
        }
        if (!Aerodynamics.fairingSafe(vessel.orbit(), atmosphere)) {
            throw new IllegalStateException(
                    FlightFault.FAIRING_ATMOSPHERE
                            + ": обтекатель не сбрасывают в плотных слоях (h="
                            + vessel.orbit().altitudeMeters()
                            + " м, q="
                            + atmosphere.dynamicPressurePascals(vessel.orbit())
                            + " Па)");
        }
        ArrayList<StageState> next = new ArrayList<>(vessel.stages().size());
        for (StageState stage : vessel.stages()) {
            next.add(stage.withoutFairings());
        }
        return new VesselState(
                vessel.id(), vessel.orbit(), next, vessel.activeStageIndex(),
                vessel.attitude(), vessel.angularVelocityBody());
    }

    /**
     * Constant-torque rigid rotation over the burn. Average ω updates attitude;
     * the final body rate is kept for coast.
     */
    private static VesselState applyBurnSpin(
            VesselState initial, VesselState flown, ThrustGeometry geometry, double elapsedSeconds) {
        if (!(elapsedSeconds > 0.0)) {
            return flown;
        }
        Vector3d omega0 = initial.angularVelocityBody();
        Vector3d alpha = Vector3d.ZERO;
        if (geometry != null && geometry.hasNetThrust()) {
            Vector3d torque = geometry.momentArmMeters().cross(geometry.netThrustNewtons());
            double tau = torque.magnitude();
            if (tau > 0.0) {
                double inertia = initial.momentOfInertiaKgM2(torque);
                if (inertia > 1.0e-6) {
                    alpha = torque.multiply(1.0 / inertia);
                }
            }
        }
        Vector3d omega1 = omega0.add(alpha.multiply(elapsedSeconds));
        Vector3d omegaAvg = omega0.add(alpha.multiply(0.5 * elapsedSeconds));
        return flown.withAttitude(flown.attitude().integrateBodyRate(omegaAvg, elapsedSeconds))
                .withAngularVelocity(omega1);
    }

    private static VesselState replaceActive(VesselState vessel, StageState nextStage) {
        ArrayList<StageState> stages = new ArrayList<>(vessel.stages());
        stages.set(vessel.activeStageIndex(), nextStage);
        return new VesselState(
                vessel.id(), vessel.orbit(), stages, vessel.activeStageIndex(),
                vessel.attitude(), vessel.angularVelocityBody());
    }
}
