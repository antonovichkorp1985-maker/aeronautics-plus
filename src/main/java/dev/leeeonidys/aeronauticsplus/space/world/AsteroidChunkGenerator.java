package dev.leeeonidys.aeronauticsplus.space.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
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
 * Void with floating rock islands. Not ChemMod veins, not n-body.
 */
public final class AsteroidChunkGenerator extends ChunkGenerator {
    public static final MapCodec<AsteroidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(AsteroidChunkGenerator::getBiomeSource)
            ).apply(instance, AsteroidChunkGenerator::new));

    public AsteroidChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
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
        // Islands placed in fillFromNoise.
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
        BlockState rock = AeronauticsPlus.ASTEROID_ROCK.get().defaultBlockState();
        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        int min = chunk.getMinBuildHeight();
        int max = chunk.getMaxBuildHeight();
        MutableBlockPos cursor = new MutableBlockPos();
        int originX = chunk.getPos().getMinBlockX();
        int originZ = chunk.getPos().getMinBlockZ();

        // Void by default, bedrock at very bottom.
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                cursor.set(originX + lx, min, originZ + lz);
                chunk.setBlockState(cursor, bedrock, false);
                for (int y = min + 1; y < max; y++) {
                    cursor.setY(y);
                    chunk.setBlockState(cursor, Blocks.AIR.defaultBlockState(), false);
                }
            }
        }

        // Floating islands: rock blobs at 24..112.
        long seedX = originX * 7919L + originZ * 13L;
        long seedZ = originZ * 7919L + originX * 13L;
        java.util.Random rnd = new java.util.Random(seedX ^ seedZ);
        for (int island = 0; island < 3; island++) {
            int cx = originX + rnd.nextInt(16);
            int cz = originZ + rnd.nextInt(16);
            int cy = 24 + rnd.nextInt(88);
            int radius = 3 + rnd.nextInt(6);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    for (int dy = -radius; dy <= radius; dy++) {
                        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (dist <= radius && rnd.nextDouble() > 0.25) {
                            cursor.set(cx + dx, cy + dy, cz + dz);
                            if (cursor.getX() >= originX && cursor.getX() < originX + 16
                                    && cursor.getZ() >= originZ && cursor.getZ() < originZ + 16
                                    && cursor.getY() > min && cursor.getY() < max) {
                                chunk.setBlockState(cursor, rock, false);
                            }
                        }
                    }
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
        return Math.max(level.getMinBuildHeight() + 1, surfaceY(x, z) + 1);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        int min = height.getMinBuildHeight();
        int len = height.getHeight();
        BlockState rock = AeronauticsPlus.ASTEROID_ROCK.get().defaultBlockState();
        BlockState[] column = new BlockState[len];
        int surface = surfaceY(x, z);
        for (int i = 0; i < len; i++) {
            int y = min + i;
            if (y <= min) {
                column[i] = Blocks.BEDROCK.defaultBlockState();
            } else if (y <= surface && isRockHere(x, y, z)) {
                column[i] = rock;
            } else {
                column[i] = Blocks.AIR.defaultBlockState();
            }
        }
        return new NoiseColumn(min, column);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        info.add("aeronauticsplus asteroid_belt");
    }

    static int surfaceY(int x, int z) {
        double n = Math.sin(x * 0.05) * Math.cos(z * 0.07) * 8.0
                + Math.sin(x * 0.2 + z * 0.15) * 3.0;
        int cellX = Math.floorDiv(x, 64);
        int cellZ = Math.floorDiv(z, 64);
        double cx = cellX * 64 + 32 + ((cellX * 11) % 13);
        double cz = cellZ * 64 + 32 + ((cellZ * 19) % 13);
        double dist = Math.hypot(x - cx, z - cz);
        double island = dist < 10.0 ? (10.0 - dist) : 0.0;
        int y = 48 + (int) Math.round(n + island);
        return Math.max(16, Math.min(112, y));
    }

    static boolean isRockHere(int x, int y, int z) {
        if (y < 16 || y > 112) {
            return false;
        }
        int cellX = Math.floorDiv(x, 64);
        int cellZ = Math.floorDiv(z, 64);
        double cx = cellX * 64 + 32 + ((cellX * 11) % 13);
        double cz = cellZ * 64 + 32 + ((cellZ * 19) % 13);
        double dist = Math.hypot(x - cx, z - cz);
        double n = Math.sin(x * 0.05) * Math.cos(z * 0.07) * 8.0
                + Math.sin(x * 0.2 + z * 0.15) * 3.0;
        double island = dist < 10.0 ? (10.0 - dist) : 0.0;
        double surface = 48.0 + n + island;
        return y <= Math.floor(surface) && y >= 16;
    }
}
