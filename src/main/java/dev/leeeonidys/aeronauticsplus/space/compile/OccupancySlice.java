package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.CellOccupancy;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;
import dev.leeeonidys.aeronauticsplus.space.core.VesselBlueprint;
import dev.leeeonidys.aeronauticsplus.space.core.VesselComponent;

/**
 * Deterministic proof that a rocket part is not bound to a full 1 m cube.
 * Occupancy follows the Chisels & Bits rule: several small mechanisms may share a cell.
 */
public final class OccupancySlice {
    private OccupancySlice() {
    }

    public record Result(double nozzleVolume, double halfVolume, double halfEngineX, double tankX) {
    }

    public static Result execute() {
        if (CellOccupancy.NOZZLE.isFullBlock() || CellOccupancy.COLUMN.isFullBlock()
                || CellOccupancy.RING.isFullBlock() || CellOccupancy.CRADLE.isFullBlock()) {
            throw new IllegalStateException("Catalog rocket parts must not occupy a full cube");
        }
        if (!(VesselPartCatalog.ENGINE.occupancy().volume() < 1.0)) {
            throw new IllegalStateException("Engine occupancy must be smaller than one cubic metre");
        }

        CellOccupancy half = new CellOccupancy(new Vector3d(0.5, 0.0, 0.0), new Vector3d(0.5, 1.0, 1.0));
        if (Math.abs(half.volume() - 0.5) > 1.0e-9 || Math.abs(half.centroid().x() - 0.75) > 1.0e-9) {
            throw new IllegalStateException("Half-cell occupancy centroid must sit at x=0.75");
        }

        VesselPartSpec halfEngine = VesselPartSpec.engine(
                "half-engine", 90.0, 20_000.0, 300.0, "rp1", "lox", 2.0, half);
        VesselBlueprint blueprint = VesselBlockCompiler.compile(VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), halfEngine, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP)));
        VesselComponent engine = component(blueprint, "half-engine@0,0,0");
        VesselComponent tank = component(blueprint, "rocket_tank@0,1,0");
        if (Math.abs(engine.localPositionMeters().x() - 0.75) > 1.0e-9) {
            throw new IllegalStateException(
                    "Sub-cell engine mass must sit at the occupancy centroid, got "
                            + engine.localPositionMeters());
        }
        if (Math.abs(tank.localPositionMeters().x() - 0.5) > 1.0e-9) {
            throw new IllegalStateException("Symmetric tank occupancy must keep x=0.5");
        }
        return new Result(
                VesselPartCatalog.ENGINE.occupancy().volume(),
                half.volume(),
                engine.localPositionMeters().x(),
                tank.localPositionMeters().x());
    }

    private static VesselComponent component(VesselBlueprint blueprint, String id) {
        return blueprint.components().stream()
                .filter(component -> component.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing component " + id));
    }
}
