package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.compile.StationPlan;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitState;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

/** Occupied station islands. Lives on the Overworld so the pocket can unload. */
public final class StationPocketData extends SavedData {
    public static final String ID = "aeronauticsplus_station_pocket";

    public record Island(
            int index,
            FlightLoop loop,
            BlockPos returnPos,
            ResourceKey<Level> returnDim) {
    }

    private final List<Island> islands = new ArrayList<>();

    public static StationPocketData get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(StationPocketData::new, StationPocketData::load),
                ID);
    }

    public static StationPocketData load(CompoundTag tag, HolderLookup.Provider registries) {
        StationPocketData data = new StationPocketData();
        ListTag list = tag.getList("Islands", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            try {
                GridPos pad = new GridPos(entry.getInt("PadX"), entry.getInt("PadY"), entry.getInt("PadZ"));
                OrbitState orbit = FlightCodec.readOrbit(entry);
                if (orbit == null) {
                    continue;
                }
                FlightLoop loop = FlightCodec.restore(entry.getString("Parts"), pad, orbit);
                if (loop.hasFairing()) {
                    try {
                        loop = loop.dropFairing();
                    } catch (IllegalStateException ignored) {
                        continue;
                    }
                }
                loop = loop.occupyStation();
                BlockPos ret = new BlockPos(entry.getInt("Rx"), entry.getInt("Ry"), entry.getInt("Rz"));
                ResourceLocation dimId = ResourceLocation.tryParse(entry.getString("Rdim"));
                if (dimId == null) {
                    continue;
                }
                ResourceKey<Level> dim = ResourceKey.create(
                        net.minecraft.core.registries.Registries.DIMENSION, dimId);
                data.islands.add(new Island(entry.getInt("Index"), loop, ret, dim));
            } catch (RuntimeException ignored) {
                // skip a corrupt island
            }
        }
        return data;
    }

    public Island add(FlightLoop loop, BlockPos returnPos, ResourceKey<Level> returnDim) {
        if (loop == null || loop.presence() != FlightPresence.STATION_BLOCKS) {
            throw new IllegalStateException("Island needs occupied station blocks");
        }
        int index = 0;
        while (used(index)) {
            index++;
        }
        Island island = new Island(index, loop, returnPos, returnDim);
        islands.add(island);
        setDirty();
        return island;
    }

    public Island nearest(BlockPos pos) {
        Island best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (Island island : islands) {
            BlockPos origin = StationPocket.islandOrigin(island.index());
            double dx = pos.getX() - origin.getX();
            double dz = pos.getZ() - origin.getZ();
            double dist = dx * dx + dz * dz;
            if (dist < bestDist) {
                bestDist = dist;
                best = island;
            }
        }
        if (best == null || bestDist > 256.0 * 256.0) {
            return null;
        }
        return best;
    }

    public Island remove(Island island) {
        if (islands.remove(island)) {
            setDirty();
            return island;
        }
        return null;
    }

    public StationPlan plan(Island island) {
        return island.loop().station();
    }

    /** Parked stations kept as map tracks for the system overview. */
    public List<FlightLoop> loops() {
        return islands.stream().map(Island::loop).toList();
    }

    private boolean used(int index) {
        for (Island island : islands) {
            if (island.index() == index) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Island island : islands) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("Index", island.index());
            GridPos pad = island.loop().padLeft().origin();
            entry.putInt("PadX", pad.x());
            entry.putInt("PadY", pad.y());
            entry.putInt("PadZ", pad.z());
            entry.putString("Parts", FlightCodec.encodeFlying(island.loop()));
            FlightCodec.putOrbit(entry, island.loop().vessel().orbit());
            entry.putInt("Rx", island.returnPos().getX());
            entry.putInt("Ry", island.returnPos().getY());
            entry.putInt("Rz", island.returnPos().getZ());
            entry.putString("Rdim", island.returnDim().location().toString());
            list.add(entry);
        }
        tag.put("Islands", list);
        return tag;
    }
}
