package dev.leeeonidys.aeronauticsplus.compat;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitState;
import dev.leeeonidys.aeronauticsplus.space.core.PadLatitude;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.fml.ModList;

/**
 * Optional TFC climate read. No TFC class is imported — reflection only when {@code tfc} is loaded.
 * Without TFC the world pad is the equator; vanilla biomes are not latitude.
 */
public final class TfcLatitude {
    private TfcLatitude() {
    }

    public static boolean present() {
        return ModList.get().isLoaded(CompatibilityManager.TERRAFIRMACRAFT_MOD_ID);
    }

    public static double hemisphereScaleBlocks(Level level) {
        if (!present() || level == null) {
            return PadLatitude.DEFAULT_HEMISPHERE_SCALE_BLOCKS;
        }
        try {
            Class<?> climate = Class.forName("net.dries007.tfc.util.climate.Climate");
            Object model = climate.getMethod("get", LevelReader.class).invoke(null, level);
            if (model == null) {
                return PadLatitude.DEFAULT_HEMISPHERE_SCALE_BLOCKS;
            }
            Object scale = model.getClass().getMethod("hemisphereScale").invoke(model);
            if (scale instanceof Number number && number.doubleValue() > 0.0 && Double.isFinite(number.doubleValue())) {
                return number.doubleValue();
            }
        } catch (ReflectiveOperationException ignored) {
            // TFC present but this climate model has no scale; keep the documented default.
        }
        return PadLatitude.DEFAULT_HEMISPHERE_SCALE_BLOCKS;
    }

    public static OrbitState padOrbit(Level level, BlockPos origin) {
        if (origin == null) {
            throw new IllegalArgumentException("Pad origin is required");
        }
        if (!present()) {
            return SpaceBodies.rotatingPad();
        }
        double latitude = PadLatitude.latitudeRadians(origin.getZ(), hemisphereScaleBlocks(level));
        return SpaceBodies.padAtLatitude(latitude);
    }

    public static String describe(Level level, BlockPos origin) {
        if (origin == null) {
            return "";
        }
        if (!present()) {
            return "Широта 0° (нет TFC-климата), sidereal восток ~465 м/с.";
        }
        double scale = hemisphereScaleBlocks(level);
        double latitude = PadLatitude.latitudeRadians(origin.getZ(), scale);
        OrbitState pad = SpaceBodies.padAtLatitude(latitude);
        return String.format(
                java.util.Locale.ROOT,
                "Широта %+.1f° (TFC Z, экватор Z≈%+.0f). Sidereal восток %.0f м/с.",
                Math.toDegrees(latitude),
                scale,
                pad.horizontalSpeedMetersPerSecond());
    }
}
