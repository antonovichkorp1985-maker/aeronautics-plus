package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.PropellantState;
import java.util.List;

/** Registered physical parts. Chemistry remains ChemMod's responsibility. */
public final class VesselPartCatalog {
    public static final String FUEL_ID = "rp1";
    public static final String OXIDIZER_ID = "lox";

    public static final VesselPartSpec STRUCTURE = VesselPartSpec.structure("rocket_structure", 120.0);
    public static final VesselPartSpec TANK = VesselPartSpec.tank(
            "rocket_tank", 80.0, 1_000.0, new PropellantState(FUEL_ID, OXIDIZER_ID, 200.0, 400.0));
    public static final VesselPartSpec ENGINE = VesselPartSpec.engine(
            "rocket_engine", 90.0, 20_000.0, 300.0, FUEL_ID, OXIDIZER_ID, 2.0);
    public static final VesselPartSpec SEPARATOR = VesselPartSpec.separator("stage_separator", 40.0);
    public static final VesselPartSpec MOUNT = VesselPartSpec.mount("rocket_mount", 160.0);

    private VesselPartCatalog() {
    }

    public static List<VesselPartSpec> all() {
        return List.of(STRUCTURE, TANK, ENGINE, SEPARATOR, MOUNT);
    }

    public static VesselPartSpec byId(String id) {
        for (VesselPartSpec spec : all()) {
            if (spec.id().equals(id)) {
                return spec;
            }
        }
        throw new IllegalArgumentException("Unknown vessel part: " + id);
    }
}
