package dev.leeeonidys.aeronauticsplus.content.rocket;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.CraneJob;
import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;
import dev.leeeonidys.aeronauticsplus.space.compile.WorldCrane;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * One gantry, one hook. The part is gone from the cell until the job ends.
 */
public final class AssemblyCraneBlockEntity extends BlockEntity {
    private static final double STEP_SECONDS = 0.05;

    private String specId = "";
    private BlockFace facing = BlockFace.UP;
    private BlockPos from = BlockPos.ZERO;
    private BlockPos to;
    private double durationSeconds;
    private double elapsedSeconds;
    private int loadId;

    public AssemblyCraneBlockEntity(BlockPos pos, BlockState state) {
        super(AeronauticsPlus.ASSEMBLY_CRANE_BE.get(), pos, state);
    }

    public boolean busy() {
        return !specId.isBlank();
    }

    public String handleUse(Player player) {
        if (level == null || player == null) {
            return "";
        }
        BlockHitResult hit = clipPastSelf(player);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return busy() ? "Кран занят. Смотри, куда ставить." : "Смотри на деталь, потом на клетку.";
        }
        BlockPos looked = hit.getBlockPos();
        if (busy() && to == null) {
            BlockPos dest = level.getBlockState(looked).isAir() ? looked : looked.relative(hit.getDirection());
            return startLift(dest);
        }
        if (busy()) {
            return "Кран занят.";
        }
        return pick(looked);
    }

    public void serverTick() {
        if (level == null || level.isClientSide || specId.isBlank()) {
            return;
        }
        syncLoad();
        if (to == null) {
            return;
        }
        elapsedSeconds += STEP_SECONDS;
        syncLoad();
        if (elapsedSeconds + 1.0e-9 < durationSeconds) {
            setChanged();
            return;
        }
        placeLoad();
    }

    public void dropLoad() {
        if (level == null || specId.isBlank()) {
            return;
        }
        discardLoad();
        BlockPos dropAt = to == null ? from : worldPosition;
        BlockState state = VesselPartBlocks.stateFor(specId, facing);
        if (state != null && level.getBlockState(dropAt).isAir()) {
            level.setBlock(dropAt, state, 3);
        } else if (state != null && !level.isClientSide) {
            ItemStack stack = new ItemStack(state.getBlock());
            level.addFreshEntity(new ItemEntity(
                    level, dropAt.getX() + 0.5, dropAt.getY() + 0.5, dropAt.getZ() + 0.5, stack));
        }
        clearJob();
    }

    private String pick(BlockPos looked) {
        BlockState state = level.getBlockState(looked);
        if (!(state.getBlock() instanceof VesselPartBlock part) || part.spec().kind() == VesselPartKind.PAD) {
            return "Кран берёт деталь ракеты, не стол.";
        }
        GridPos crane = grid(worldPosition);
        GridPos fromGrid = grid(looked);
        String reject = WorldCrane.rejectLift(crane, fromGrid, fromGrid.translate(0, 1, 0), false, false);
        if ("TOO_FAR".equals(reject) || !WorldCrane.inReach(crane, fromGrid)) {
            return "Вне вылета крана.";
        }
        specId = part.spec().id();
        facing = faceOf(state.getValue(VesselPartBlock.FACING));
        from = looked.immutable();
        to = null;
        durationSeconds = 0.0;
        elapsedSeconds = 0.0;
        level.removeBlock(looked, false);
        spawnLoad();
        setChanged();
        return "На крюке.";
    }

    private String startLift(BlockPos dest) {
        if (level.getBlockState(dest).isAir() == false) {
            return "Клетка занята.";
        }
        GridPos crane = grid(worldPosition);
        String reject = WorldCrane.rejectLift(crane, grid(from), grid(dest), to != null, !level.getBlockState(dest).isAir());
        if (!reject.isEmpty()) {
            return switch (reject) {
                case "CRANE_BUSY" -> "Кран занят.";
                case "SAME_CELL" -> "Кран не спавнит на месте.";
                case "TOO_FAR" -> "Вне вылета крана.";
                case "OVERLAP" -> "Клетка занята.";
                default -> reject;
            };
        }
        to = dest.immutable();
        durationSeconds = CraneJob.durationSeconds(grid(from), grid(to));
        elapsedSeconds = 0.0;
        syncLoad();
        setChanged();
        return "Кран везёт.";
    }

    private void placeLoad() {
        if (to == null) {
            return;
        }
        BlockState state = VesselPartBlocks.stateFor(specId, facing);
        discardLoad();
        if (state != null && level.getBlockState(to).isAir()) {
            level.setBlock(to, state, 3);
        } else if (state != null) {
            ItemStack stack = new ItemStack(state.getBlock());
            level.addFreshEntity(new ItemEntity(
                    level, to.getX() + 0.5, to.getY() + 0.5, to.getZ() + 0.5, stack));
        }
        clearJob();
        setChanged();
    }

    private void spawnLoad() {
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        Vec3 here = lerpVec();
        CraneLoadEntity load = new CraneLoadEntity(AeronauticsPlus.CRANE_LOAD.get(), server);
        load.moveTo(here.x, here.y, here.z, 0.0f, 0.0f);
        load.setPart(specId, facing);
        server.addFreshEntity(load);
        loadId = load.getId();
    }

    private void syncLoad() {
        if (level == null || loadId == 0) {
            return;
        }
        if (!(level.getEntity(loadId) instanceof CraneLoadEntity load)) {
            spawnLoad();
            return;
        }
        Vec3 here = lerpVec();
        load.setPart(specId, facing);
        load.setPos(here.x, here.y, here.z);
    }

    private Vec3 lerpVec() {
        if (to == null) {
            return new Vec3(from.getX() + 0.5, from.getY(), from.getZ() + 0.5);
        }
        double t = durationSeconds <= 0.0 ? 1.0 : Math.min(1.0, elapsedSeconds / durationSeconds);
        return new Vec3(
                from.getX() + 0.5 + (to.getX() - from.getX()) * t,
                from.getY() + (to.getY() - from.getY()) * t,
                from.getZ() + 0.5 + (to.getZ() - from.getZ()) * t);
    }

    private void discardLoad() {
        if (level != null && loadId != 0 && level.getEntity(loadId) instanceof CraneLoadEntity load) {
            load.discard();
        }
        loadId = 0;
    }

    private void clearJob() {
        specId = "";
        facing = BlockFace.UP;
        from = BlockPos.ZERO;
        to = null;
        durationSeconds = 0.0;
        elapsedSeconds = 0.0;
        loadId = 0;
    }

    private BlockHitResult clipPastSelf(Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(WorldCrane.REACH_METERS));
        BlockHitResult hit = level.clip(new ClipContext(
                start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(worldPosition)) {
            Vec3 next = Vec3.atCenterOf(worldPosition).add(player.getLookAngle().scale(0.6));
            return level.clip(new ClipContext(
                    next, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        }
        return hit;
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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
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
        tag.putDouble("Duration", durationSeconds);
        tag.putDouble("Elapsed", elapsedSeconds);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        specId = tag.getString("Spec");
        try {
            facing = BlockFace.valueOf(tag.getString("Facing"));
        } catch (RuntimeException ignored) {
            facing = BlockFace.UP;
        }
        from = new BlockPos(tag.getInt("FromX"), tag.getInt("FromY"), tag.getInt("FromZ"));
        if (tag.contains("ToX")) {
            to = new BlockPos(tag.getInt("ToX"), tag.getInt("ToY"), tag.getInt("ToZ"));
        }
        durationSeconds = tag.getDouble("Duration");
        elapsedSeconds = tag.getDouble("Elapsed");
    }
}
