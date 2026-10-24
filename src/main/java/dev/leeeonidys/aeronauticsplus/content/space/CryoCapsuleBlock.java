package dev.leeeonidys.aeronauticsplus.content.space;

import com.mojang.serialization.MapCodec;
import dev.leeeonidys.aeronauticsplus.space.core.LunarTime;
import dev.leeeonidys.aeronauticsplus.space.world.BodyClockData;
import dev.leeeonidys.aeronauticsplus.space.world.MarsWorld;
import dev.leeeonidys.aeronauticsplus.space.world.MoonWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Cryo capsule: the way to get through a lunar night, since beds do not work off Earth
 * ({@code bed_works: false} on both the Moon and Mars).
 *
 * <p>It is not a bed clone. A bed cannot be used here because vanilla beds explode in
 * non-bed dimensions, and a bed also does not exist as a spawn anchor on an airless body.
 * The capsule:

 * <ul>
 *   <li>refuses in daylight — there is nothing to skip;</li>
 *   <li>sets the respawn point to itself, so a dead crew member returns to the base;</li>
 *   <li>skips the remaining night. On the Moon that means shifting the clock offset
 *       ({@link BodyClockData}), because {@code dayTime} is recomputed every tick; on Mars and
 *       anywhere else the vanilla day is simply moved forward.</li>
 * </ul>
 *
 * <p>Scope discipline: the capsule does not supply oxygen, power or heat, and it does not
 * pretend to. Life support is ChemMod's; this block only owns time and the spawn point.</p>
 */
public final class CryoCapsuleBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<CryoCapsuleBlock> CODEC = simpleCodec(CryoCapsuleBlock::new);

    public CryoCapsuleBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        // Faces the builder, like any machine front.
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }
        if (serverLevel.dimension().equals(Level.OVERWORLD)) {
            tell(serverPlayer, "На Земле спите в кровати: криокапсула для тел без атмосферы.");
            return InteractionResult.CONSUME;
        }
        long dayTime = serverLevel.getDayTime();
        if (LunarTime.isDay(dayTime)) {
            tell(serverPlayer, "Сейчас " + LunarTime.label(dayTime)
                    + ": криокапсула не нужна, ночь ещё не началась.");
            return InteractionResult.CONSUME;
        }
        if (serverPlayer.isPassenger()) {
            serverPlayer.stopRiding();
        }
        serverPlayer.setRespawnPosition(
                serverLevel.dimension(), pos, serverPlayer.getYRot(), true, true);
        double hours;
        if (MoonWorld.isMoon(serverLevel)) {
            hours = BodyClockData.get(serverLevel).skipToNextDawn(serverLevel);
        } else {
            // Vanilla-speed body: move the day forward to the next sunrise directly.
            long dawn = dayTime - Math.floorMod(dayTime, (long) LunarTime.VANILLA_DAY_TICKS)
                    + LunarTime.VANILLA_DAY_TICKS;
            hours = (dawn - dayTime) / (double) LunarTime.TICKS_PER_SECOND / 3600.0;
            serverLevel.setDayTime(dawn);
        }
        tell(serverPlayer, String.format(
                java.util.Locale.ROOT,
                "Криокапсула: %.1f ч до рассвета%s. Точка возрождения — здесь.",
                hours,
                MarsWorld.isMars(serverLevel) ? " (марсианские сутки почти земные)" : ""));
        return InteractionResult.CONSUME;
    }

    private static void tell(ServerPlayer player, String text) {
        player.displayClientMessage(Component.literal(text), false);
    }
}
