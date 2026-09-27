package dev.leeeonidys.aeronauticsplus.content.propeller;

import net.minecraft.world.level.material.MapColor;

/**
 * Balance constants for Aeronautics Plus aircraft propellers.
 *
 * The numbers are intentionally conservative first-pass values: the important
 * milestone is that each part is a real Create Aeronautics propeller block
 * entity and participates in the same Sable/CA thrust pipeline as the stock
 * parts. Exact thrust/airflow/radius will be tuned in a test world.
 */
public record PropellerSpec(
        String id,
        Material material,
        int blades,
        double thrust,
        double airflow,
        float radius,
        float hardness,
        float resistance,
        MapColor mapColor
) {
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

    public static PropellerSpec wooden(String id, int blades, double thrust, double airflow, float radius) {
        return new PropellerSpec(id, Material.WOOD, blades, thrust, airflow, radius, 1.5f, 3.0f, MapColor.WOOD);
    }

    public static PropellerSpec aluminum(String id, int blades, double thrust, double airflow, float radius) {
        return new PropellerSpec(id, Material.ALUMINUM, blades, thrust, airflow, radius, 2.0f, 6.0f, MapColor.METAL);
    }

    public static PropellerSpec steel(String id, int blades, double thrust, double airflow, float radius) {
        return new PropellerSpec(id, Material.STEEL, blades, thrust, airflow, radius, 3.0f, 9.0f, MapColor.METAL);
    }

    public String englishName() {
        return material.englishName() + " " + blades + "-Blade Aircraft Propeller";
    }

    public String russianName() {
        return material.russianName() + " " + blades + "-лопастной авиационный винт";
    }
}
