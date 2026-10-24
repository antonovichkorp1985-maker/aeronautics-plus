package dev.leeeonidys.aeronauticsplus.space.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

/**
 * Open void with floating rock. Not ChemMod veins, not n-body, and no floor: the belt is space,
 * so there is no bedrock slab under it any more.
 *
 * <p>Asteroids come from three jittered 3D lattices — pebbles, rocks and the rare giant — each
 * asteroid an ellipsoid with its own axis ratios, displaced by 3D value noise so it reads as a
 * lumpy potato rather than a sphere. A cell is hashed, not stored, and every asteroid is scanned
 * from the chunks it can reach, so one rock may span several chunks without a seam.</p>
 */
public final class AsteroidChunkGenerator extends ChunkGenerator {
    public static final MapCodec<AsteroidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(AsteroidChunkGenerator::getBiomeSource)
            ).apply(instance, AsteroidChunkGenerator::new));

    /** Asteroids live between these heights. Below the band there is nothing to stand on. */
    static final int BELT_MIN_Y = 12;
    static final int BELT_MAX_Y = 228;

    /** Pebbles: one or two blocks, the dust of the belt. */
    private static final Field DEBRIS = new Field(13, 0.34, 0.35, 1.15, 0x1BAL);
    /** Rocks: 2–9 blocks across, the ones a ship dodges. */
    private static final Field ROCKS = new Field(27, 0.52, 0.9, 4.6, 0x2C4L);
    /** Giants: 14–28 blocks across, rare. Big enough to land on later, still not a planetoid. */
    private static final Field GIANTS = new Field(108, 0.34, 7.0, 13.5, 0x3D6L);

    private volatile long terrainSeed;

    public AsteroidChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    private long seedOf(RandomState random) {
        long cached = terrainSeed;
        if (cached == 0L) {
            cached = random.getOrCreateRandomFactory(
                            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "asteroid_terrain"))
                    .at(0, 0, 0).nextLong();
            terrainSeed = cached;
        }
        return cached;
    }

    /** One asteroid family: lattice pitch, fill chance, radius range, hash salt. */
    private record Field(int cell, double chance, double minRadius, double maxRadius, long salt) {
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(
            WorldGenRegion level,
            long seed,
            RandomState random,
            BiomeManager biomes,
            StructureManager structures,
            ChunkAccess chunk,
            GenerationStep.Carving step) {
        // No caves.
    }

    @Override
    public void buildSurface(
            WorldGenRegion level,
            StructureManager structures,
            RandomState random,
            ChunkAccess chunk) {
        // Asteroids are placed in fillFromNoise.
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
        // Unmanned rocks.
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(
            Blender blender,
            RandomState random,
            StructureManager structures,
            ChunkAccess chunk) {
        long seed = seedOf(random);
        BlockState rock = AeronauticsPlus.ASTEROID_ROCK.get().defaultBlockState();
        int min = chunk.getMinBuildHeight();
        int max = chunk.getMaxBuildHeight();
        int originX = chunk.getPos().getMinBlockX();
        int originZ = chunk.getPos().getMinBlockZ();
        int lowY = Math.max(min + 1, BELT_MIN_Y);
        int highY = Math.min(max - 1, BELT_MAX_Y);
        // Chunks start as air, and the belt keeps it that way: nothing is written below lowY.
        scatter(seed, chunk, rock, originX, originZ, lowY, highY, DEBRIS);
        scatter(seed, chunk, rock, originX, originZ, lowY, highY, ROCKS);
        scatter(seed, chunk, rock, originX, originZ, lowY, highY, GIANTS);
        Heightmap.primeHeightmaps(chunk, EnumSet.of(
                Heightmap.Types.WORLD_SURFACE_WG,
                Heightmap.Types.WORLD_SURFACE,
                Heightmap.Types.OCEAN_FLOOR_WG,
                Heightmap.Types.OCEAN_FLOOR,
                Heightmap.Types.MOTION_BLOCKING,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES));
        return CompletableFuture.completedFuture(chunk);
    }

    private static void scatter(
            long seed,
            ChunkAccess chunk,
            BlockState rock,
            int originX,
            int originZ,
            int lowY,
            int highY,
            Field field) {
        int cell = field.cell();
        int pad = (int) Math.ceil(field.maxRadius() * 2.0) + 2;
        int gx0 = Math.floorDiv(originX - pad, cell);
        int gx1 = Math.floorDiv(originX + 15 + pad, cell);
        int gz0 = Math.floorDiv(originZ - pad, cell);
        int gz1 = Math.floorDiv(originZ + 15 + pad, cell);
        int gy0 = Math.floorDiv(lowY - pad, cell);
        int gy1 = Math.floorDiv(highY + pad, cell);
        MutableBlockPos cursor = new MutableBlockPos();
        for (int gy = gy0; gy <= gy1; gy++) {
            for (int gx = gx0; gx <= gx1; gx++) {
                for (int gz = gz0; gz <= gz1; gz++) {
                    place(seed, chunk, rock, cursor, originX, originZ, lowY, highY, field, gx, gy, gz);
                }
            }
        }
    }

    private static void place(
            long seed,
            ChunkAccess chunk,
            BlockState rock,
            MutableBlockPos cursor,
            int originX,
            int originZ,
            int lowY,
            int highY,
            Field field,
            int gx,
            int gy,
            int gz) {
        long cellSeed = TerrainNoise.mix(seed ^ field.salt(), gx, gy, gz);
        if (((cellSeed >>> 11) & 0x1FFFFFL) / 2097151.0 > field.chance()) {
            return;
        }
        int cell = field.cell();
        double u1 = TerrainNoise.unit(cellSeed, 1, 0, 0);
        double u2 = TerrainNoise.unit(cellSeed, 2, 0, 0);
        double u3 = TerrainNoise.unit(cellSeed, 3, 0, 0);
        double u4 = TerrainNoise.unit(cellSeed, 4, 0, 0);
        double u5 = TerrainNoise.unit(cellSeed, 5, 0, 0);
        double u6 = TerrainNoise.unit(cellSeed, 6, 0, 0);
        double u7 = TerrainNoise.unit(cellSeed, 7, 0, 0);
        double u8 = TerrainNoise.unit(cellSeed, 8, 0, 0);
        double u9 = TerrainNoise.unit(cellSeed, 9, 0, 0);
        // Cubed draws: most asteroids sit near the bottom of the range, a few are large.
        double radius = field.minRadius() + Math.pow(u3, 2.4) * (field.maxRadius() - field.minRadius());
        double ax = radius * (0.62 + 0.78 * u4);
        double ay = radius * (0.62 + 0.78 * u5);
        double az = radius * (0.62 + 0.78 * u6);
        double cx = gx * cell + 0.5 * cell + (u7 - 0.5) * cell * 0.8;
        double cy = gy * cell + 0.5 * cell + (u8 - 0.5) * cell * 0.8;
        double cz = gz * cell + 0.5 * cell + (u9 - 0.5) * cell * 0.8;
        if (cy + ay < lowY || cy - ay > highY) {
            return;
        }
        int x0 = Math.max(originX, (int) Math.floor(cx - ax * 1.35 - 1.0));
        int x1 = Math.min(originX + 15, (int) Math.ceil(cx + ax * 1.35 + 1.0));
        int z0 = Math.max(originZ, (int) Math.floor(cz - az * 1.35 - 1.0));
        int z1 = Math.min(originZ + 15, (int) Math.ceil(cz + az * 1.35 + 1.0));
        int y0 = Math.max(lowY, (int) Math.floor(cy - ay * 1.35 - 1.0));
        int y1 = Math.min(highY, (int) Math.ceil(cy + ay * 1.35 + 1.0));
        for (int x = x0; x <= x1; x++) {
            for (int z = z0; z <= z1; z++) {
                for (int y = y0; y <= y1; y++) {
                    double qx = (x - cx) / ax;
                    double qy = (y - cy) / ay;
                    double qz = (z - cz) / az;
                    double q = Math.sqrt(qx * qx + qy * qy + qz * qz);
                    double wobble = TerrainNoise.value3(seed ^ 0x9E37L, x * 0.23, y * 0.23, z * 0.23) * 0.30;
                    if (q + wobble > 1.0) {
                        continue;
                    }
                    cursor.set(x, y, z);
                    chunk.setBlockState(cursor, rock, false);
                }
            }
        }
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return Math.max(level.getMinBuildHeight() + 1, BELT_MIN_Y);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        int min = height.getMinBuildHeight();
        int len = height.getHeight();
        BlockState[] column = new BlockState[len];
        for (int i = 0; i < len; i++) {
            column[i] = Blocks.AIR.defaultBlockState();
        }
        return new NoiseColumn(min, column);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        info.add("aeronauticsplus asteroid_belt");
    }
}
