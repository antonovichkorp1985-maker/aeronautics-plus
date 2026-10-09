package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.PropellantState;
import java.util.List;

/**
 * Registered physical parts. Chemistry remains ChemMod's responsibility.
 * Kerolox placeholders: RP-1/LOX O/F ≈ 2.3 (F-1 ~2.27, Merlin ~2.34). Combined tank is a
 * common-bulkhead vessel (Centaur / S-II / S-IVB), not two fluids mixed in one volume.
 * 20 kN is 1 m-segment scale (~2 g on a tonne), not an F-1.
 */
public final class VesselPartCatalog {
    public static final String FUEL_ID = "rp1";
    public static final String OXIDIZER_ID = "lox";
    /** Oxidizer/fuel by mass for RP-1/LOX. */
    public static final double KEROLX_MIXTURE = 2.3;

    public static final VesselPartSpec STRUCTURE = VesselPartSpec.structure("rocket_structure", 120.0);
    public static final VesselPartSpec TANK = VesselPartSpec.tank(
            "rocket_tank", 80.0, 1_000.0, new PropellantState(FUEL_ID, OXIDIZER_ID, 200.0, 460.0));
    /** RP-1 barrel. Does not carry LOX. */
    public static final VesselPartSpec FUEL_TANK = VesselPartSpec.tank(
            "rocket_fuel_tank", 70.0, 500.0, new PropellantState(FUEL_ID, OXIDIZER_ID, 400.0, 0.0));
    /** LOX barrel. Does not carry RP-1. */
    public static final VesselPartSpec OXIDIZER_TANK = VesselPartSpec.tank(
            "rocket_oxidizer_tank", 75.0, 1_000.0, new PropellantState(FUEL_ID, OXIDIZER_ID, 0.0, 920.0));
    public static final VesselPartSpec ENGINE = VesselPartSpec.engine(
            "rocket_engine", 90.0, 20_000.0, 300.0, FUEL_ID, OXIDIZER_ID, KEROLX_MIXTURE);
    public static final VesselPartSpec SEPARATOR = VesselPartSpec.separator("stage_separator", 40.0);
    public static final VesselPartSpec MOUNT = VesselPartSpec.mount("rocket_mount", 160.0);
    public static final VesselPartSpec FAIRING = VesselPartSpec.fairing("rocket_fairing", 45.0);
    public static final VesselPartSpec PAYLOAD = VesselPartSpec.payload("rocket_payload", 180.0);
    public static final VesselPartSpec HABITAT = VesselPartSpec.habitat("crew_habitat", 250.0);
    public static final VesselPartSpec SOLAR = VesselPartSpec.solar("solar_panel", 25.0);
    public static final VesselPartSpec GYRO = VesselPartSpec.gyro("control_gyro", 40.0);
    public static final VesselPartSpec RCS = VesselPartSpec.rcs(
            "rcs_thruster", 15.0, 400.0, 220.0, FUEL_ID, OXIDIZER_ID, KEROLX_MIXTURE);
    /** APAS/IDSS-class ring, 1 m segment. */
    public static final VesselPartSpec DOCKING = VesselPartSpec.docking("docking_port", 80.0);
    /** Eclipse store. Solar does not work in shadow. */
    public static final VesselPartSpec BATTERY = VesselPartSpec.battery("battery_pack", 55.0);
    /** ISS-style deployable panel. Vacuum has no convection. */
    public static final VesselPartSpec RADIATOR = VesselPartSpec.radiator("heat_radiator", 35.0);

    private VesselPartCatalog() {
    }

    public static List<VesselPartSpec> all() {
        return List.of(
                STRUCTURE, TANK, FUEL_TANK, OXIDIZER_TANK, ENGINE, SEPARATOR, MOUNT, FAIRING, PAYLOAD,
                HABITAT, SOLAR, GYRO, RCS, DOCKING, BATTERY, RADIATOR);
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
