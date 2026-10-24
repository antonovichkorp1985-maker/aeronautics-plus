package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.world.MarsChunkGenerator;
import dev.leeeonidys.aeronauticsplus.space.world.MoonChunkGenerator;

/**
 * Height-field self-check for the hand-built bodies, run at startup like the other slices.
 *
 * <p>The previous terrain put every crater on a 48-block lattice with a {@code mod 7} shift, so
 * the same crater pattern came back every 336 blocks — which is exactly what the owner saw and
 * rejected. These checks sample the real generator functions, not a copy of the constants, and
 * refuse to boot if the relief ever flattens or starts repeating one old period away.</p>
 */
public final class TerrainSlice {
    private static final long SEED = 20261025L;
    /** The old period: craters sat on a 48-block lattice with a mod-7 jitter. */
    private static final int OLD_PERIOD = 48 * 7;
    /** Incommensurate with every lattice in play, so it is the control shift. */
    private static final int CONTROL_SHIFT = 517;

    private TerrainSlice() {
    }

    public record Result(
            int samples,
            double moonRelief,
            double marsRelief,
            double repeatRatio,
            double canyonDepth,
            double volcanoRise) {
    }

    public static Result execute() {
        double moonMin = Double.MAX_VALUE;
        double moonMax = -Double.MAX_VALUE;
        double marsMin = Double.MAX_VALUE;
        double marsMax = -Double.MAX_VALUE;
        double deltaOld = 0.0;
        double deltaControl = 0.0;
        int samples = 0;
        for (int x = -600; x <= 600; x += 7) {
            for (int z = -600; z <= 600; z += 11) {
                double moon = MoonChunkGenerator.height(SEED, x, z);
                double mars = MarsChunkGenerator.height(SEED, x, z);
                moonMin = Math.min(moonMin, moon);
                moonMax = Math.max(moonMax, moon);
                marsMin = Math.min(marsMin, mars);
                marsMax = Math.max(marsMax, mars);
                // A repeating field answers nearly the same height one old period away. Compare
                // that shift with a shift no lattice can share; if the two look alike the terrain
                // is just smooth, and if the old period looks flat the pattern repeats.
                deltaOld += Math.abs(MoonChunkGenerator.height(SEED, x + OLD_PERIOD, z) - moon);
                deltaControl += Math.abs(MoonChunkGenerator.height(SEED, x + CONTROL_SHIFT, z) - moon);
                samples++;
            }
        }
        double moonRelief = moonMax - moonMin;
        double marsRelief = marsMax - marsMin;
        if (moonRelief < 12.0) {
            throw new IllegalStateException("Lunar relief must exceed 12 blocks, got " + moonRelief);
        }
        if (moonRelief > 120.0) {
            throw new IllegalStateException("Lunar relief must stay walkable, got " + moonRelief);
        }
        if (marsRelief < 15.0) {
            throw new IllegalStateException("Mars relief must exceed 15 blocks, got " + marsRelief);
        }
        double meanOld = deltaOld / samples;
        double meanControl = deltaControl / samples;
        if (meanOld < 0.5) {
            throw new IllegalStateException(
                    "Terrain is flat: mean height change over a shift is " + meanOld + " blocks");
        }
        double repeatRatio = meanControl == 0.0 ? Double.POSITIVE_INFINITY : meanOld / meanControl;
        if (repeatRatio < 0.6) {
            throw new IllegalStateException(String.format(
                    java.util.Locale.ROOT,
                    "Terrain repeats every %d blocks: shift %.3f vs control %.3f (ratio %.2f)",
                    OLD_PERIOD, meanOld, meanControl, repeatRatio));
        }
        // The rift must be a real valley: sample its own centre line against ground 260 m away.
        double canyonDepth = 0.0;
        for (int x = -400; x <= 400; x += 50) {
            int zc = (int) Math.round(MarsChunkGenerator.canyonCentre(SEED, x));
            double floor = MarsChunkGenerator.height(SEED, x, zc);
            double plain = MarsChunkGenerator.height(SEED, x, zc + 260);
            canyonDepth = Math.max(canyonDepth, plain - floor);
        }
        if (canyonDepth < 10.0) {
            throw new IllegalStateException("Mars canyon must cut at least 10 blocks, got " + canyonDepth);
        }
        // One shield volcano per world: cone apex above the caldera floor, ground ring at zero.
        int vx = (int) Math.round(MarsChunkGenerator.volcanoCentreX(SEED));
        int vz = (int) Math.round(MarsChunkGenerator.volcanoCentreZ(SEED));
        double volcanoRise = MarsChunkGenerator.volcano(SEED, vx, vz)
                - MarsChunkGenerator.volcano(SEED, vx + 400, vz);
        if (volcanoRise < 30.0) {
            throw new IllegalStateException("Mars volcano must rise above 30 blocks, got " + volcanoRise);
        }
        return new Result(samples, moonRelief, marsRelief, repeatRatio, canyonDepth, volcanoRise);
    }
}
