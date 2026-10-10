package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Deterministic pad ascent: Earth atmosphere, sea-level vs vacuum Isp, hold-down
 * clamps, drag, Max-Q, and fairing stay-on in dense air. No orientation GUI.
 */
public final class AscentSlice {
    private AscentSlice() {
    }

    public record Result(
            double padThrustToWeight,
            double altitudeMeters,
            double dragAltitudeLossMeters,
            double peakDynamicPressurePascals,
            boolean heldDown,
            boolean fairingBlockedOnPad) {
    }

    public static Result execute() {
        Atmosphere earth = Atmosphere.earth();
        if (Math.abs(earth.densityKgPerM3(0.0) - 1.225) > 1.0e-12) {
            throw new IllegalStateException("Sea-level density must be 1.225 kg/m³");
        }
        double atScaleHeight = earth.densityKgPerM3(earth.scaleHeightMeters());
        if (Math.abs(atScaleHeight - 1.225 / Math.E) > 1.0e-9) {
            throw new IllegalStateException("Density at scale height must fall by 1/e");
        }
        if (!(earth.densityKgPerM3(200_000.0) < 1.0e-9)) {
            throw new IllegalStateException("200 km must be vacuum for this exponential model");
        }
        if (Math.abs(earth.pressurePascals(0.0) - PropulsionMath.SEA_LEVEL_PRESSURE_PASCALS) > 1.0e-6) {
            throw new IllegalStateException("Sea-level pressure must be 101325 Pa");
        }

        double vacIsp = 300.0;
        double slIsp = 264.0;
        if (Math.abs(PropulsionMath.ispAtPressure(vacIsp, slIsp, 0.0) - vacIsp) > 1.0e-12) {
            throw new IllegalStateException("Vacuum Isp must be recovered at zero ambient pressure");
        }
        if (Math.abs(PropulsionMath.ispAtPressure(vacIsp, slIsp, 101_325.0) - slIsp) > 1.0e-12) {
            throw new IllegalStateException("Sea-level Isp must be recovered at 1 atm");
        }
        EngineState booster = boosterEngine(20_000.0, vacIsp, slIsp);
        if (!(booster.activeThrustNewtonsAt(101_325.0) < booster.activeThrustNewtons() - 1.0)) {
            throw new IllegalStateException("Sea-level thrust must be lower than vacuum thrust");
        }
        if (Math.abs(booster.activeThrustNewtonsAt(0.0) - booster.activeThrustNewtons()) > 1.0e-6) {
            throw new IllegalStateException("Vacuum thrust must match the declared engine thrust");
        }
        if (Math.abs(EngineState.defaultSeaLevelIsp("lh2", 450.0) - 180.0) > 1.0e-12) {
            throw new IllegalStateException("LH2 vacuum nozzles must derate hard at the pad");
        }

        VesselState lifter = stack(booster, 120.0, false);
        AscentOutcome held = VesselDynamics.attemptAscent(
                stack(boosterEngine(5_000.0, 300.0, 264.0), 120.0, false),
                earth, 8.0, VesselDynamics.DEFAULT_ASCENT_STEP_SECONDS, Aerodynamics.ROCKET_CD, 1.0);
        if (!held.has(FlightFault.HOLD_DOWN)
                || held.elapsedSeconds() != 0.0
                || Math.abs(held.vessel().orbit().altitudeMeters()) > 1.0e-6) {
            throw new IllegalStateException("Weak T/W must stay on the clamps: " + held.faultText());
        }

        AscentOutcome flown = VesselDynamics.attemptAscent(
                lifter, earth, 12.0, VesselDynamics.DEFAULT_ASCENT_STEP_SECONDS, Aerodynamics.ROCKET_CD, 1.0);
        if (!flown.completed() || flown.has(FlightFault.HOLD_DOWN) || flown.has(FlightFault.IMPACT)) {
            throw new IllegalStateException("Lifter must leave the pad: " + flown.faultText());
        }
        if (!(flown.padThrustToWeight() > 1.0)) {
            throw new IllegalStateException("Lifter pad T/W must exceed one, got " + flown.padThrustToWeight());
        }
        if (!(flown.vessel().orbit().altitudeMeters() > 100.0)) {
            throw new IllegalStateException("12 s burn must climb, alt=" + flown.vessel().orbit().altitudeMeters());
        }
        if (!(flown.vessel().totalMassKg() < lifter.totalMassKg() - 10.0)) {
            throw new IllegalStateException("Hold-down plus ascent must consume propellant");
        }
        if (!(flown.peakDynamicPressurePascals() > 100.0)) {
            throw new IllegalStateException("Max-Q must be tracked, q=" + flown.peakDynamicPressurePascals());
        }
        if (!(flown.vessel().orbit().epochSeconds() > VesselDynamics.HOLD_DOWN_SECONDS + 11.0)) {
            throw new IllegalStateException("Epoch must include clamp ignition plus flight");
        }

        AscentOutcome vacuumDrag = VesselDynamics.attemptAscent(
                lifter, earth, 12.0, VesselDynamics.DEFAULT_ASCENT_STEP_SECONDS, 0.0, 1.0);
        double dragLoss = vacuumDrag.vessel().orbit().altitudeMeters()
                - flown.vessel().orbit().altitudeMeters();
        if (!(dragLoss > 1.0)) {
            throw new IllegalStateException(
                    "Drag must cut altitude versus Cd=0, loss=" + dragLoss);
        }

        VesselState shrouded = stack(booster, 120.0, true);
        boolean fairingBlocked = false;
        try {
            VesselDynamics.jettisonFairing(shrouded);
            throw new IllegalStateException("Fairing must not drop on the pad");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains(FlightFault.FAIRING_ATMOSPHERE)) {
                throw exception;
            }
            fairingBlocked = true;
        }

        VesselState orbital = new VesselState(
                "orbital-fairing", SpaceBodies.parkingOrbit(), shrouded.stages(), 0, shrouded.attitude());
        VesselState dropped = VesselDynamics.jettisonFairing(orbital);
        if (dropped.activeStage().hasFairing()) {
            throw new IllegalStateException("Parking-orbit jettison must still drop the shroud");
        }
        if (Math.abs(orbital.totalMassKg() - dropped.totalMassKg() - 45.0) > 1.0e-9) {
            throw new IllegalStateException("Vacuum jettison must drop only fairing mass");
        }

        return new Result(
                flown.padThrustToWeight(),
                flown.vessel().orbit().altitudeMeters(),
                dragLoss,
                flown.peakDynamicPressurePascals(),
                held.has(FlightFault.HOLD_DOWN),
                fairingBlocked);
    }

    private static VesselState stack(EngineState engine, double frameKg, boolean fairing) {
        TankState tank = new TankState(
                "tank", 80.0, 1_000.0,
                new PropellantState("rp1", "lox", 200.0, 460.0),
                Vector3d.ZERO);
        List<MassElement> structure = fairing
                ? List.of(
                        new MassElement("frame", frameKg, Vector3d.ZERO),
                        new MassElement("fairing", 45.0, new Vector3d(0.0, 2.0, 0.0), MassElement.Role.FAIRING))
                : List.of(new MassElement("frame", frameKg, Vector3d.ZERO));
        StageState stage = new StageState("stage-0", structure, List.of(tank), List.of(engine), true);
        Attitude radial = Attitude.pointing(Vector3d.UNIT_Y, Vector3d.UNIT_X);
        return new VesselState("ascent-demo", SpaceBodies.pad(), List.of(stage), 0, radial);
    }

    private static EngineState boosterEngine(double thrustNewtons, double vacuumIsp, double seaLevelIsp) {
        return new EngineState(
                "engine", "rp1", "lox", 90.0, thrustNewtons, vacuumIsp, 2.3,
                1.0, 0.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y,
                false, seaLevelIsp);
    }
}
