package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDynamics;
import dev.leeeonidys.aeronauticsplus.space.core.VesselState;

/**
 * Tank sizes, a second propellant family, antennas and a station lab.
 * Chemistry ids stay placeholders; ChemMod remains the chemistry source.
 */
public final class VarietySlice {
    private VarietySlice() {
    }

    public record Result(
            boolean smallLaunchable,
            boolean largeLaunchable,
            boolean hydroloxLaunchable,
            double hydroloxDroppedKg,
            String wrongFuel,
            String noAntenna,
            String labAlone) {
    }

    public static Result execute() {
        VesselCompilation small = VesselBlockCompiler.compile(smallKerolox()).analyze();
        if (!small.isLaunchable()) {
            throw new IllegalStateException("Small RP-1/LOX pair must launch:\n" + small.diagnosticText());
        }
        VesselCompilation large = VesselBlockCompiler.compile(largeKerolox()).analyze();
        if (!large.isLaunchable()) {
            throw new IllegalStateException("Large RP-1/LOX pair must launch:\n" + large.diagnosticText());
        }
        if (!(large.toVesselState("large", SpaceBodies.parkingOrbit()).totalMassKg()
                > small.toVesselState("small", SpaceBodies.parkingOrbit()).totalMassKg() + 500.0)) {
            throw new IllegalStateException("Large barrels must outweigh the small pair");
        }

        VesselCompilation hydrolox = VesselBlockCompiler.compile(hydroloxStack()).analyze();
        if (!hydrolox.isLaunchable()) {
            throw new IllegalStateException("LH2 + LOX + RL10-class engine must launch:\n"
                    + hydrolox.diagnosticText());
        }
        VesselState before = hydrolox.toVesselState("hydrolox", SpaceBodies.parkingOrbit());
        VesselState after = VesselDynamics.burnActiveStage(before, 3.0);
        double dropped = before.totalMassKg() - after.totalMassKg();
        if (!(dropped > 0.2)) {
            throw new IllegalStateException("Hydrolox burn must consume LH2 and LOX, dropped=" + dropped);
        }

        String wrongFuel = VesselBlockCompiler.compile(hydroloxOnRp1()).analyze().diagnosticText();
        if (!wrongFuel.contains("NO_FUEL_PATH") || wrongFuel.contains("NO_OXIDIZER_PATH")) {
            throw new IllegalStateException("RL10-class engine does not drink RP-1:\n" + wrongFuel);
        }

        String noAntenna = VesselBlockCompiler.analyze(habitatWithoutAntenna()).diagnosticText();
        if (!noAntenna.contains("NO_ANTENNA")) {
            throw new IllegalStateException("Habitat without a radio must warn NO_ANTENNA:\n" + noAntenna);
        }
        String labAlone = VesselBlockCompiler.analyze(labWithoutHabitat()).diagnosticText();
        if (!labAlone.contains("LAB_WITHOUT_HABITAT")) {
            throw new IllegalStateException("Lab without a habitat must warn:\n" + labAlone);
        }

        VesselCompilation bus = VesselBlockCompiler.analyze(SpacecraftSlice.spacecraft());
        if (bus.diagnosticText().contains("NO_ANTENNA") || !bus.stages().get(0).hasAntenna()) {
            throw new IllegalStateException("Complete bus needs an antenna:\n" + bus.diagnosticText());
        }

        return new Result(
                small.isLaunchable(),
                large.isLaunchable(),
                hydrolox.isLaunchable(),
                dropped,
                wrongFuel,
                noAntenna,
                labAlone);
    }

    public static VesselBlockGrid smallKerolox() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(-1, 0, 0), VesselPartCatalog.FUEL_TANK_SMALL, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 0, 0), VesselPartCatalog.OXIDIZER_TANK_SMALL, BlockFace.UP));
    }

    public static VesselBlockGrid largeKerolox() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(-1, 0, 0), VesselPartCatalog.FUEL_TANK_LARGE, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 0, 0), VesselPartCatalog.OXIDIZER_TANK_LARGE, BlockFace.UP));
    }

    public static VesselBlockGrid hydroloxStack() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.HYDROLOX_ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(-1, 0, 0), VesselPartCatalog.LH2_TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 0, 0), VesselPartCatalog.OXIDIZER_TANK, BlockFace.UP));
    }

    private static VesselBlockGrid hydroloxOnRp1() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.HYDROLOX_ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(-1, 0, 0), VesselPartCatalog.FUEL_TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(1, 0, 0), VesselPartCatalog.OXIDIZER_TANK, BlockFace.UP));
    }

    private static VesselBlockGrid habitatWithoutAntenna() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.HABITAT, BlockFace.UP));
    }

    private static VesselBlockGrid labWithoutHabitat() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.LAB, BlockFace.UP));
    }
}
