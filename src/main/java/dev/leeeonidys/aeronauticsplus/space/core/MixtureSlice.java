package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import java.util.List;

/**
 * Deterministic proof that burns consume propellant at the engine's declared O/F
 * and refuse a mismatched composition. ChemMod remains the chemistry source.
 */
public final class MixtureSlice {
    private MixtureSlice() {
    }

    public record Result(
            double designFuelKg,
            double designOxidizerKg,
            double leftoverFuelKg,
            String wrongPropellant) {
    }

    public static Result execute() {
        VesselState matched = vessel("mixture-demo", "rp1", "lox", 2.0, 40.0, 80.0);
        BurnOutcome design = VesselDynamics.attemptBurn(matched, 2.0);
        if (!design.completed() || !(design.deltaVMetersPerSecond() > 0.0)) {
            throw new IllegalStateException("Matching mixture must complete a short burn");
        }
        PropellantState after = design.vessel().activeStage().tanks().get(0).contents();
        double designFuel = 40.0 - after.fuelMassKg();
        double designOxidizer = 80.0 - after.oxidizerMassKg();
        if (designFuel < 1.0 || Math.abs(designOxidizer / designFuel - 2.0) > 1.0e-6) {
            throw new IllegalStateException(
                    "Declared O/F 2.0 must consume oxidizer at twice the fuel rate, got "
                            + designFuel + " / " + designOxidizer);
        }
        if (Math.abs(after.mixtureRatio() - 2.0) > 1.0e-6) {
            throw new IllegalStateException("A stoichiometric tank must stay at O/F 2.0 after the burn");
        }

        VesselState rich = vessel("mixture-starve", "rp1", "lox", 2.0, 50.0, 20.0);
        double richMass = rich.totalMassKg();
        BurnOutcome starved = VesselDynamics.attemptBurn(rich, 1_000.0);
        PropellantState leftover = starved.vessel().activeStage().tanks().get(0).contents();
        if (!starved.has(FlightFault.DRY_TANK) || leftover.oxidizerMassKg() > 1.0e-6
                || leftover.fuelMassKg() < 30.0) {
            throw new IllegalStateException(
                    "Oxidizer-limited burn must stop with leftover fuel: " + starved.faultText());
        }
        if (!(starved.vessel().totalMassKg() < richMass - 1.0)) {
            throw new IllegalStateException("Starved burn must still consume the available oxidizer pair");
        }

        VesselState stranger = vessel(
                "mixture-stranger",
                List.of(
                        tank("rp1-tank", "rp1", "lox", 40.0, 80.0),
                        tank("ch4-tank", "ch4", "lox", 30.0, 70.0)),
                engine("engine", "rp1", "lox", 2.0));
        double strangerCh4 = stranger.activeStage().tanks().get(1).totalMassKg();
        BurnOutcome isolated = VesselDynamics.attemptBurn(stranger, 2.0);
        if (!isolated.completed()) {
            throw new IllegalStateException("Matching tank must still burn when a stranger tank is present");
        }
        TankState leftoverStranger = isolated.vessel().activeStage().tanks().get(1);
        if (Math.abs(leftoverStranger.totalMassKg() - strangerCh4) > 1.0e-9) {
            throw new IllegalStateException("A mismatched tank must not be drained by a compatible engine");
        }

        VesselState wrong = vessel("mixture-wrong", "ch4", "lox", 3.5, 50.0, 100.0);
        double wrongMass = wrong.totalMassKg();
        double wrongSpeed = wrong.orbit().velocityMetersPerSecond().magnitude();
        BurnOutcome rejected = VesselDynamics.attemptBurn(wrong, 5.0);
        if (!rejected.has(FlightFault.WRONG_PROPELLANT)
                || rejected.elapsedSeconds() != 0.0
                || Math.abs(rejected.vessel().totalMassKg() - wrongMass) > 1.0e-9
                || Math.abs(rejected.vessel().orbit().velocityMetersPerSecond().magnitude() - wrongSpeed) > 1.0e-9) {
            throw new IllegalStateException(
                    "Wrong propellant must cancel the burn and change nothing: " + rejected.faultText());
        }

        return new Result(designFuel, designOxidizer, leftover.fuelMassKg(), rejected.faultText());
    }

    private static VesselState vessel(
            String id, String fuelId, String oxidizerId, double mixtureRatio,
            double fuelKg, double oxidizerKg) {
        return vessel(id, List.of(tank("tank", "rp1", "lox", fuelKg, oxidizerKg)),
                engine("engine", fuelId, oxidizerId, mixtureRatio));
    }

    private static VesselState vessel(String id, List<TankState> tanks, EngineState engine) {
        StageState stage = new StageState(
                "stage-0",
                List.of(new MassElement("frame", 120.0, Vector3d.ZERO)),
                tanks,
                List.of(engine),
                true);
        return new VesselState(id, SpaceBodies.parkingOrbit(), List.of(stage), 0);
    }

    private static TankState tank(String id, String fuelId, String oxidizerId, double fuelKg, double oxidizerKg) {
        return new TankState(
                id, 80.0, 1_000.0,
                new PropellantState(fuelId, oxidizerId, fuelKg, oxidizerKg),
                Vector3d.ZERO);
    }

    private static EngineState engine(String id, String fuelId, String oxidizerId, double mixtureRatio) {
        return new EngineState(
                id, fuelId, oxidizerId, 90.0, 20_000.0, 300.0, mixtureRatio,
                1.0, 0.0, true, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
    }
}
