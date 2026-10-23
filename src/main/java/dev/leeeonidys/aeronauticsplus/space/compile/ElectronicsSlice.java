package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;

/**
 * Cabin oxygen, radio transponder and telescope. Not rocket SKUs: apparatus on the bus.
 * Feedstock still belongs to ChemMod.
 */
public final class ElectronicsSlice {
    private ElectronicsSlice() {
    }

    public record Result(
            boolean complete,
            boolean oxygen,
            boolean transponder,
            String noOxygen,
            String antennaAlone,
            String telescopeBlind) {
    }

    public static Result execute() {
        VesselCompilation bus = VesselBlockCompiler.analyze(SpacecraftSlice.spacecraft());
        if (!bus.isLaunchable()) {
            throw new IllegalStateException("Bus with ECLSS/radio must launch:\n" + bus.diagnosticText());
        }
        String text = bus.diagnosticText();
        if (text.contains("NO_OXYGEN")
                || text.contains("ANTENNA_WITHOUT_TRANSPONDER")
                || text.contains("TRANSPONDER_WITHOUT_ANTENNA")) {
            throw new IllegalStateException("Complete bus must carry O2 and a radio:\n" + text);
        }
        if (!bus.stages().get(0).hasOxygen() || !bus.stages().get(0).hasTransponder()) {
            throw new IllegalStateException("Bus must compile cabin oxygen and a transponder");
        }

        String noOxygen = VesselBlockCompiler.analyze(habitatWithoutOxygen()).diagnosticText();
        if (!noOxygen.contains("NO_OXYGEN")) {
            throw new IllegalStateException("Habitat without cabin O2 must warn NO_OXYGEN:\n" + noOxygen);
        }
        String antennaAlone = VesselBlockCompiler.analyze(antennaWithoutRadio()).diagnosticText();
        if (!antennaAlone.contains("ANTENNA_WITHOUT_TRANSPONDER")) {
            throw new IllegalStateException("Antenna without a transponder must warn:\n" + antennaAlone);
        }
        String telescopeBlind = VesselBlockCompiler.analyze(telescopeAlone()).diagnosticText();
        if (!telescopeBlind.contains("TELESCOPE_WITHOUT_GYRO")
                || !telescopeBlind.contains("TELESCOPE_WITHOUT_ANTENNA")) {
            throw new IllegalStateException("Bare telescope must need gyro and antenna:\n" + telescopeBlind);
        }

        return new Result(
                bus.isLaunchable(),
                bus.stages().get(0).hasOxygen(),
                bus.stages().get(0).hasTransponder(),
                noOxygen,
                antennaAlone,
                telescopeBlind);
    }

    private static VesselBlockGrid habitatWithoutOxygen() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.HABITAT, BlockFace.UP));
    }

    private static VesselBlockGrid antennaWithoutRadio() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.OMNI_ANTENNA, BlockFace.UP));
    }

    private static VesselBlockGrid telescopeAlone() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.TELESCOPE, BlockFace.UP));
    }
}
