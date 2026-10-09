package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.core.VesselComponent;
import dev.leeeonidys.aeronauticsplus.space.core.VesselConnection;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDynamics;
import dev.leeeonidys.aeronauticsplus.space.core.VesselState;
import java.util.List;

/**
 * Deterministic proof that a physical block grid preserves ports, stages and mass
 * positions when compiled to {@link dev.leeeonidys.aeronauticsplus.space.core.VesselBlueprint}.
 */
public final class VesselCompileSlice {
    private VesselCompileSlice() {
    }

    public record Result(
            VesselCompilation compilation,
            int componentCount,
            int structuralLinks,
            int fuelLinks,
            int separationLinks,
            double boosterEngineY,
            double upperTankY) {
    }

    public static Result execute() {
        VesselCompilation compilation = VesselBlockCompiler.compile(twoStageStack()).analyze();
        if (!compilation.isLaunchable()) {
            throw new IllegalStateException("Physical two-stage stack must be launchable:\n" + compilation.diagnosticText());
        }
        if (compilation.stages().size() != 2) {
            throw new IllegalStateException("Separator must split the stack into two stages");
        }
        if (!"stage-0".equals(compilation.stages().get(0).id())
                || !"stage-1".equals(compilation.stages().get(1).id())) {
            throw new IllegalStateException("Stages must be numbered bottom to top");
        }

        var blueprint = VesselBlockCompiler.compile(twoStageStack());
        VesselComponent boosterEngine = component(blueprint.components(), "rocket_engine@0,0,0");
        VesselComponent upperTank = component(blueprint.components(), "rocket_tank@0,4,0");
        if (Math.abs(boosterEngine.localPositionMeters().y() - 0.5) > 1.0e-9) {
            throw new IllegalStateException("Booster engine mass must sit at block centre y=0.5");
        }
        if (Math.abs(upperTank.localPositionMeters().y() - 4.5) > 1.0e-9) {
            throw new IllegalStateException("Upper tank mass must sit at block centre y=4.5");
        }
        if (!boosterEngine.stageId().equals("stage-0") || !upperTank.stageId().equals("stage-1")) {
            throw new IllegalStateException("Compiled stages do not follow the physical stack");
        }

        int structural = count(blueprint.connections(), VesselConnection.ConnectionKind.STRUCTURAL);
        int fuel = count(blueprint.connections(), VesselConnection.ConnectionKind.FUEL);
        int oxidizer = count(blueprint.connections(), VesselConnection.ConnectionKind.OXIDIZER);
        int separation = count(blueprint.connections(), VesselConnection.ConnectionKind.SEPARATION);
        if (structural < 3 || fuel < 2 || oxidizer < 2 || separation != 1) {
            throw new IllegalStateException(
                    "Ports were dropped: structural=" + structural + " fuel=" + fuel
                            + " oxidizer=" + oxidizer + " separation=" + separation);
        }

        VesselState burned = VesselDynamics.burnActiveStage(
                compilation.toVesselState("compiled-stack", SpaceBodies.parkingOrbit()), 3.0);
        if (!(burned.totalMassKg() < compilation.toVesselState("compiled-stack", SpaceBodies.parkingOrbit()).totalMassKg() - 0.5)) {
            throw new IllegalStateException("Compiled vessel must still consume propellant");
        }

        String broken = VesselBlockCompiler.compile(disconnectedParts()).analyze().diagnosticText();
        if (!broken.contains("NO_FUEL_PATH") || !broken.contains("NO_OXIDIZER_PATH")) {
            throw new IllegalStateException(
                    "A tank not adjacent to its engine must keep fuel-path diagnostics:\n" + broken);
        }

        String offset = VesselBlockCompiler.compile(offsetEngine()).analyze().diagnosticText();
        if (!offset.contains("THRUST_OFFSET")) {
            throw new IllegalStateException("Offset physical engine must warn about thrust offset:\n" + offset);
        }

        String cancelled = VesselBlockCompiler.compile(cancelledThrust()).analyze().diagnosticText();
        if (!cancelled.contains("ZERO_NET_THRUST")) {
            throw new IllegalStateException("Opposing physical engines must report zero net thrust:\n" + cancelled);
        }

        return new Result(
                compilation,
                blueprint.components().size(),
                structural,
                fuel,
                separation,
                boosterEngine.localPositionMeters().y(),
                upperTank.localPositionMeters().y());
    }

    public static VesselBlockGrid twoStageStack() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.SEPARATOR, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.TANK, BlockFace.UP));
    }

    private static VesselBlockGrid disconnectedParts() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 0, 0), VesselPartCatalog.STRUCTURE, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(2, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN));
    }

    private static VesselBlockGrid offsetEngine() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, -1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 0, 0), VesselPartCatalog.STRUCTURE, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(2, 0, 0), VesselPartCatalog.STRUCTURE, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(3, 0, 0), VesselPartCatalog.STRUCTURE, BlockFace.UP));
    }

    private static VesselBlockGrid cancelledThrust() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.ENGINE, BlockFace.UP));
    }

    private static VesselComponent component(List<VesselComponent> components, String id) {
        return components.stream()
                .filter(component -> component.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing compiled component " + id));
    }

    private static int count(List<VesselConnection> connections, VesselConnection.ConnectionKind kind) {
        int total = 0;
        for (VesselConnection connection : connections) {
            if (connection.kind() == kind) {
                total++;
            }
        }
        return total;
    }
}
