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
 * Rust dunes, a rift valley and one shield volcano per world. Not ChemMod dust chemistry,
 * not n-body, and no water: any liquid stays ChemMod's problem.
 *
 * <p>Same kit as the Moon — fractal value noise with domain warping, jittered crater lattices —
 * but the detail is anisotropic on purpose: dunes are stretched along X so the crests run
 * north-south, and the canyon is a distance field around a wandering centre line instead of a
 * noise blob. The volcano is one point per world (two hashes), so every chunk can evaluate it
 * without knowing anything about its neighbours.</p>
 */
public final class MarsChunkGenerator extends ChunkGenerator {
    public static final MapCodec<MarsChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(MarsChunkGenerator::getBiomeSource)
            ).apply(instance, MarsChunkGenerator::new));

    /** Mean datum, blocks. */
    private static final int BASE = 70;
    private static final int MIN_SURFACE = 6;
    private static final int MAX_SURFACE = 168;

    /** Craters are smaller and shallower than on the Moon: Mars kept its air, and dust fills them. */
    private static final int[] CRATER_CELL = {61, 141, 353};
    private static final double[] CRATER_CHANCE = {0.55, 0.30, 0.14};
    private static final double[] CRATER_MIN_RADIUS = {4.0, 11.0, 30.0};
    private static final double[] CRATER_MAX_RADIUS = {9.0, 26.0, 74.0};

    private volatile long terrainSeed;

    public MarsChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    private long seedOf(RandomState random) {
        long cached = terrainSeed;
        if (cached == 0L) {
            cached = random.getOrCreateRandomFactory(
                            ResourceLocation.fromNamespaceAndPath(AeronauticsPlus.MODID, "mars_terrain"))
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
        // The canyon is part of the height field, not a carver. No caves yet.
    }

    @Override
    public void buildSurface(
            WorldGenRegion level,
            StructureManager structures,
            RandomState random,
            ChunkAccess chunk) {
        // fillFromNoise already placed dust and rock.
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
        // Unmanned. No Martian fauna.
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
        BlockState dust = AeronauticsPlus.MARS_REGOLITH.get().defaultBlockState();
        BlockState substrate = Blocks.STONE.defaultBlockState();
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
                int dustFrom = Math.max(min + 1, surface - dustDepth(seed, worldX, worldZ) + 1);
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
        BlockState dust = AeronauticsPlus.MARS_REGOLITH.get().defaultBlockState();
        BlockState substrate = Blocks.STONE.defaultBlockState();
        BlockState[] column = new BlockState[len];
        int surface = surfaceY(seed, x, z);
        int dustFrom = Math.max(min + 1, surface - dustDepth(seed, x, z) + 1);
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
        info.add("aeronauticsplus mars g=" + String.format(java.util.Locale.ROOT, "%.2f", MarsWorld.GRAVITY_FACTOR));
    }

    public static int surfaceY(long seed, int x, int z) {
        int y = (int) Math.round(height(seed, x, z));
        return Math.max(MIN_SURFACE, Math.min(MAX_SURFACE, y));
    }

    /** Relief before clamping, blocks above y = 0. Public for {@code TerrainSlice}. */
    public static double height(long seed, int x, int z) {
        double plains = TerrainNoise.fbm2(seed ^ 0x44D1L, x * 0.0022, z * 0.0022, 4, 2.0, 0.55) * 11.0;
        double dunes = TerrainNoise.fbm2(seed ^ 0x11D3L, x * 0.011, z * 0.055, 4, 2.0, 0.5) * 3.6;
        double ripples = TerrainNoise.fbm2(seed ^ 0x22D5L, x * 0.038, z * 0.16, 3, 2.0, 0.5) * 1.2;
        double yardangs = Math.max(0.0,
                TerrainNoise.ridged2(seed ^ 0x33D7L, x * 0.0045, z * 0.03, 3, 2.0, 0.5) - 0.66) * 22.0;
        double grain = TerrainNoise.fbm2(seed ^ 0x55D9L, x * 0.12, z * 0.12, 3, 2.0, 0.5) * 1.0;
        return BASE + plains + dunes + ripples + yardangs + grain
                + craters(seed, x, z) - canyon(seed, x, z) + volcano(seed, x, z);
    }

    /** Wind-blown dust cover, 4..11 blocks over the bedrock. */
    static int dustDepth(long seed, int x, int z) {
        double n = TerrainNoise.fbm2(seed ^ 0x6B2L, x * 0.045, z * 0.045, 3, 2.0, 0.5);
        return (int) Math.round(4.0 + (n + 1.0) * 3.5);
    }

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

    private static double crater(long seed, int scale, int gx, int gz, int cell, int x, int z) {
        long cellSeed = TerrainNoise.mix(seed ^ 0x2E9AL, scale, gx, gz);
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
        // Shallower than the Moon: dust drifts into the bowl and the rim softens.
        double bowl = t < 1.0 ? -radius * 0.13 * (1.0 - t * t) * (1.0 - 0.3 * t) : 0.0;
        double e = (t - 1.0) / 0.34;
        double rim = radius * 0.055 * Math.exp(-e * e);
        double ragged = 1.0 + 0.25 * TerrainNoise.value2(
                seed ^ 0x5EEDL, centerX * 0.05 + distance * 0.15, centerZ * 0.05);
        return (bowl * ragged + rim) * (0.7 + 0.6 * u4);
    }

    /**
     * Rift valley along the X axis: the centre line wanders, the width breathes, the walls step.
     * Returns a depth in blocks at this column, 0 outside the rim.
     */
    public static double canyon(long seed, int x, int z) {
        double center = canyonCentre(seed, x);
        double halfWidth = canyonHalfWidth(x);
        double d = Math.abs(z - center);
        if (d > halfWidth * 2.0) {
            return 0.0;
        }
        double t = d / halfWidth;
        if (t < 1.0) {
            double wall = Math.floor(t * 3.0) / 3.0;
            double depth = 34.0 * (1.0 - t * t) * (1.0 - 0.35 * wall);
            return Math.max(0.0, depth);
        }
        double e = (t - 1.0) / 0.35;
        return -2.5 * Math.exp(-e * e);
    }

    /** Wandering centre line of the rift valley, in blocks along Z. */
    public static double canyonCentre(long seed, int x) {
        return 900.0
                + 260.0 * Math.sin(x * 0.0016 + TerrainNoise.unit(seed, 0, 0, 0) * 6.2831853)
                + 120.0 * Math.sin(x * 0.0051 + 1.7);
    }

    /** Half-width of the rift at this X, blocks. */
    public static double canyonHalfWidth(int x) {
        return 26.0 + 10.0 * Math.sin(x * 0.0037);
    }

    /** The single volcano's centre, in blocks. Two hashes, so every chunk agrees. */
    public static double volcanoCentreX(long seed) {
        return TerrainNoise.unit(seed ^ 0x7A1L, 0, 0, 0) * 4000.0 - 2000.0;
    }

    public static double volcanoCentreZ(long seed) {
        return TerrainNoise.unit(seed ^ 0x7A2L, 0, 0, 0) * 4000.0 - 2000.0;
    }

    /** One shield volcano per world, from two hashes. Caldera included, no lava. */
    public static double volcano(long seed, int x, int z) {
        double centerX = volcanoCentreX(seed);
        double centerZ = volcanoCentreZ(seed);
        double dx = x - centerX;
        double dz = z - centerZ;
        double distance = Math.sqrt(dx * dx + dz * dz);
        double radius = 320.0;
        if (distance > radius) {
            return 0.0;
        }
        double cone = 62.0 * Math.pow(1.0 - distance / radius, 2.2);
        double caldera = distance < 34.0 ? 26.0 * (1.0 - distance / 34.0) : 0.0;
        return cone - caldera;
    }
}
