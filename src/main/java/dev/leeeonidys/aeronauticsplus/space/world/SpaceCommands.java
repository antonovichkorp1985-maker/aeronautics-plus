package dev.leeeonidys.aeronauticsplus.space.world;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.core.CelestialBody;
import dev.leeeonidys.aeronauticsplus.space.core.CreativeOrbit;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.SolarSystem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Creative cheats. {@code /ap station} is the orbit pocket.
 * {@code /ap world moon} is the walkable Moon. {@code /ap state} is Kepler parking.
 */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class SpaceCommands {
    private SpaceCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("ap")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("station").executes(context -> station(context.getSource())))
                .then(Commands.literal("bodies").executes(context -> bodies(context.getSource())))
                .then(Commands.literal("clock").executes(context -> clock(context.getSource())))
                .then(Commands.literal("world")
                        .then(Commands.argument("body", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                        BodyWorlds.ids(), builder))
                                .executes(context -> world(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "body")))))
                .then(Commands.literal("state")
                        .then(Commands.argument("body", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                        SolarSystem.ids(), builder))
                                .executes(context -> state(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "body"))))));
    }

    private static int station(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Нужен игрок."));
            return 0;
        }
        String message = StationEnter.toggle(player);
        source.sendSuccess(() -> Component.literal(message), false);
        return 1;
    }

    private static int bodies(CommandSourceStack source) {
        source.sendSuccess(
                () -> Component.literal("Тела: " + String.join(", ", SolarSystem.ids())
                        + ". Миры: " + String.join(", ", BodyWorlds.ids())),
                false);
        return SolarSystem.ids().size();
    }

    /**
     * Lunar clock. The Moon runs a synodic day ({@link dev.leeeonidys.aeronauticsplus.space.core.LunarTime}),
     * so {@code /time query daytime} answers in vanilla ticks and {@code /time set} does not stick
     * there. This prints the real answer for wherever the caller stands.
     */
    private static int clock(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Нужен игрок."));
            return 0;
        }
        long dayTime = player.serverLevel().getDayTime();
        boolean moon = MoonWorld.isMoon(player.level());
        String text = (moon ? "Луна: " : "Мир: ")
                + dev.leeeonidys.aeronauticsplus.space.core.LunarTime.describe(dayTime)
                + (moon ? "" : " (обычные ванильные сутки)");
        source.sendSuccess(() -> Component.literal(text), false);
        return 1;
    }

    private static int world(CommandSourceStack source, String bodyId) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Нужен игрок."));
            return 0;
        }
        String message = BodyWorldEnter.go(player, bodyId);
        if (message.startsWith("Нет поверхности") || message.startsWith("Измерение")) {
            source.sendFailure(Component.literal(message));
            return 0;
        }
        source.sendSuccess(() -> Component.literal(message), false);
        return 1;
    }

    private static int state(CommandSourceStack source, String bodyId) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Нужен игрок."));
            return 0;
        }
        CelestialBody body;
        try {
            body = SolarSystem.body(bodyId);
        } catch (IllegalArgumentException exception) {
            source.sendFailure(Component.literal(exception.getMessage()));
            return 0;
        }
        if (StationPocket.isStation(player.level())) {
            StationEnter.leave(player);
        }
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            source.sendFailure(Component.literal("Нет оверворлда."));
            return 0;
        }
        FlightLoop parked = CreativeOrbit.parkedAround(body);
        OrbitMapData.get(overworld).enter(parked);
        String entered = StationEnter.enter(player);
        source.sendSuccess(
                () -> Component.literal("Состояние " + body.id() + ". " + entered),
                false);
        return 1;
    }
}
