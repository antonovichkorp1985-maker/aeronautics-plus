package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client asks the server for the current Kepler-map snapshot. */
public record OrbitMapRequestPayload() implements CustomPacketPayload {
    public static final Type<OrbitMapRequestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "orbit_map_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OrbitMapRequestPayload> STREAM_CODEC =
            StreamCodec.of((buf, value) -> {
            }, buf -> new OrbitMapRequestPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OrbitMapRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            if (!(serverPlayer.level() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
                PacketDistributor.sendToPlayer(serverPlayer, new OrbitMapSnapshotPayload(java.util.List.of()));
                return;
            }
            PacketDistributor.sendToPlayer(
                    serverPlayer, new OrbitMapSnapshotPayload(OrbitMapData.get(level).tracks(level)));
        });
    }
}
