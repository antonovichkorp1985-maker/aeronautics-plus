package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * AP names for ChemMod substances. Not tank blocks. Hydrocarbons and O2 already
 * exist as graphs; these ids are what the engine feed reads.
 */
public final class PropellantIds {
    private PropellantIds() {
    }

    public static boolean hydrocarbonFuel(String id) {
        String key = key(id);
        return "rp1".equals(key)
                || "kerosene".equals(key)
                || "octane".equals(key)
                || "methane".equals(key)
                || "ch4".equals(key)
                || "propane".equals(key);
    }

    public static boolean hydrogenFuel(String id) {
        String key = key(id);
        return "lh2".equals(key) || "hydrogen".equals(key) || "h2".equals(key);
    }

    public static boolean lox(String id) {
        String key = key(id);
        return "lox".equals(key) || "oxygen".equals(key) || "o2".equals(key);
    }

    private static String key(String id) {
        return id == null ? "" : id.toLowerCase(java.util.Locale.ROOT).trim();
    }
}
