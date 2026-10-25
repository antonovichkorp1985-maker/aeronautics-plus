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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Discrete control requests sent by the selected-vessel flight panel. */
public record OrbitMapControlPayload(String trackId, int action) implements CustomPacketPayload {
    public static final int THROTTLE_DOWN = 0;
    public static final int THROTTLE_UP = 1;
    public static final int THROTTLE_CUT = 2;
    public static final int THROTTLE_FULL = 3;
    public static final int AIM_PROGRADE = 4;
    public static final int AIM_RETROGRADE = 5;
    public static final int AIM_RADIAL = 6;
    public static final int RCS_PULSE = 7;

    public static final Type<OrbitMapControlPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "orbit_map_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OrbitMapControlPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        buf.writeUtf(value.trackId());
                        buf.writeVarInt(value.action());
                    },
                    buf -> new OrbitMapControlPayload(buf.readUtf(256), buf.readVarInt()));

    public OrbitMapControlPayload {
        if (trackId == null || trackId.isBlank() || trackId.length() > 256
                || action < THROTTLE_DOWN || action > RCS_PULSE) {
            throw new IllegalArgumentException("Orbit-map control request is invalid");
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OrbitMapControlPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)
                    || !(serverPlayer.level() instanceof ServerLevel level)
                    || level.dimension() != Level.OVERWORLD) {
                return;
            }
            String result = OrbitMapData.get(level).command(payload.trackId(), payload.action());
            serverPlayer.displayClientMessage(Component.literal(result), true);
        });
    }
}
