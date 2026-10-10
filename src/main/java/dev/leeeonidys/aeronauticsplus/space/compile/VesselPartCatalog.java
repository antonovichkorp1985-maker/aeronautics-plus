package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.CellOccupancy;
import dev.leeeonidys.aeronauticsplus.space.core.PropellantState;
import java.util.List;

/**
 * Compiler fixtures. Minecraft items from this catalog are the engine, control
 * seat, train mount, launch pad and pyro ring. Tanks stay ChemMod (adapt those). Hull SKUs are not AP items.
 */
public final class VesselPartCatalog {
    public static final String FUEL_ID = "rp1";
    public static final String OXIDIZER_ID = "lox";
    public static final String HYDROGEN_ID = "lh2";
    /** Oxidizer/fuel by mass for RP-1/LOX. */
    public static final double KEROLX_MIXTURE = 2.3;
    /** RL10 / SSME class. ChemMod remains the chemistry source. */
    public static final double HYDROLOX_MIXTURE = 6.0;

    public static final VesselPartSpec STRUCTURE = VesselPartSpec.structure("rocket_structure", 120.0);
    public static final VesselPartSpec TANK = VesselPartSpec.tank(
            "rocket_tank", 80.0, 1_000.0, new PropellantState(FUEL_ID, OXIDIZER_ID, 200.0, 460.0));
    /** RP-1 barrel. Does not carry LOX. */
    public static final VesselPartSpec FUEL_TANK = VesselPartSpec.tank(
            "rocket_fuel_tank", 70.0, 500.0, new PropellantState(FUEL_ID, OXIDIZER_ID, 400.0, 0.0));
    public static final VesselPartSpec FUEL_TANK_SMALL = VesselPartSpec.tank(
            "rocket_fuel_tank_small", 35.0, 180.0,
            new PropellantState(FUEL_ID, OXIDIZER_ID, 150.0, 0.0), CellOccupancy.SLIM);
    public static final VesselPartSpec FUEL_TANK_LARGE = VesselPartSpec.tank(
            "rocket_fuel_tank_large", 140.0, 1_000.0,
            new PropellantState(FUEL_ID, OXIDIZER_ID, 850.0, 0.0), CellOccupancy.WIDE);
    /** LOX barrel. Does not carry RP-1. */
    public static final VesselPartSpec OXIDIZER_TANK = VesselPartSpec.tank(
            "rocket_oxidizer_tank", 75.0, 1_000.0, new PropellantState(FUEL_ID, OXIDIZER_ID, 0.0, 920.0));
    public static final VesselPartSpec OXIDIZER_TANK_SMALL = VesselPartSpec.tank(
            "rocket_oxidizer_tank_small", 40.0, 400.0,
            new PropellantState(FUEL_ID, OXIDIZER_ID, 0.0, 345.0), CellOccupancy.SLIM);
    public static final VesselPartSpec OXIDIZER_TANK_LARGE = VesselPartSpec.tank(
            "rocket_oxidizer_tank_large", 150.0, 2_000.0,
            new PropellantState(FUEL_ID, OXIDIZER_ID, 0.0, 1_955.0), CellOccupancy.WIDE);
    /**
     * LH2 barrel. Density ~70 kg/m³, so a 1 m cell holds tens of kilograms, not tonnes.
     * Same LOX tank still feeds a hydrolox engine.
     */
    public static final VesselPartSpec LH2_TANK = VesselPartSpec.tank(
            "lh2_tank", 45.0, 80.0, new PropellantState(HYDROGEN_ID, OXIDIZER_ID, 55.0, 0.0),
            CellOccupancy.WIDE);
    public static final VesselPartSpec ENGINE = VesselPartSpec.engine(
            "rocket_engine", 90.0, 20_000.0, 300.0, FUEL_ID, OXIDIZER_ID, KEROLX_MIXTURE);
    /** Vacuum hydrolox, RL10 class at 1 m-segment scale. */
    public static final VesselPartSpec HYDROLOX_ENGINE = VesselPartSpec.engine(
            "hydrolox_engine", 40.0, 8_000.0, 450.0, HYDROGEN_ID, OXIDIZER_ID, HYDROLOX_MIXTURE);
    public static final VesselPartSpec SEPARATOR = VesselPartSpec.separator("stage_separator", 40.0);
    public static final VesselPartSpec MOUNT = VesselPartSpec.mount("rocket_mount", 160.0);
    /** Ground table. Not ChemMod, not a flying SKU. */
    public static final VesselPartSpec PAD = VesselPartSpec.pad("launch_pad", 420.0);
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
    public static final VesselPartSpec OMNI_ANTENNA = VesselPartSpec.antenna(
            "omni_antenna", 8.0, CellOccupancy.MAST);
    public static final VesselPartSpec HIGH_GAIN_ANTENNA = VesselPartSpec.antenna(
            "high_gain_antenna", 22.0, CellOccupancy.DISH);
    public static final VesselPartSpec LAB = VesselPartSpec.lab("research_lab", 220.0);
    /** Cabin O2. Not LOX. */
    public static final VesselPartSpec OXYGEN = VesselPartSpec.oxygen("cabin_oxygen", 40.0);
    public static final VesselPartSpec TRANSPONDER = VesselPartSpec.transponder("radio_transponder", 12.0);
    public static final VesselPartSpec TELESCOPE = VesselPartSpec.telescope("space_telescope", 85.0);
    /** Marks the pile as a rocket. */
    public static final VesselPartSpec CONTROLLER = VesselPartSpec.controller("rocket_controller", 35.0);

    private VesselPartCatalog() {
    }

    public static List<VesselPartSpec> all() {
        return List.of(
                STRUCTURE, TANK, FUEL_TANK, FUEL_TANK_SMALL, FUEL_TANK_LARGE,
                OXIDIZER_TANK, OXIDIZER_TANK_SMALL, OXIDIZER_TANK_LARGE, LH2_TANK,
                ENGINE, HYDROLOX_ENGINE, SEPARATOR, MOUNT, PAD, FAIRING, PAYLOAD,
                HABITAT, SOLAR, GYRO, RCS, DOCKING, BATTERY, RADIATOR,
                OMNI_ANTENNA, HIGH_GAIN_ANTENNA, LAB, OXYGEN, TRANSPONDER, TELESCOPE, CONTROLLER);
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
