package dev.leeeonidys.aeronauticsplus.space.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
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
 */
public final class MoonChunkGenerator extends ChunkGenerator {
    public static final MapCodec<MoonChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(MoonChunkGenerator::getBiomeSource)
            ).apply(instance, MoonChunkGenerator::new));

    public MoonChunkGenerator(BiomeSource biomeSource) {
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
        // No caves yet. Vacuum overburden is ChemMod later.
    }

    @Override
    public void buildSurface(
            WorldGenRegion level,
            StructureManager structures,
            RandomState random,
            ChunkAccess chunk) {
        // fillFromNoise already placed the dust.
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
        BlockState dust = AeronauticsPlus.MOON_REGOLITH.get().defaultBlockState();
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
                int surface = surfaceY(worldX, worldZ);
                for (int y = min; y < max; y++) {
                    cursor.set(worldX, y, worldZ);
                    if (y <= min) {
                        chunk.setBlockState(cursor, bedrock, false);
                    } else if (y <= surface) {
                        chunk.setBlockState(cursor, dust, false);
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
        BlockState dust = AeronauticsPlus.MOON_REGOLITH.get().defaultBlockState();
        BlockState[] column = new BlockState[len];
        int surface = surfaceY(x, z);
        for (int i = 0; i < len; i++) {
            int y = min + i;
            if (y <= min) {
                column[i] = Blocks.BEDROCK.defaultBlockState();
            } else if (y <= surface) {
                column[i] = dust;
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

    static int surfaceY(int x, int z) {
        double hills = Math.sin(x * 0.031) * Math.cos(z * 0.029) * 5.0
                + Math.sin(x * 0.11 + z * 0.07) * 2.0;
        int cellX = Math.floorDiv(x, 48);
        int cellZ = Math.floorDiv(z, 48);
        double cx = cellX * 48 + 24 + ((cellX * 13) % 7);
        double cz = cellZ * 48 + 24 + ((cellZ * 17) % 7);
        double dist = Math.hypot(x - cx, z - cz);
        double crater = dist < 14.0 ? (14.0 - dist) * 0.9 : 0.0;
        int y = 64 + (int) Math.round(hills - crater);
        return Math.max(8, Math.min(120, y));
    }
}
