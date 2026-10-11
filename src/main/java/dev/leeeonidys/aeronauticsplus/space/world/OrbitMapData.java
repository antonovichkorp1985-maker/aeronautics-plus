package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.compile.GridPos;
import dev.leeeonidys.aeronauticsplus.space.core.FlightLoop;
import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMap;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapTrack;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitState;
import net.minecraft.world.entity.Entity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Overworld Kepler map. Survives chunk unload; reentry spawns {@link VesselEntity}. */
public final class OrbitMapData extends SavedData {
    public static final String ID = "aeronauticsplus_orbit_map";
    private static final double STEP_SECONDS = 0.05;

    private final OrbitMap map = new OrbitMap();
    private final List<FlightLoop> pendingSpawn = new ArrayList<>();

    public static OrbitMapData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(OrbitMapData::new, OrbitMapData::load),
                ID);
    }

    public static OrbitMapData load(CompoundTag tag, HolderLookup.Provider registries) {
        OrbitMapData data = new OrbitMapData();
        ListTag list = tag.getList("Vessels", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            try {
                GridPos pad = new GridPos(entry.getInt("PadX"), entry.getInt("PadY"), entry.getInt("PadZ"));
                OrbitState orbit = FlightCodec.readOrbit(entry);
                if (orbit == null) {
                    continue;
                }
                FlightLoop loop = FlightCodec.restore(entry.getString("Parts"), pad, orbit);
                if (loop.presence() == FlightPresence.ORBIT_MAP) {
                    data.map.enter(loop);
                } else {
                    data.pendingSpawn.add(loop);
                }
            } catch (RuntimeException ignored) {
                // skip a corrupt map row rather than crash the world
            }
        }
        return data;
    }

    public void enter(FlightLoop loop) {
        map.enter(loop);
        setDirty();
    }

    /** Map vehicles plus Overworld entities, for the Kepler screen. */
    public List<OrbitMapTrack> tracks(ServerLevel level) {
        List<OrbitMapTrack> tracks = new ArrayList<>();
        for (FlightLoop loop : map.snapshot()) {
            tracks.add(OrbitMapTrack.from(loop));
        }
        if (level != null) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof VesselEntity vessel) {
                    OrbitMapTrack track = vessel.mapTrack();
                    if (track != null) {
                        tracks.add(track);
                    }
                }
            }
        }
        return tracks;
    }

    public void tick(ServerLevel level) {
        boolean dirty = map.size() > 0 || !pendingSpawn.isEmpty();
        List<FlightLoop> reentered = map.size() > 0 ? map.advance(STEP_SECONDS) : List.of();
        List<FlightLoop> waiting = new ArrayList<>(pendingSpawn);
        waiting.addAll(reentered);
        pendingSpawn.clear();
        for (FlightLoop loop : waiting) {
            if (!VesselLaunch.spawn(level, loop)) {
                pendingSpawn.add(loop);
            }
        }
        if (dirty) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (FlightLoop loop : map.snapshot()) {
            list.add(write(loop));
        }
        for (FlightLoop loop : pendingSpawn) {
            list.add(write(loop));
        }
        tag.put("Vessels", list);
        return tag;
    }

    private static CompoundTag write(FlightLoop loop) {
        CompoundTag entry = new CompoundTag();
        GridPos pad = loop.padLeft().origin();
        entry.putInt("PadX", pad.x());
        entry.putInt("PadY", pad.y());
        entry.putInt("PadZ", pad.z());
        entry.putString("Parts", FlightCodec.encodeFlying(loop));
        FlightCodec.putOrbit(entry, loop.vessel().orbit());
        return entry;
    }
}
