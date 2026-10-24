package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.core.LunarTime;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Per-dimension clock offset. The lunar {@code dayTime} is a function of the level's
 * {@code gameTime}, so "skip to morning" cannot be a write to {@code dayTime}: the next tick
 * would recompute it. Instead the whole mapping is shifted and the shift is saved.
 *
 * <pre>dayTime = (gameTime + offset) / LunarTime.SLOWDOWN</pre>
 *
 * <p>Stored once per dimension, so the Moon keeps its own offset and the Overworld keeps none.
 * The value is a plain {@code long}, so NBT is one tag and there is nothing to migrate.</p>
 */
public final class BodyClockData extends SavedData {
    private static final String FILE_ID = "aeronauticsplus_body_clock";
    private static final String OFFSET_TAG = "OffsetTicks";

    private long offsetTicks;

    public static BodyClockData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(BodyClockData::new, BodyClockData::load, DataFixTypes.LEVEL),
                FILE_ID);
    }

    private static BodyClockData load(CompoundTag tag, HolderLookup.Provider registries) {
        BodyClockData data = new BodyClockData();
        data.offsetTicks = tag.getLong(OFFSET_TAG);
        return data;
    }

    public long offsetTicks() {
        return offsetTicks;
    }

    /**
     * Moves the clock to the next sunrise on a body with a slowed day and returns how many hours
     * of game time were skipped. No-op for a body whose day is not slowed by this mod.
     */
    public double skipToNextDawn(ServerLevel level) {
        long gameTime = level.getGameTime();
        long next = LunarTime.offsetForNextDawn(gameTime, offsetTicks);
        long before = LunarTime.dayTimeFor(gameTime, offsetTicks);
        long after = LunarTime.dayTimeFor(gameTime, next);
        offsetTicks = next;
        setDirty();
        // One dayTime tick is SLOWDOWN real ticks, so the wall-clock cost of the jump is the
        // dayTime delta scaled back up. Reading it off the clock keeps the number honest.
        return (after - before) * (double) LunarTime.SLOWDOWN
                / LunarTime.TICKS_PER_SECOND / 3600.0;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong(OFFSET_TAG, offsetTicks);
        return tag;
    }
}
