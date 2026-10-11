package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Seated crew command. Not the orientation GUI. 0 = fairing, 1 = cut, 2 = full, 3 = stage. */
public record CrewCommandPayload(int command) implements CustomPacketPayload {
    public static final int DROP_FAIRING = 0;
    public static final int THROTTLE_CUT = 1;
    public static final int THROTTLE_FULL = 2;
    public static final int STAGE = 3;

    public static final Type<CrewCommandPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "crew_command"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CrewCommandPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> buf.writeVarInt(value.command),
                    buf -> new CrewCommandPayload(buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CrewCommandPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            if (!(serverPlayer.getVehicle() instanceof VesselEntity vessel)) {
                serverPlayer.displayClientMessage(
                        Component.literal("Команда экипажа — только в полёте, сидя в кресле."), false);
                return;
            }
            if (payload.command == DROP_FAIRING) {
                String fail = vessel.commandDropFairing();
                serverPlayer.displayClientMessage(
                        Component.literal(fail.isBlank() ? "Обтекатель сброшен." : fail), false);
            } else if (payload.command == THROTTLE_CUT || payload.command == THROTTLE_FULL) {
                double throttle = payload.command == THROTTLE_FULL ? 1.0 : 0.0;
                serverPlayer.displayClientMessage(
                        Component.literal(vessel.commandThrottle(throttle)), false);
            } else if (payload.command == STAGE) {
                serverPlayer.displayClientMessage(Component.literal(vessel.commandStage()), false);
            }
        });
    }
}
