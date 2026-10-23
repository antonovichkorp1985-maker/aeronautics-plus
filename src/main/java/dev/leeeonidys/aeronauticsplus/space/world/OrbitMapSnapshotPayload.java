package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapTrack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Server snapshot of vehicles the Kepler map (and world entities) can draw. */
public record OrbitMapSnapshotPayload(List<OrbitMapTrack> tracks) implements CustomPacketPayload {
    public static final Type<OrbitMapSnapshotPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "orbit_map_snapshot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OrbitMapSnapshotPayload> STREAM_CODEC =
            StreamCodec.of(OrbitMapSnapshotPayload::write, OrbitMapSnapshotPayload::read);

    public OrbitMapSnapshotPayload {
        tracks = List.copyOf(tracks == null ? List.of() : tracks);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OrbitMapSnapshotPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> OrbitMapClientHooks.OPEN.accept(payload.tracks()));
    }

    private static void write(RegistryFriendlyByteBuf buf, OrbitMapSnapshotPayload payload) {
        buf.writeVarInt(payload.tracks().size());
        for (OrbitMapTrack track : payload.tracks()) {
            buf.writeUtf(track.id());
            buf.writeDouble(track.altitudeMeters());
            buf.writeDouble(track.angleRadians());
            buf.writeUtf(track.presence().name());
            buf.writeDouble(track.horizontalSpeedMetersPerSecond());
        }
    }

    private static OrbitMapSnapshotPayload read(RegistryFriendlyByteBuf buf) {
        int count = buf.readVarInt();
        if (count < 0 || count > 64) {
            throw new IllegalArgumentException("Map snapshot track count is invalid");
        }
        List<OrbitMapTrack> tracks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            tracks.add(new OrbitMapTrack(
                    buf.readUtf(),
                    buf.readDouble(),
                    buf.readDouble(),
                    FlightPresence.valueOf(buf.readUtf()),
                    buf.readDouble()));
        }
        return new OrbitMapSnapshotPayload(tracks);
    }
}
