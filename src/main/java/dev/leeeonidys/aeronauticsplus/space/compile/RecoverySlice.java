package dev.leeeonidys.aeronauticsplus.space.compile;

/**
 * Pyro ring cuts the pile like a KSP decoupler; the booster layout sits back on a pad.
 * World write is {@link dev.leeeonidys.aeronauticsplus.space.world.WorldStageRecovery}.
 */
public final class RecoverySlice {
    private RecoverySlice() {
    }

    public record Result(
            boolean pyroCut,
            boolean boosterHasEngine,
            boolean upperHasEngine,
            boolean padStayedOnGround,
            boolean planOnPad,
            int recoveredParts) {
    }

    public static Result execute() {
        try {
            VesselBlockCompiler.firePyro(singleStage());
            throw new IllegalStateException("A stack without a pyro ring must not separate");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().contains("pyro")) {
                throw expected;
            }
        }

        GridSplit split = VesselBlockCompiler.firePyro(twoStageOnPad());
        if (hasKind(split.booster(), VesselPartKind.PAD) || hasKind(split.continuing(), VesselPartKind.PAD)) {
            throw new IllegalStateException("Pad must stay on the ground after pyro");
        }
        if (!hasKind(split.booster(), VesselPartKind.ENGINE) || !hasKind(split.booster(), VesselPartKind.SEPARATOR)) {
            throw new IllegalStateException("Booster must keep the engine and the spent ring");
        }
        if (!hasKind(split.continuing(), VesselPartKind.ENGINE) || hasKind(split.continuing(), VesselPartKind.SEPARATOR)) {
            throw new IllegalStateException("Upper must fly without the pyro ring");
        }
        if (hasSpec(split.booster(), VesselPartCatalog.ENGINE.id())
                && countKind(split.booster(), VesselPartKind.ENGINE) != 1) {
            throw new IllegalStateException("Booster must not keep the upper engine");
        }
        if (countKind(split.continuing(), VesselPartKind.ENGINE) != 1) {
            throw new IllegalStateException("Upper must keep exactly one engine");
        }

        RecoveryPlan plan = RecoveryPlan.sitOnPad(split.booster());
        if (plan.includesKind(VesselPartKind.PAD)) {
            throw new IllegalStateException("Recovery plan must not place a second pad");
        }
        if (!plan.includes(VesselPartCatalog.ENGINE.id()) || !plan.includes(VesselPartCatalog.SEPARATOR.id())) {
            throw new IllegalStateException("Recovered booster must include engine and pyro ring");
        }
        if (plan.includes("rocket_engine") && plan.placements().stream()
                .anyMatch(placement -> placement.spec().id().equals("rocket_engine")
                        && placement.relativeToPad().y() < 1)) {
            throw new IllegalStateException("Recovered engine must sit on the pad, not inside it");
        }
        boolean engineOnPad = plan.placements().stream()
                .anyMatch(placement -> placement.spec().kind() == VesselPartKind.ENGINE
                        && placement.relativeToPad().y() >= 1);
        if (!engineOnPad) {
            throw new IllegalStateException("Booster engine must come back above the pad");
        }
        return new Result(
                true,
                hasKind(split.booster(), VesselPartKind.ENGINE),
                hasKind(split.continuing(), VesselPartKind.ENGINE),
                !hasKind(split.booster(), VesselPartKind.PAD),
                engineOnPad,
                plan.placements().size());
    }

    private static VesselBlockGrid twoStageOnPad() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.PAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.SEPARATOR, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 4, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 5, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 6, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }

    private static VesselBlockGrid singleStage() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.CONTROLLER, BlockFace.UP));
    }

    private static boolean hasKind(VesselBlockGrid grid, VesselPartKind kind) {
        return countKind(grid, kind) > 0;
    }

    private static int countKind(VesselBlockGrid grid, VesselPartKind kind) {
        int total = 0;
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().kind() == kind) {
                total++;
            }
        }
        return total;
    }

    private static boolean hasSpec(VesselBlockGrid grid, String specId) {
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().id().equals(specId)) {
                return true;
            }
        }
        return false;
    }
}
