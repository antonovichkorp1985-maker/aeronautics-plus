package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.BurnAim;
import dev.leeeonidys.aeronauticsplus.space.core.MissionEvent;
import dev.leeeonidys.aeronauticsplus.space.core.MissionExecutor;
import dev.leeeonidys.aeronauticsplus.space.core.MissionPlan;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDynamics;
import dev.leeeonidys.aeronauticsplus.space.core.VesselState;
import java.util.List;

/**
 * Deterministic proof that an orbital stack carries an apparatus under a fairing
 * and that fairing jettison drops only that mass. Sequence follows Soyuz / Falcon 9 /
 * Saturn V: leave dense atmosphere, drop the shroud, keep the payload.
 */
public final class PayloadSlice {
    private PayloadSlice() {
    }

    public record Result(
            double fairingMassKg,
            double payloadMassKg,
            double massDroppedKg,
            boolean launchable,
            String noPayload,
            String unshrouded) {
    }

    public static Result execute() {
        VesselCompilation orbital = VesselBlockCompiler.analyze(orbitalStack());
        if (!orbital.isLaunchable()) {
            throw new IllegalStateException("Orbital stack with payload must be launchable:\n"
                    + orbital.diagnosticText());
        }
        if (orbital.diagnosticText().contains("NO_PAYLOAD")
                || orbital.diagnosticText().contains("PAYLOAD_WITHOUT_FAIRING")
                || orbital.diagnosticText().contains("FAIRING_WITHOUT_PAYLOAD")) {
            throw new IllegalStateException("Complete stack must not warn about payload:\n"
                    + orbital.diagnosticText());
        }
        double fairingMass = orbital.stages().get(0).fairingMassKg();
        double payloadMass = orbital.stages().get(0).payloadMassKg();
        if (Math.abs(fairingMass - VesselPartCatalog.FAIRING.massKg()) > 1.0e-9
                || Math.abs(payloadMass - VesselPartCatalog.PAYLOAD.massKg()) > 1.0e-9) {
            throw new IllegalStateException("Fairing/payload mass must compile from the catalog parts");
        }

        VesselState initial = orbital.toVesselState("payload-demo", SpaceBodies.parkingOrbit());
        double massBefore = initial.totalMassKg();
        VesselState jettisoned = VesselDynamics.jettisonFairing(initial);
        double dropped = massBefore - jettisoned.totalMassKg();
        if (Math.abs(dropped - fairingMass) > 1.0e-9) {
            throw new IllegalStateException("Jettison must drop only fairing mass, dropped=" + dropped);
        }
        if (Math.abs(jettisoned.activeStage().payloadMassKg() - payloadMass) > 1.0e-9) {
            throw new IllegalStateException("Payload must remain after fairing jettison");
        }
        if (jettisoned.activeStage().hasFairing()) {
            throw new IllegalStateException("Fairing must be gone after jettison");
        }
        try {
            VesselDynamics.jettisonFairing(jettisoned);
            throw new IllegalStateException("Second jettison must fail");
        } catch (IllegalStateException exception) {
            if (exception.getMessage() == null || !exception.getMessage().contains("fairing")) {
                throw exception;
            }
        }

        MissionPlan plan = new MissionPlan("shroud-drop", List.of(
                MissionEvent.burn("leave-atmosphere", 2.0, BurnAim.ENGINE),
                MissionEvent.jettisonFairing("drop-shroud"),
                MissionEvent.coast("coast-ten", 10.0)));
        VesselState flown = MissionExecutor.execute(initial, plan);
        if (flown.activeStage().hasFairing() || !(flown.totalMassKg() < massBefore - 1.0)) {
            throw new IllegalStateException("Mission jettison must drop the shroud and burn mass");
        }
        if (!plan.describe().contains("JETTISON_FAIRING")) {
            throw new IllegalStateException("Mission map must name the fairing drop");
        }

        String noPayload = VesselBlockCompiler.analyze(VesselCompileSlice.twoStageStack()).diagnosticText();
        if (!noPayload.contains("NO_PAYLOAD")) {
            throw new IllegalStateException("Booster-only stack must warn NO_PAYLOAD:\n" + noPayload);
        }
        String unshrouded = VesselBlockCompiler.analyze(payloadWithoutFairing()).diagnosticText();
        if (!unshrouded.contains("PAYLOAD_WITHOUT_FAIRING") || unshrouded.contains("NO_PAYLOAD")) {
            throw new IllegalStateException("Bare payload must warn PAYLOAD_WITHOUT_FAIRING:\n" + unshrouded);
        }
        String emptyShroud = VesselBlockCompiler.analyze(fairingWithoutPayload()).diagnosticText();
        if (!emptyShroud.contains("FAIRING_WITHOUT_PAYLOAD")) {
            throw new IllegalStateException("Empty shroud must warn FAIRING_WITHOUT_PAYLOAD:\n" + emptyShroud);
        }

        return new Result(fairingMass, payloadMass, dropped, orbital.isLaunchable(), noPayload, unshrouded);
    }

    public static VesselBlockGrid orbitalStack() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.PAYLOAD, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 3, 0), VesselPartCatalog.FAIRING, BlockFace.UP));
    }

    private static VesselBlockGrid payloadWithoutFairing() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.PAYLOAD, BlockFace.UP));
    }

    private static VesselBlockGrid fairingWithoutPayload() {
        return VesselBlockGrid.of(
                new VesselBlockOccupant(new GridPos(0, 0, 0), VesselPartCatalog.ENGINE, BlockFace.DOWN),
                new VesselBlockOccupant(new GridPos(0, 1, 0), VesselPartCatalog.TANK, BlockFace.UP),
                new VesselBlockOccupant(new GridPos(0, 2, 0), VesselPartCatalog.FAIRING, BlockFace.UP));
    }
}
