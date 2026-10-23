package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.CellOccupancy;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;
import dev.leeeonidys.aeronauticsplus.space.core.VesselBlueprint;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.core.VesselComponent;

/**
 * Deterministic proof that several small objects may share one world cell
 * when they fit and do not overlap. The Minecraft cell is storage, not the object.
 */
public final class PackingSlice {
    private PackingSlice() {
    }

    public record Result(int packedEngines, double westX, double eastX, String overlap, String overfill) {
    }

    public static Result execute() {
        CellOccupancy west = new CellOccupancy(new Vector3d(0.0, 0.0, 0.0), new Vector3d(0.5, 1.0, 1.0));
        CellOccupancy east = new CellOccupancy(new Vector3d(0.5, 0.0, 0.0), new Vector3d(0.5, 1.0, 1.0));
        if (west.intersects(east)) {
            throw new IllegalStateException("Halves that only touch must not count as overlap");
        }

        VesselPartSpec westEngine = VesselPartSpec.engine(
                "engine-west", 90.0, 10_000.0, 300.0, "rp1", "lox", 2.0, west);
        VesselPartSpec eastEngine = VesselPartSpec.engine(
                "engine-east", 90.0, 10_000.0, 300.0, "rp1", "lox", 2.0, east);
        VesselBlockGrid packed = VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), westEngine, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 0, 0), eastEngine, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP));
        VesselCompilation compilation = VesselBlockCompiler.analyze(packed);
        if (!compilation.isLaunchable()) {
            throw new IllegalStateException("Non-overlapping packed engines must launch:\\n" + compilation.diagnosticText());
        }
        if (compilation.stages().size() != 1 || compilation.stages().get(0).engines().size() != 2) {
            throw new IllegalStateException("Packed cell must compile as one stage with two engines");
        }
        VesselComponent westPart = component(VesselBlockCompiler.compile(packed), "engine-west@0,0,0");
        VesselComponent eastPart = component(VesselBlockCompiler.compile(packed), "engine-east@0,0,0");
        if (Math.abs(westPart.localPositionMeters().x() - 0.25) > 1.0e-9
                || Math.abs(eastPart.localPositionMeters().x() - 0.75) > 1.0e-9) {
            throw new IllegalStateException("Packed engines must keep their own centroids, got "
                    + westPart.localPositionMeters() + " and " + eastPart.localPositionMeters());
        }

        VesselCompilation overlap = VesselBlockCompiler.analyze(VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), westEngine, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 0, 0),
                        VesselPartSpec.engine("engine-overlap", 90.0, 10_000.0, 300.0, "rp1", "lox", 2.0, west),
                        BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP)));
        if (overlap.isLaunchable() || !overlap.diagnosticText().contains("OVERLAP")) {
            throw new IllegalStateException("Overlapping occupancies must be OVERLAP:\\n" + overlap.diagnosticText());
        }

        VesselCompilation overfill = VesselBlockCompiler.analyze(VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 0, 0),
                        VesselPartSpec.engine("engine-full", 90.0, 10_000.0, 300.0, "rp1", "lox", 2.0,
                                CellOccupancy.FULL),
                        BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP)));
        if (overfill.isLaunchable() || !overfill.diagnosticText().contains("OVERFILL")) {
            throw new IllegalStateException("Two bulky parts in one cell must be OVERFILL:\\n" + overfill.diagnosticText());
        }

        return new Result(
                compilation.stages().get(0).engines().size(),
                westPart.localPositionMeters().x(),
                eastPart.localPositionMeters().x(),
                overlap.diagnosticText(),
                overfill.diagnosticText());
    }

    private static VesselComponent component(VesselBlueprint blueprint, String id) {
        return blueprint.components().stream()
                .filter(component -> component.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing component " + id));
    }
}
