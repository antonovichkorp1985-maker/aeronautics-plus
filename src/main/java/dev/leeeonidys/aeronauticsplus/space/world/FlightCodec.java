package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockCompiler;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockGrid;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselBlockOccupant;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitState;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.core.VesselState;
import dev.leeeonidys.aeronauticsplus.space.core.WorldHandoff;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;

/** Parts payload plus Kepler state. Chunk unload must not drop the flight. */
public final class FlightCodec {
    private FlightCodec() {
    }

    public static String encodeFlying(FlightLoop loop) {
        StringBuilder builder = new StringBuilder();
        GridPos pad = loop.padLeft().origin();
        for (VesselBlockOccupant occupant : loop.flying().occupants()) {
            if (!builder.isEmpty()) {
                builder.append(';');
            }
            builder.append(occupant.spec().id())
                    .append(',')
                    .append(occupant.pos().x() - pad.x())
                    .append(',')
                    .append(occupant.pos().y() - pad.y())
                    .append(',')
                    .append(occupant.pos().z() - pad.z())
                    .append(',')
                    .append(occupant.facing().name());
        }
        return builder.toString();
    }

    public static VesselBlockGrid flyingGrid(String payload, GridPos pad) {
        VesselEntity.PartView[] views = VesselEntity.decodeParts(payload);
        List<VesselBlockOccupant> occupants = new ArrayList<>();
        for (VesselEntity.PartView view : views) {
            try {
                occupants.add(new VesselBlockOccupant(
                        pad.translate(view.x(), view.y(), view.z()),
                        VesselPartCatalog.byId(view.specId()),
                        view.facing()));
            } catch (RuntimeException ignored) {
                // skip a corrupt or ChemMod-unknown part
            }
        }
        return new VesselBlockGrid(occupants);
    }

    public static FlightLoop restore(String parts, GridPos pad, OrbitState orbit) {
        if (pad == null || orbit == null) {
            throw new IllegalArgumentException("Pad and orbit are required");
        }
        VesselBlockGrid flying = flyingGrid(parts, pad);
        if (flying.isEmpty()) {
            throw new IllegalStateException("No flying parts to restore");
        }
        VesselBlockOccupant padOccupant = new VesselBlockOccupant(pad, VesselPartCatalog.PAD, BlockFace.UP);
        VesselBlockGrid padLeft = VesselBlockGrid.of(padOccupant);
        List<VesselBlockOccupant> stacked = new ArrayList<>();
        stacked.add(padOccupant);
        stacked.addAll(flying.occupants());
        VesselCompilation compiled = VesselBlockCompiler.analyze(flying);
        VesselState vessel = compiled.toVesselState("flight", orbit);
        FlightPresence presence = WorldHandoff.inWorld(orbit)
                ? FlightPresence.WORLD_ENTITY
                : FlightPresence.ORBIT_MAP;
        return new FlightLoop(presence, new VesselBlockGrid(stacked), padLeft, flying, vessel, null);
    }

    public static void putOrbit(CompoundTag tag, OrbitState orbit) {
        Vector3d position = orbit.positionMeters();
        Vector3d velocity = orbit.velocityMetersPerSecond();
        tag.putDouble("Ox", position.x());
        tag.putDouble("Oy", position.y());
        tag.putDouble("Oz", position.z());
        tag.putDouble("Vx", velocity.x());
        tag.putDouble("Vy", velocity.y());
        tag.putDouble("Vz", velocity.z());
        tag.putDouble("Epoch", orbit.epochSeconds());
    }

    public static OrbitState readOrbit(CompoundTag tag) {
        if (tag == null || !tag.contains("Ox")) {
            return null;
        }
        return new OrbitState(
                SpaceBodies.earth(),
                new Vector3d(tag.getDouble("Ox"), tag.getDouble("Oy"), tag.getDouble("Oz")),
                new Vector3d(tag.getDouble("Vx"), tag.getDouble("Vy"), tag.getDouble("Vz")),
                tag.getDouble("Epoch"));
    }
}
