package dev.leeeonidys.aeronauticsplus.space.world;

/**
 * Value-noise kit for the two hand-built dimensions.
 *
 * <p>The Moon and the asteroid belt are plain {@link net.minecraft.world.level.chunk.ChunkGenerator}s
 * with their own codecs, so they cannot borrow a JSON noise router; they carry their own noise
 * instead. Everything here is a pure function of {@code (seed, x, y, z)} — no state, no per-chunk
 * cache — so two neighbouring chunks always agree on the blocks along their shared border. That
 * is what fixed the old hand-written {@code sin(x)·cos(z)} terrain, which repeated every lattice
 * cell and left visible seams where a blob was clipped to its own chunk.</p>
 *
 * <p>Not ChemMod: this is rock shape, not gas. Not n-body: no gravity field here either.</p>
 */
final class TerrainNoise {
    /** 2^-53, so a shifted 53-bit hash maps to [0, 1) without a division. */
    private static final double INV_2_53 = 0x1.0p-53;
    private static final long GOLDEN = 0x9E3779B97F4A7C15L;

    private TerrainNoise() {
    }

    /** SplitMix64 finaliser: cheap avalanche, good enough for terrain hashing. */
    static long scramble(long value) {
        long z = value + GOLDEN;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    /** Deterministic 64-bit hash of a lattice point for a given world seed. */
    static long mix(long seed, long x, long y, long z) {
        return scramble(seed
                ^ (x * 0x9E3779B97F4A7C15L)
                ^ (y * 0xC2B2AE3D27D4EB4FL)
                ^ (z * 0x165667B19E3779F9L));
    }

    /** Hash to [0, 1). */
    static double unit(long seed, int x, int y, int z) {
        return (mix(seed, x, y, z) >>> 11) * INV_2_53;
    }

    static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double smooth(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    private static double fade(double a, double b, double c, double d, double fx, double fz) {
        return lerp(lerp(a, b, fx), lerp(c, d, fx), fz);
    }

    /** Bilinear value noise in [-1, 1]. */
    static double value2(long seed, double x, double z) {
        int xi = (int) Math.floor(x);
        int zi = (int) Math.floor(z);
        double fx = smooth(x - xi);
        double fz = smooth(z - zi);
        double a = unit(seed, xi, 0, zi);
        double b = unit(seed, xi + 1, 0, zi);
        double c = unit(seed, xi, 0, zi + 1);
        double d = unit(seed, xi + 1, 0, zi + 1);
        return fade(a, b, c, d, fx, fz) * 2.0 - 1.0;
    }

    /** Trilinear value noise in [-1, 1]; used to make asteroids lumpy instead of spheres. */
    static double value3(long seed, double x, double y, double z) {
        int xi = (int) Math.floor(x);
        int yi = (int) Math.floor(y);
        int zi = (int) Math.floor(z);
        double fx = smooth(x - xi);
        double fy = smooth(y - yi);
        double fz = smooth(z - zi);
        double a = unit(seed, xi, yi, zi);
        double b = unit(seed, xi + 1, yi, zi);
        double c = unit(seed, xi, yi, zi + 1);
        double d = unit(seed, xi + 1, yi, zi + 1);
        double e = unit(seed, xi, yi + 1, zi);
        double f = unit(seed, xi + 1, yi + 1, zi);
        double g = unit(seed, xi, yi + 1, zi + 1);
        double h = unit(seed, xi + 1, yi + 1, zi + 1);
        double bottom = fade(a, b, c, d, fx, fz);
        double top = fade(e, f, g, h, fx, fz);
        return lerp(bottom, top, fy) * 2.0 - 1.0;
    }

    /** Fractal sum of {@link #value2}, in [-1, 1]. */
    static double fbm2(long seed, double x, double z, int octaves, double lacunarity, double gain) {
        double sum = 0.0;
        double amplitude = 1.0;
        double norm = 0.0;
        double frequency = 1.0;
        for (int i = 0; i < octaves; i++) {
            sum += value2(seed + i * 0x632BE59BD9B4E019L, x * frequency, z * frequency) * amplitude;
            norm += amplitude;
            amplitude *= gain;
            frequency *= lacunarity;
        }
        return norm == 0.0 ? 0.0 : sum / norm;
    }

    /**
     * Ridged fractal in [0, 1]: crests where the noise crosses zero. This is what makes crater rims
     * and wrinkle ridges read as lines instead of as a smooth wave.
     */
    static double ridged2(long seed, double x, double z, int octaves, double lacunarity, double gain) {
        double sum = 0.0;
        double amplitude = 1.0;
        double norm = 0.0;
        double frequency = 1.0;
        for (int i = 0; i < octaves; i++) {
            double n = 1.0 - Math.abs(value2(seed + i * 0x632BE59BD9B4E019L, x * frequency, z * frequency));
            sum += n * n * amplitude;
            norm += amplitude;
            amplitude *= gain;
            frequency *= lacunarity;
        }
        return norm == 0.0 ? 0.0 : sum / norm;
    }
}
