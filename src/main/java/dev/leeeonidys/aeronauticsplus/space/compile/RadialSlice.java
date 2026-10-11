package dev.leeeonidys.aeronauticsplus.space.compile;

/**
 * Soyuz / Falcon Heavy sides: the same pyro ring facing out drops strap-ons.
 * Axial firePyro is unchanged. Not ChemMod air.
 */
public final class RadialSlice {
    private RadialSlice() {
    }

    public record Result(
            int sideCount,
            boolean coreKeepsController,
            boolean sidesHaveEngines,
            boolean axialStillCuts,
            boolean axialNotRadial,
            boolean compilerMarksStrapOns) {
    }

    public static Result execute() {
        try {
            VesselBlockCompiler.fireRadial(VesselCompileSlice.twoStageStack());
            throw new IllegalStateException("An axial stack must not fire as radial");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().contains("radial")) {
                throw expected;
            }
        }

        RadialSplit split = VesselBlockCompiler.fireRadial(heavy());
        if (split.sides().size() != 2) {
            throw new IllegalStateException("Heavy must drop two side boosters, sides=" + split.sides().size());
        }
        if (!hasKind(split.core(), VesselPartKind.CONTROLLER) || !hasKind(split.core(), VesselPartKind.SEPARATOR)) {
            throw new IllegalStateException("Core must keep the seat and the radial rings");
        }
        if (hasKind(split.core(), VesselPartKind.PAD)) {
            throw new IllegalStateException("Pad must stay on the ground");
        }
        for (VesselBlockGrid side : split.sides()) {
            if (!hasKind(side, VesselPartKind.ENGINE) || hasKind(side, VesselPartKind.CONTROLLER)
                    || hasKind(side, VesselPartKind.SEPARATOR)) {
                throw new IllegalStateException("A side booster is engine+tank, not the core");
            }
        }

        GridSplit axial = VesselBlockCompiler.firePyro(VesselCompileSlice.twoStageStack());
        if (axial.booster().occupants().isEmpty() || axial.continuing().occupants().isEmpty()) {
            throw new IllegalStateException("Axial pyro must still cut a stacked booster");
        }

        VesselCompilation compiled = VesselBlockCompiler.analyze(heavy());
        long straps = compiled.stages().stream().filter(StageState::strapOn).count();
        if (straps != 2) {
            throw new IllegalStateException("Compiler must mark two strap-ons, straps=" + straps);
        }
        if (compiled.stages().stream().filter(stage -> !stage.strapOn()).findAny().isEmpty()) {
            throw new IllegalStateException("Core must stay a non-strap stage");
        }

        return new Result(
                split.sides().size(),
                hasKind(split.core(), VesselPartKind.CONTROLLER),
                split.sides().stream().allMatch(side -> hasKind(side, VesselPartKind.ENGINE)),
                !axial.booster().isEmpty(),
                true,
                straps == 2);
    }

    private static VesselBlockGrid heavy() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(-1, 2, 0), VesselPartCatalog.SEPARATOR, BlockFace.WEST),
                new VesselBlockOccupant(new GridPos(-2, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(-2, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 2, 0), VesselPartCatalog.SEPARATOR, BlockFace.EAST),
                new VesselBlockOccupant(new GridPos(2, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(2, 2, 0), VesselPartCatalog.TANK, BlockFace.UP));
    }

    private static boolean hasKind(VesselBlockGrid grid, VesselPartKind kind) {
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().kind() == kind) {
                return true;
            }
        }
        return false;
    }
}
