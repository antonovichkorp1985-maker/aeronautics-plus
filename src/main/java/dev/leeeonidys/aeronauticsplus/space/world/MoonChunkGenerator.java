package dev.leeeonidys.aeronauticsplus.space.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
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
 * Cratered dusty hills. Not n-body, not ChemMod veins.
 *
 * <p>Relief is fractal value noise with domain warping: three crater lattices of different scales
 * (jittered so no grid shows), ridged noise for wrinkle ridges on the maria, and a separate
 * regolith thickness field over a compact rock substrate. Every value is a pure function of the
 * world seed and the block coordinates, so chunk borders match without any cross-chunk state.</p>
 */
public final class MoonChunkGenerator extends ChunkGenerator {
    public static final MapCodec<MoonChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(MoonChunkGenerator::getBiomeSource)
            ).apply(instance, MoonChunkGenerator::new));

    /** Mean mare level, blocks. */
    private static final int BASE = 62;
    private static final int MIN_SURFACE = 6;
    private static final int MAX_SURFACE = 150;

    /** Jittered crater lattices, fine to coarse. */
    private static final int[] CRATER_CELL = {71, 173, 421};
    private static final double[] CRATER_CHANCE = {0.62, 0.34, 0.16};
    private static final double[] CRATER_MIN_RADIUS = {4.5, 13.0, 38.0};
    private static final double[] CRATER_MAX_RADIUS = {11.0, 31.0, 92.0};

    private volatile long terrainSeed;

    public MoonChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    /**
     * The world seed, pulled out of the vanilla random state once. {@code ChunkGenerator} is not
     * handed the {@code ServerLevel}, but {@link RandomState} is seeded from it, so a named factory
     * gives a stable per-world number instead of the same terrain in every world.
     */
    private long seedOf(RandomState random) {
        long cached = terrainSeed;
        if (cached == 0L) {
            cached = random.getOrCreateRandomFactory(
                            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "moon_terrain"))
                    .at(0, 0, 0).nextLong();
            terrainSeed = cached;
        }
        return cached;
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
        // No caves yet. Vacuum overburden is ChemMod later.
    }

    @Override
    public void buildSurface(
            WorldGenRegion level,
            StructureManager structures,
            RandomState random,
            ChunkAccess chunk) {
        // fillFromNoise already placed the dust and the rock under it.
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
        // Unmanned. No lunar fauna.
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
        BlockState dust = AeronauticsPlus.MOON_REGOLITH.get().defaultBlockState();
        BlockState substrate = Blocks.TUFF.defaultBlockState();
        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        int min = chunk.getMinBuildHeight();
        int max = chunk.getMaxBuildHeight();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int originX = chunk.getPos().getMinBlockX();
        int originZ = chunk.getPos().getMinBlockZ();
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int worldX = originX + lx;
                int worldZ = originZ + lz;
                cursor.set(worldX, min, worldZ);
                chunk.setBlockState(cursor, bedrock, false);
                int surface = surfaceY(seed, worldX, worldZ);
                int dustTop = Math.max(min + 1, surface);
                int dustFrom = Math.max(min + 1, surface - regolithDepth(seed, worldX, worldZ) + 1);
                for (int y = min + 1; y <= dustTop && y < max; y++) {
                    cursor.setY(y);
                    chunk.setBlockState(cursor, y >= dustFrom ? dust : substrate, false);
                }
            }
        }
        Heightmap.primeHeightmaps(chunk, EnumSet.of(
                Heightmap.Types.WORLD_SURFACE_WG,
                Heightmap.Types.WORLD_SURFACE,
                Heightmap.Types.OCEAN_FLOOR_WG,
                Heightmap.Types.OCEAN_FLOOR,
                Heightmap.Types.MOTION_BLOCKING,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES));
        return CompletableFuture.completedFuture(chunk);
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
        return Math.max(level.getMinBuildHeight() + 1, surfaceY(seedOf(random), x, z) + 1);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        long seed = seedOf(random);
        int min = height.getMinBuildHeight();
        int len = height.getHeight();
        BlockState dust = AeronauticsPlus.MOON_REGOLITH.get().defaultBlockState();
        BlockState substrate = Blocks.TUFF.defaultBlockState();
        BlockState[] column = new BlockState[len];
        int surface = surfaceY(seed, x, z);
        int dustFrom = Math.max(min + 1, surface - regolithDepth(seed, x, z) + 1);
        for (int i = 0; i < len; i++) {
            int y = min + i;
            if (y <= min) {
                column[i] = Blocks.BEDROCK.defaultBlockState();
            } else if (y <= surface) {
                column[i] = y >= dustFrom ? dust : substrate;
            } else {
                column[i] = Blocks.AIR.defaultBlockState();
            }
        }
        return new NoiseColumn(min, column);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        info.add("aeronauticsplus moon g=" + String.format(java.util.Locale.ROOT, "%.2f", MoonWorld.GRAVITY_FACTOR));
    }

    public static int surfaceY(long seed, int x, int z) {
        int y = (int) Math.round(height(seed, x, z));
        return Math.max(MIN_SURFACE, Math.min(MAX_SURFACE, y));
    }

    /** Relief before clamping, blocks above y = 0. Public for {@code TerrainSlice}. */
    public static double height(long seed, int x, int z) {
        double warpX = TerrainNoise.fbm2(seed ^ 0x51A7L, x * 0.0042, z * 0.0042, 3, 2.0, 0.5) * 46.0;
        double warpZ = TerrainNoise.fbm2(seed ^ 0x77B3L, x * 0.0042, z * 0.0042, 3, 2.0, 0.5) * 46.0;
        double wx = x + warpX;
        double wz = z + warpZ;
        double maria = TerrainNoise.fbm2(seed ^ 0x1D2L, wx * 0.0031, wz * 0.0031, 4, 2.0, 0.55) * 9.5;
        double highlands = TerrainNoise.fbm2(seed ^ 0x2C4L, wx * 0.017, wz * 0.017, 5, 2.0, 0.5) * 7.5;
        double grain = TerrainNoise.fbm2(seed ^ 0x3E6L, wx * 0.13, wz * 0.13, 3, 2.0, 0.5) * 1.2;
        double ridges = Math.max(0.0,
                TerrainNoise.ridged2(seed ^ 0x4A8L, wx * 0.0052, wz * 0.0052, 3, 2.0, 0.5) - 0.72) * 34.0;
        return BASE + maria + highlands + grain + ridges + craters(seed, x, z);
    }

    /** Loose dust thickness on top of the compact substrate, 3..9 blocks. */
    static int regolithDepth(long seed, int x, int z) {
        double n = TerrainNoise.fbm2(seed ^ 0x6B1L, x * 0.06, z * 0.06, 3, 2.0, 0.5);
        return (int) Math.round(3.0 + (n + 1.0) * 2.8);
    }

    /** Sum of every crater whose rim can still reach this column. */
    private static double craters(long seed, int x, int z) {
        double sum = 0.0;
        for (int scale = 0; scale < CRATER_CELL.length; scale++) {
            int cell = CRATER_CELL[scale];
            int gx = Math.floorDiv(x, cell);
            int gz = Math.floorDiv(z, cell);
            for (int ox = -1; ox <= 1; ox++) {
                for (int oz = -1; oz <= 1; oz++) {
                    sum += crater(seed, scale, gx + ox, gz + oz, cell, x, z);
                }
            }
        }
        return sum;
    }

    /**
     * One crater: parabolic bowl, raised rim, ejecta tail and a central peak on the big ones.
     * Returns 0 far outside the rim, so the sum never depends on scan order.
     */
    private static double crater(long seed, int scale, int gx, int gz, int cell, int x, int z) {
        long cellSeed = TerrainNoise.mix(seed ^ 0x7F4A7C15L, scale, gx, gz);
        if (((cellSeed >>> 11) & 0x1FFFFFL) / 2097151.0 > CRATER_CHANCE[scale]) {
            return 0.0;
        }
        double u1 = TerrainNoise.unit(cellSeed, 1, 0, 0);
        double u2 = TerrainNoise.unit(cellSeed, 2, 0, 0);
        double u3 = TerrainNoise.unit(cellSeed, 3, 0, 0);
        double u4 = TerrainNoise.unit(cellSeed, 4, 0, 0);
        double centerX = gx * cell + 0.5 * cell + (u1 - 0.5) * cell * 0.7;
        double centerZ = gz * cell + 0.5 * cell + (u2 - 0.5) * cell * 0.7;
        double radius = CRATER_MIN_RADIUS[scale]
                + Math.pow(u3, 1.7) * (CRATER_MAX_RADIUS[scale] - CRATER_MIN_RADIUS[scale]);
        double dx = x - centerX;
        double dz = z - centerZ;
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance > radius * 1.9) {
            return 0.0;
        }
        double t = distance / radius;
        double bowl = t < 1.0 ? -radius * 0.20 * (1.0 - t * t) * (1.0 - 0.3 * t) : 0.0;
        double e = (t - 1.0) / 0.30;
        double rim = radius * 0.085 * Math.exp(-e * e);
        double peak = scale >= 1 && t < 0.42 ? radius * 0.10 * (1.0 - t / 0.42) : 0.0;
        double ragged = 1.0 + 0.25 * TerrainNoise.value2(
                seed ^ 0x5EEDL, centerX * 0.05 + distance * 0.15, centerZ * 0.05);
        return (bowl * ragged + rim + peak) * (0.7 + 0.6 * u4);
    }
}
