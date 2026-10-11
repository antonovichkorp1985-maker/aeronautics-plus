package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Ground crane command. Not seated, not a GUI, not an AP crane block. */
public record CraneCommandPayload() implements CustomPacketPayload {
    public static final Type<CraneCommandPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "crane_command"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CraneCommandPayload> STREAM_CODEC =
            StreamCodec.unit(new CraneCommandPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CraneCommandPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer) || !(serverPlayer.level() instanceof ServerLevel level)) {
                return;
            }
            String message = CraneLiftData.get(level).command(level, serverPlayer);
            if (!message.isBlank()) {
                serverPlayer.displayClientMessage(Component.literal(message), false);
            }
        });
    }
}
