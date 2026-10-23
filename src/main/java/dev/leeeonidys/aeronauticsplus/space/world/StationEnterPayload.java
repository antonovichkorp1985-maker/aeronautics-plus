package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client pressed enter-station. Not the orientation GUI. */
public record StationEnterPayload() implements CustomPacketPayload {
    public static final Type<StationEnterPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "station_enter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StationEnterPayload> STREAM_CODEC =
            StreamCodec.of((buf, value) -> {
            }, buf -> new StationEnterPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StationEnterPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            StationEnter.tell(serverPlayer, StationEnter.toggle(serverPlayer));
        });
    }
}
