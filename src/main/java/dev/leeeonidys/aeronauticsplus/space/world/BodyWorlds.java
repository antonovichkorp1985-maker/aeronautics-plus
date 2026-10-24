package dev.leeeonidys.aeronauticsplus.space.world;

import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Which catalogue bodies have a walkable dimension. Tiny moons stay map dots.
 * Earth is the Overworld. Station pocket is orbit, not this list.
 * Ceres/Vesta/Pallas/Hygiea share the asteroid belt dimension.
 */
public final class BodyWorlds {
    private BodyWorlds() {
    }

    public static List<String> ids() {
        return List.of("earth", "moon", "ceres", "vesta", "pallas", "hygiea");
    }

    public static boolean hasWorld(String bodyId) {
        return ids().contains(bodyId);
    }

    public static ResourceKey<Level> worldOf(String bodyId) {
        if (bodyId == null || bodyId.isBlank()) {
            throw new IllegalArgumentException("Body id is required");
        }
        return switch (bodyId) {
            case "earth" -> Level.OVERWORLD;
            case "moon" -> MoonWorld.LEVEL;
            case "ceres", "vesta", "pallas", "hygiea" -> AsteroidWorld.LEVEL;
            default -> null;
        };
    }
}
