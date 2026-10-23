package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.List;

/**
 * Deterministic vertical slice for the Minecraft-free core: freeform blueprint → stages → burn
 * with mass change, automatic thrust direction, orbit change and staging.
 * Physical block compilation lives in {@code VesselCompileSlice}.
 */
public final class SpaceCoreSlice {
    private SpaceCoreSlice() {
    }

    public record Result(
            VesselCompilation compilation,
            VesselState afterBoosterBurn,
            VesselState afterSeparation,
            VesselState afterUpperBurn,
            double boosterDeltaVMetersPerSecond,
            String brokenDiagnosticText) {
    }

    public static Result execute() {
        CelestialBody earth = new CelestialBody("earth", 3.986004418e14, 6_371_000.0);
        double radius = earth.radiusMeters() + 200_000.0;
        double circularSpeed = Math.sqrt(earth.gravitationalParameter() / radius);
        OrbitState parking = new OrbitState(
                earth,
                new Vector3d(radius, 0.0, 0.0),
                new Vector3d(0.0, 0.0, circularSpeed),
                0.0);

        VesselCompilation compilation = twoStageVessel().analyze();
        if (!compilation.isLaunchable()) {
            throw new IllegalStateException("Reference vessel must be launchable:\n" + compilation.diagnosticText());
        }
        if (compilation.stages().size() != 2) {
            throw new IllegalStateException("Reference vessel must compile into two stages");
        }

        VesselState initial = compilation.toVesselState("slice-demo", parking);
        double massBefore = initial.totalMassKg();
        double vzBefore = initial.orbit().velocityMetersPerSecond().z();
        ThrustGeometry geometry = initial.activeThrustGeometry();
        if (!geometry.hasNetThrust() || geometry.hasMaterialOffset()) {
            throw new IllegalStateException("Reference booster thrust geometry is invalid");
        }

        VesselState afterBooster = VesselDynamics.burnActiveStage(initial, 8.0);
        double massAfterBurn = afterBooster.totalMassKg();
        double vyAfter = afterBooster.orbit().velocityMetersPerSecond().y();
        if (!(massAfterBurn < massBefore - 1.0)) {
            throw new IllegalStateException("Booster burn must consume propellant");
        }
        if (!(vyAfter > 1.0)) {
            throw new IllegalStateException("Automatic +Y thrust must change inertial velocity");
        }
        if (!(afterBooster.orbit().velocityMetersPerSecond().z() > vzBefore * 0.5)) {
            throw new IllegalStateException("Circular velocity component must remain after a short burn");
        }

        VesselState afterSeparation = VesselDynamics.separateActiveStage(afterBooster);
        if (afterSeparation.stages().size() != 1) {
            throw new IllegalStateException("Separation must drop the booster and keep the upper stage");
        }
        if (!(afterSeparation.totalMassKg() < massAfterBurn - 1.0)) {
            throw new IllegalStateException("Separation must drop booster mass");
        }

        Vector3d prograde = afterSeparation.orbit().velocityMetersPerSecond();
        VesselState afterUpper = VesselDynamics.burnActiveStage(afterSeparation, 4.0, prograde);
        if (!(afterUpper.totalMassKg() < afterSeparation.totalMassKg() - 0.5)) {
            throw new IllegalStateException("Upper-stage burn must consume propellant");
        }
        if (!(afterUpper.orbit().velocityMetersPerSecond().magnitude()
                > afterSeparation.orbit().velocityMetersPerSecond().magnitude())) {
            throw new IllegalStateException("Prograde burn must raise inertial speed");
        }

        VesselState propagated = VesselDynamics.propagate(afterUpper, 60.0, 1.0);
        if (!(propagated.orbit().epochSeconds() > afterUpper.orbit().epochSeconds() + 59.0)) {
            throw new IllegalStateException("Propagation must advance epoch");
        }

        String brokenText = brokenVessel().analyze().diagnosticText();
        if (!brokenText.contains("NO_FUEL_PATH") || !brokenText.contains("STRUCTURAL_DISCONNECT")) {
            throw new IllegalStateException(
                    "Broken vessel must explain missing fuel path and structural break:\n" + brokenText);
        }

        String offsetText = offsetVessel().analyze().diagnosticText();
        if (!offsetText.contains("THRUST_OFFSET")) {
            throw new IllegalStateException("Offset engines must warn about thrust offset:\n" + offsetText);
        }

        String cancelledText = cancelledThrustVessel().analyze().diagnosticText();
        if (!cancelledText.contains("ZERO_NET_THRUST")) {
            throw new IllegalStateException("Opposing engines must report zero net thrust:\n" + cancelledText);
        }

        double boosterDeltaV = afterBooster.orbit().velocityMetersPerSecond()
                .subtract(initial.orbit().velocityMetersPerSecond())
                .magnitude();
        return new Result(compilation, afterBooster, afterSeparation, afterUpper, boosterDeltaV, brokenText);
    }

    private static VesselBlueprint twoStageVessel() {
        TankState boosterTank = new TankState(
                "booster-tank", 200.0, 7_000.0,
                new PropellantState("rp1", "lox", 2_000.0, 4_000.0),
                new Vector3d(0.0, -0.5, 0.0));
        EngineState boosterEngine = engine(
                "booster-engine", 150.0, 50_000.0, 280.0, new Vector3d(0.0, -1.5, 0.0), Vector3d.UNIT_Y);
        TankState upperTank = new TankState(
                "upper-tank", 80.0, 1_400.0,
                new PropellantState("rp1", "lox", 400.0, 800.0),
                new Vector3d(0.0, 1.0, 0.0));
        EngineState upperEngine = engine(
                "upper-engine", 80.0, 15_000.0, 320.0, new Vector3d(0.0, 0.6, 0.0), Vector3d.UNIT_Y);

        return new VesselBlueprint(
                List.of(
                        VesselComponent.structure(
                                "booster-frame", "booster", VesselComponent.ComponentKind.STRUCTURE,
                                500.0, Vector3d.ZERO),
                        VesselComponent.tank("booster-tank", "booster", boosterTank),
                        VesselComponent.engine("booster-engine", "booster", boosterEngine),
                        VesselComponent.structure(
                                "upper-frame", "upper", VesselComponent.ComponentKind.STRUCTURE,
                                200.0, new Vector3d(0.0, 1.5, 0.0)),
                        VesselComponent.tank("upper-tank", "upper", upperTank),
                        VesselComponent.engine("upper-engine", "upper", upperEngine)),
                List.of(
                        new VesselConnection("booster-frame", "booster-tank", VesselConnection.ConnectionKind.STRUCTURAL),
                        new VesselConnection("booster-tank", "booster-engine", VesselConnection.ConnectionKind.STRUCTURAL),
                        new VesselConnection("booster-tank", "booster-engine", VesselConnection.ConnectionKind.FUEL),
                        new VesselConnection("booster-tank", "booster-engine", VesselConnection.ConnectionKind.OXIDIZER),
                        new VesselConnection("upper-frame", "upper-tank", VesselConnection.ConnectionKind.STRUCTURAL),
                        new VesselConnection("upper-tank", "upper-engine", VesselConnection.ConnectionKind.STRUCTURAL),
                        new VesselConnection("upper-tank", "upper-engine", VesselConnection.ConnectionKind.FUEL),
                        new VesselConnection("upper-tank", "upper-engine", VesselConnection.ConnectionKind.OXIDIZER)));
    }

    private static VesselBlueprint brokenVessel() {
        TankState tank = new TankState(
                "lone-tank", 50.0, 100.0,
                new PropellantState("rp1", "lox", 10.0, 20.0),
                Vector3d.ZERO);
        EngineState engine = engine(
                "orphan-engine", 40.0, 10_000.0, 300.0, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
        return new VesselBlueprint(
                List.of(
                        VesselComponent.tank("lone-tank", "broken", tank),
                        VesselComponent.engine("orphan-engine", "broken", engine)),
                List.of());
    }

    private static VesselBlueprint offsetVessel() {
        TankState tank = new TankState(
                "offset-tank", 100.0, 1_000.0,
                new PropellantState("rp1", "lox", 200.0, 400.0),
                Vector3d.ZERO);
        EngineState engine = engine(
                "offset-engine", 80.0, 20_000.0, 300.0, new Vector3d(2.0, -1.0, 0.0), Vector3d.UNIT_Y);
        return new VesselBlueprint(
                List.of(
                        VesselComponent.structure(
                                "offset-frame", "offset", VesselComponent.ComponentKind.STRUCTURE, 400.0, Vector3d.ZERO),
                        VesselComponent.tank("offset-tank", "offset", tank),
                        VesselComponent.engine("offset-engine", "offset", engine)),
                List.of(
                        new VesselConnection("offset-frame", "offset-tank", VesselConnection.ConnectionKind.STRUCTURAL),
                        new VesselConnection("offset-tank", "offset-engine", VesselConnection.ConnectionKind.STRUCTURAL),
                        new VesselConnection("offset-tank", "offset-engine", VesselConnection.ConnectionKind.FUEL),
                        new VesselConnection("offset-tank", "offset-engine", VesselConnection.ConnectionKind.OXIDIZER)));
    }

    private static VesselBlueprint cancelledThrustVessel() {
        TankState tank = new TankState(
                "cancel-tank", 100.0, 1_000.0,
                new PropellantState("rp1", "lox", 200.0, 400.0),
                Vector3d.ZERO);
        EngineState up = engine("up-engine", 40.0, 10_000.0, 300.0, new Vector3d(0.0, -1.0, 0.0), Vector3d.UNIT_Y);
        EngineState down = engine(
                "down-engine", 40.0, 10_000.0, 300.0, new Vector3d(0.0, 1.0, 0.0), new Vector3d(0.0, -1.0, 0.0));
        return new VesselBlueprint(
                List.of(
                        VesselComponent.structure(
                                "cancel-frame", "cancel", VesselComponent.ComponentKind.STRUCTURE, 200.0, Vector3d.ZERO),
                        VesselComponent.tank("cancel-tank", "cancel", tank),
                        VesselComponent.engine("up-engine", "cancel", up),
                        VesselComponent.engine("down-engine", "cancel", down)),
                List.of(
                        new VesselConnection("cancel-frame", "cancel-tank", VesselConnection.ConnectionKind.STRUCTURAL),
                        new VesselConnection("cancel-tank", "up-engine", VesselConnection.ConnectionKind.STRUCTURAL),
                        new VesselConnection("cancel-tank", "down-engine", VesselConnection.ConnectionKind.STRUCTURAL),
                        new VesselConnection("cancel-tank", "up-engine", VesselConnection.ConnectionKind.FUEL),
                        new VesselConnection("cancel-tank", "up-engine", VesselConnection.ConnectionKind.OXIDIZER),
                        new VesselConnection("cancel-tank", "down-engine", VesselConnection.ConnectionKind.FUEL),
                        new VesselConnection("cancel-tank", "down-engine", VesselConnection.ConnectionKind.OXIDIZER)));
    }

    private static EngineState engine(
            String id, double dryMassKg, double thrustNewtons, double ispSeconds,
            Vector3d position, Vector3d axis) {
        return new EngineState(
                id, "rp1", "lox", dryMassKg, thrustNewtons, ispSeconds, 2.0, 1.0, 0.0, true, position, axis);
    }
}
