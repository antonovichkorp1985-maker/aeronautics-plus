package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.core.LunarTime;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Gives the Moon its own clock: {@code dayTime = gameTime / LunarTime.SLOWDOWN}, rewritten every
 * tick so vanilla's one-tick-per-tick advance is overwritten rather than fought.
 *
 * <p>The offset from {@link BodyClockData} is added before the division, which is how a cryo
 * capsule can skip a lunar night without fighting this tick hook.</p>
 *
 * <p>Two consequences worth knowing:</p>
 * <ul>
 *   <li>{@code /time set} does not stick on the Moon: the next tick recomputes the value from
 *       the level's game time. Use {@code /ap clock} to read it.</li>
 *   <li>{@code doDaylightCycle false} freezes the lunar clock, exactly as it freezes the others;
 *       the value snaps back to the schedule when the gamerule is turned on again.</li>
 * </ul>
 *
 * <p>The client is told the time by vanilla's sync every 20 ticks, which happens before the level
 * ticks — so the client's lunar clock is at most one sync interval behind. At 1/8 speed that is
 * 2–3 lunar ticks, invisible.</p>
 */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class MoonTimeEvents {
    private MoonTimeEvents() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        ServerLevel moon = MoonWorld.get(server);
        if (moon == null) {
            return;
        }
        if (!moon.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            return;
        }
        long target = LunarTime.dayTimeFor(moon.getGameTime(), BodyClockData.get(moon).offsetTicks());
        if (moon.getDayTime() != target) {
            moon.setDayTime(target);
        }
    }
}
