package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.content.rocket.CraneLoadEntity;
import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlock;
import dev.leeeonidys.aeronauticsplus.content.rocket.VesselPartBlocks;
import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.CraneJob;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import dev.leeeonidys.aeronauticsplus.space.compile.WorldCrane;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Timed lifts hanging off whoever the pack called a crane. Not an AP crane block.
 */
public final class CraneLiftData extends SavedData {
    public static final String ID = "aeronauticsplus_crane_lifts";
    private static final double STEP_SECONDS = 0.05;

    private final Map<BlockPos, Lift> lifts = new HashMap<>();

    public static CraneLiftData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CraneLiftData::new, CraneLiftData::load),
                ID);
    }

    public static CraneLiftData load(CompoundTag tag, HolderLookup.Provider registries) {
        CraneLiftData data = new CraneLiftData();
        ListTag list = tag.getList("Lifts", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            try {
                data.lifts.put(readPos(entry, "Crane"), Lift.read(entry));
            } catch (RuntimeException ignored) {
                // skip a corrupt row
            }
        }
        return data;
    }

    public String command(ServerLevel level, ServerPlayer player) {
        BlockHitResult hit = clip(level, player);
        BlockPos crane = nearestCrane(level, player.blockPosition(),
                hit.getType() == HitResult.Type.BLOCK ? hit.getBlockPos() : player.blockPosition());
        if (crane == null) {
            return "Нет крана в вылете. Gantry, шкив, поршень Create или блок из тега cranes.";
        }
        WorldCrane.Kind kind = WorldCrane.kindOf(BuiltInRegistries.BLOCK.getKey(level.getBlockState(crane).getBlock()).toString());
        Lift current = lifts.get(crane);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return current == null ? "Смотри на деталь или на клетку." : kindPhrase(kind) + " занят. Смотри, куда ставить.";
        }
        BlockPos looked = hit.getBlockPos();
        BlockState lookedState = level.getBlockState(looked);
        if (current == null) {
            return pick(level, crane, kind, looked, lookedState);
        }
        if (current.to == null) {
            BlockPos dest = lookedState.isAir() ? looked : looked.relative(hit.getDirection());
            return startHaul(level, crane, kind, current, dest);
        }
        return kindPhrase(kind) + " занят.";
    }

    public void tick(ServerLevel level) {
        Iterator<Map.Entry<BlockPos, Lift>> it = lifts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Lift> entry = it.next();
            Lift lift = entry.getValue();
            if (!level.getBlockState(entry.getKey()).is(AeronauticsPlus.CRANES)) {
                drop(level, lift);
                it.remove();
                setDirty();
                continue;
            }
            syncLoad(level, lift);
            if (lift.to == null) {
                continue;
            }
            lift.elapsed += STEP_SECONDS;
            syncLoad(level, lift);
            if (lift.elapsed + 1.0e-9 < lift.duration) {
                setDirty();
                continue;
            }
            place(level, lift);
            it.remove();
            setDirty();
        }
    }

    private String pick(ServerLevel level, BlockPos crane, WorldCrane.Kind kind, BlockPos looked, BlockState state) {
        if (!(state.getBlock() instanceof VesselPartBlock part) || part.spec().kind() == VesselPartKind.PAD) {
            return "Кран берёт деталь ракеты, не стол.";
        }
        GridPos craneGrid = grid(crane);
        GridPos fromGrid = grid(looked);
        if (!WorldCrane.inReach(craneGrid, fromGrid)) {
            return "Вне вылета крана.";
        }
        Lift lift = new Lift(
                part.spec().id(),
                faceOf(state.getValue(VesselPartBlock.FACING)),
                looked.immutable(),
                null,
                0.0,
                0.0,
                0,
                kind);
        level.removeBlock(looked, false);
        lifts.put(crane.immutable(), lift);
        syncLoad(level, lift);
        setDirty();
        return kindPhrase(kind) + ". На крюке.";
    }

    private String startHaul(ServerLevel level, BlockPos crane, WorldCrane.Kind kind, Lift lift, BlockPos dest) {
        if (!level.getBlockState(dest).isAir()) {
            return "Клетка занята.";
        }
        String reject = WorldCrane.rejectLift(
                grid(crane), grid(lift.from), grid(dest), lift.to != null, false);
        if (!reject.isEmpty()) {
            return switch (reject) {
                case "CRANE_BUSY" -> kindPhrase(kind) + " занят.";
                case "SAME_CELL" -> "Кран не спавнит на месте.";
                case "TOO_FAR" -> "Вне вылета крана.";
                case "OVERLAP" -> "Клетка занята.";
                default -> reject;
            };
        }
        lift.to = dest.immutable();
        lift.duration = CraneJob.durationSeconds(grid(lift.from), grid(dest));
        lift.elapsed = 0.0;
        syncLoad(level, lift);
        setDirty();
        return kindPhrase(kind) + " везёт.";
    }

    private void place(ServerLevel level, Lift lift) {
        discardLoad(level, lift);
        BlockState state = VesselPartBlocks.stateFor(lift.specId, lift.facing);
        if (state != null && lift.to != null && level.getBlockState(lift.to).isAir()) {
            level.setBlock(lift.to, state, 3);
        } else if (state != null && lift.to != null) {
            dropItem(level, lift.to, state);
        }
    }

    private void drop(ServerLevel level, Lift lift) {
        discardLoad(level, lift);
        BlockState state = VesselPartBlocks.stateFor(lift.specId, lift.facing);
        BlockPos at = lift.to == null ? lift.from : lift.from;
        if (state != null && level.getBlockState(at).isAir()) {
            level.setBlock(at, state, 3);
        } else if (state != null) {
            dropItem(level, at, state);
        }
    }

    private void dropItem(ServerLevel level, BlockPos at, BlockState state) {
        level.addFreshEntity(new ItemEntity(
                level, at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, new ItemStack(state.getBlock())));
    }

    private void syncLoad(ServerLevel level, Lift lift) {
        Vec3 here = lerp(lift);
        if (lift.loadId != 0 && level.getEntity(lift.loadId) instanceof CraneLoadEntity load) {
            load.setPart(lift.specId, lift.facing);
            load.setPos(here.x, here.y, here.z);
            return;
        }
        CraneLoadEntity load = new CraneLoadEntity(AeronauticsPlus.CRANE_LOAD.get(), level);
        load.moveTo(here.x, here.y, here.z, 0.0f, 0.0f);
        load.setPart(lift.specId, lift.facing);
        level.addFreshEntity(load);
        lift.loadId = load.getId();
    }

    private void discardLoad(ServerLevel level, Lift lift) {
        if (lift.loadId != 0 && level.getEntity(lift.loadId) instanceof CraneLoadEntity load) {
            load.discard();
        }
        lift.loadId = 0;
    }

    private static Vec3 lerp(Lift lift) {
        if (lift.to == null) {
            return new Vec3(lift.from.getX() + 0.5, lift.from.getY(), lift.from.getZ() + 0.5);
        }
        double t = lift.duration <= 0.0 ? 1.0 : Math.min(1.0, lift.elapsed / lift.duration);
        return new Vec3(
                lift.from.getX() + 0.5 + (lift.to.getX() - lift.from.getX()) * t,
                lift.from.getY() + (lift.to.getY() - lift.from.getY()) * t,
                lift.from.getZ() + 0.5 + (lift.to.getZ() - lift.from.getZ()) * t);
    }

    private static BlockPos nearestCrane(ServerLevel level, BlockPos player, BlockPos site) {
        int r = WorldCrane.REACH_METERS;
        BlockPos best = null;
        double bestD = Double.POSITIVE_INFINITY;
        BlockPos origin = site;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (!level.getBlockState(pos).is(AeronauticsPlus.CRANES)) {
                        continue;
                    }
                    double d = pos.distSqr(origin);
                    if (d < bestD && WorldCrane.inReach(grid(pos), grid(player))) {
                        bestD = d;
                        best = pos.immutable();
                    }
                }
            }
        }
        return best;
    }

    private static BlockHitResult clip(ServerLevel level, ServerPlayer player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(WorldCrane.REACH_METERS));
        return level.clip(new ClipContext(
                start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    private static String kindPhrase(WorldCrane.Kind kind) {
        return switch (kind) {
            case TOWER -> "Башня";
            case CARRIAGE -> "Каретка";
            case MOBILE -> "Транспортный кран";
        };
    }

    private static GridPos grid(BlockPos pos) {
        return new GridPos(pos.getX(), pos.getY(), pos.getZ());
    }

    private static BlockFace faceOf(Direction direction) {
        return switch (direction) {
            case DOWN -> BlockFace.DOWN;
            case UP -> BlockFace.UP;
            case NORTH -> BlockFace.NORTH;
            case SOUTH -> BlockFace.SOUTH;
            case WEST -> BlockFace.WEST;
            case EAST -> BlockFace.EAST;
        };
    }

    private static BlockPos readPos(CompoundTag tag, String prefix) {
        return new BlockPos(tag.getInt(prefix + "X"), tag.getInt(prefix + "Y"), tag.getInt(prefix + "Z"));
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, Lift> entry : lifts.entrySet()) {
            CompoundTag row = new CompoundTag();
            row.putInt("CraneX", entry.getKey().getX());
            row.putInt("CraneY", entry.getKey().getY());
            row.putInt("CraneZ", entry.getKey().getZ());
            entry.getValue().write(row);
            list.add(row);
        }
        tag.put("Lifts", list);
        return tag;
    }

    private static final class Lift {
        private final String specId;
        private final BlockFace facing;
        private final BlockPos from;
        private BlockPos to;
        private double duration;
        private double elapsed;
        private int loadId;
        private final WorldCrane.Kind kind;

        private Lift(
                String specId,
                BlockFace facing,
                BlockPos from,
                BlockPos to,
                double duration,
                double elapsed,
                int loadId,
                WorldCrane.Kind kind) {
            this.specId = specId;
            this.facing = facing;
            this.from = from;
            this.to = to;
            this.duration = duration;
            this.elapsed = elapsed;
            this.loadId = loadId;
            this.kind = kind;
        }

        private void write(CompoundTag tag) {
            tag.putString("Spec", specId);
            tag.putString("Facing", facing.name());
            tag.putInt("FromX", from.getX());
            tag.putInt("FromY", from.getY());
            tag.putInt("FromZ", from.getZ());
            if (to != null) {
                tag.putInt("ToX", to.getX());
                tag.putInt("ToY", to.getY());
                tag.putInt("ToZ", to.getZ());
            }
            tag.putDouble("Duration", duration);
            tag.putDouble("Elapsed", elapsed);
            tag.putString("Kind", kind.name());
        }

        private static Lift read(CompoundTag tag) {
            BlockPos to = tag.contains("ToX") ? readPos(tag, "To") : null;
            WorldCrane.Kind kind;
            try {
                kind = WorldCrane.Kind.valueOf(tag.getString("Kind"));
            } catch (RuntimeException ignored) {
                kind = WorldCrane.Kind.TOWER;
            }
            BlockFace facing;
            try {
                facing = BlockFace.valueOf(tag.getString("Facing"));
            } catch (RuntimeException ignored) {
                facing = BlockFace.UP;
            }
            return new Lift(
                    tag.getString("Spec"),
                    facing,
                    readPos(tag, "From"),
                    to,
                    tag.getDouble("Duration"),
                    tag.getDouble("Elapsed"),
                    0,
                    kind);
        }
    }
}
