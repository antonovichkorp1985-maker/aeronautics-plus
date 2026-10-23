package dev.leeeonidys.aeronauticsplus.content.propeller;

import net.minecraft.world.level.material.MapColor;

/**
 * Balance constants for Aeronautics Plus aircraft propellers.
 *
 * Thrust and airflow values are multipliers per live Create shaft RPM, matching
 * the contract of Create Aeronautics' BasePropellerBlockEntity. They are kept
 * near the stock propeller defaults (1.0 thrust and 0.1 airflow) so Sable's
 * acceleration limit is not saturated at every non-zero kinetic speed.
 */
public record PropellerSpec(
        String id,
        Material material,
        int blades,
        double thrustPerRpm,
        double airflowPerRpm,
        float radius,
        float hardness,
        float resistance,
        MapColor mapColor
) {
    public PropellerSpec {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Propeller id must not be blank");
        }
        if (blades < 2 || blades > 4) {
            throw new IllegalArgumentException("Supported blade count is 2 through 4");
        }
        // Create Aeronautics multiplies both values by the live shaft RPM.
        // Bounds catch accidental use of final-force values (the old 95..215 bug).
        if (!(thrustPerRpm > 0.0 && thrustPerRpm <= 4.0)) {
            throw new IllegalArgumentException("Thrust-per-RPM must be in (0, 4]");
        }
        if (!(airflowPerRpm > 0.0 && airflowPerRpm <= 1.0)) {
            throw new IllegalArgumentException("Airflow-per-RPM must be in (0, 1]");
        }
        if (radius <= 0.0f) {
            throw new IllegalArgumentException("Propeller radius must be positive");
        }
    }

    public enum Material {
        WOOD("Wooden", "Деревянный"),
        ALUMINUM("Aluminum", "Алюминиевый"),
        STEEL("Steel", "Стальной");

        private final String englishName;
        private final String russianName;

        Material(String englishName, String russianName) {
            this.englishName = englishName;
            this.russianName = russianName;
        }

        public String englishName() {
            return englishName;
        }

        public String russianName() {
            return russianName;
        }
    }

    public static PropellerSpec wooden(
            String id, int blades, double thrustPerRpm, double airflowPerRpm, float radius
    ) {
        return new PropellerSpec(
                id, Material.WOOD, blades, thrustPerRpm, airflowPerRpm, radius,
                1.5f, 3.0f, MapColor.WOOD
        );
    }

    public static PropellerSpec aluminum(
            String id, int blades, double thrustPerRpm, double airflowPerRpm, float radius
    ) {
        return new PropellerSpec(
                id, Material.ALUMINUM, blades, thrustPerRpm, airflowPerRpm, radius,
                2.0f, 6.0f, MapColor.METAL
        );
    }

    public static PropellerSpec steel(
            String id, int blades, double thrustPerRpm, double airflowPerRpm, float radius
    ) {
        return new PropellerSpec(
                id, Material.STEEL, blades, thrustPerRpm, airflowPerRpm, radius,
                3.0f, 9.0f, MapColor.METAL
        );
    }

    public String englishName() {
        return material.englishName() + " " + blades + "-Blade Aircraft Propeller";
    }

    public String russianName() {
        return material.russianName() + " " + blades + "-лопастной авиационный винт";
    }
}
