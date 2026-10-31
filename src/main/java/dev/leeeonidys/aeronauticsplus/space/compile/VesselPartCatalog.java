package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.CellOccupancy;
import dev.leeeonidys.aeronauticsplus.space.core.PropellantState;
import java.util.List;

/**
 * Compiler fixtures. Minecraft items from this catalog are the engine, control
 * seat, train mount, launch pad, pyro ring, landing legs, fairing,
 * RCS and docking. Habitat is a compiler fixture, not an AP item.
 * Life support is ChemMod; the seat marks players aboard.
 * Payload is a property of the stack, not an item.
 * Tanks, solar and radiator stay ChemMod. Hull SKUs are not AP items.
 */
public final class VesselPartCatalog {
    /** AP name for ChemMod kerosene-cut hydrocarbons. Not a tank block. */
    public static final String FUEL_ID = "rp1";
    public static final String KEROSENE_ID = "kerosene";
    public static final String OCTANE_ID = "octane";
    /** AP name for ChemMod O2. Not a LOX barrel item. */
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
    /** Turbopump / propellant feed unit (1 m segment). */
    public static final VesselPartSpec TURBO_PUMP = VesselPartSpec.engine(
            "turbopump_unit", 15.0, 5_000.0, 300.0, FUEL_ID, OXIDIZER_ID, KEROLX_MIXTURE);
    /** Combustion chamber (1 m segment). */
    public static final VesselPartSpec COMBUSTION_CHAMBER = VesselPartSpec.engine(
            "combustion_chamber", 25.0, 8_000.0, 320.0, FUEL_ID, OXIDIZER_ID, KEROLX_MIXTURE);
    /** Engine nozzle, atmospheric / sea-level. */
    public static final VesselPartSpec ENGINE_NOZZLE_SEA = VesselPartSpec.engine(
            "engine_nozzle_sea", 10.0, 4_000.0, 280.0, FUEL_ID, OXIDIZER_ID, KEROLX_MIXTURE);
    /** Engine nozzle, vacuum optimized. */
    public static final VesselPartSpec ENGINE_NOZZLE_VACUUM = VesselPartSpec.engine(
            "engine_nozzle_vacuum", 12.0, 2_500.0, 380.0, FUEL_ID, OXIDIZER_ID, HYDROLOX_MIXTURE);
    /** Gimbal / TVC mount for engine (1 m). */
    public static final VesselPartSpec GIMBAL_MOUNT = VesselPartSpec.mount("gimbal_mount", 20.0);
    /** Vacuum hydrolox, RL10 class at 1 m-segment scale. */
    public static final VesselPartSpec HYDROLOX_ENGINE = VesselPartSpec.engine(
            "hydrolox_engine", 40.0, 8_000.0, 450.0, HYDROGEN_ID, OXIDIZER_ID, HYDROLOX_MIXTURE);
    public static final VesselPartSpec SEPARATOR = VesselPartSpec.separator("stage_separator", 40.0);
    public static final VesselPartSpec MOUNT = VesselPartSpec.mount("rocket_mount", 160.0);
/** Ground table. Not ChemMod, not a flying SKU. */
    public static final VesselPartSpec PAD = VesselPartSpec.pad("launch_pad", 420.0);
    /** Launch-pad hold-down clamp (1 m segment). Holds rocket until T/W > 1. */
    public static final VesselPartSpec HOLD_DOWN_CLAMP = VesselPartSpec.holdDownClamp("hold_down_clamp", 15.0);
    /** Single-block rocket fairing (original). */
    public static final VesselPartSpec FAIRING = VesselPartSpec.fairing("rocket_fairing", 45.0);
    /** 1 m fairing panel section (cylindrical). */
    public static final VesselPartSpec FAIRING_PANEL = VesselPartSpec.fairingPanel("fairing_panel", 5.0);
    /** 1 m ogive fairing cone nose section. */
    public static final VesselPartSpec FAIRING_CONE = VesselPartSpec.fairingCone("fairing_cone", 8.0);
    /** Compiler satellite fixture. Not an AP item; payload is a property. */
    public static final VesselPartSpec PAYLOAD = VesselPartSpec.payload("rocket_payload", 180.0);
    /** Compiler fixture only. Not a registered AP block. */
    public static final VesselPartSpec HABITAT = VesselPartSpec.habitat("crew_habitat", 250.0);
    /** AP's own wing family — compiler fixture id replaced by registered ROCKET_WING block. */
    public static final VesselPartSpec WING = VesselPartSpec.wing("rocket_wing_fixture", 30.0);
    /** Root section that attaches to the fuselage. 45 kg. */
    public static final VesselPartSpec WING_ROOT = VesselPartSpec.wingRoot("wing_root", 45.0);
    /** Basic aerodynamic wing panel. 30 kg. */
    public static final VesselPartSpec ROCKET_WING = VesselPartSpec.rocketWing("rocket_wing", 30.0);
    /** Wing tip, reduces induced drag. 15 kg. */
    public static final VesselPartSpec WING_TIP = VesselPartSpec.wingTip("wing_tip", 15.0);
    /** Elevon / rudder control surface. 35 kg. */
    public static final VesselPartSpec WING_ELEVON = VesselPartSpec.wingElevon("wing_elevon", 35.0);
    /** Internal reinforced longeron within wing panel. 55 kg. */
    public static final VesselPartSpec WING_SPAR = VesselPartSpec.wingSpar("wing_spar", 55.0);
    public static final VesselPartSpec SOLAR = VesselPartSpec.solar("solar_panel", 25.0);
    public static final VesselPartSpec GYRO = VesselPartSpec.gyro("control_gyro", 40.0);
    public static final VesselPartSpec RCS = VesselPartSpec.rcs(
            "rcs_thruster", 15.0, 400.0, 220.0, FUEL_ID, OXIDIZER_ID, KEROLX_MIXTURE);
    /** APAS/IDSS-class ring, 1 m segment. */
    public static final VesselPartSpec DOCKING = VesselPartSpec.docking("docking_port", 80.0);
    /** Eclipse store. Solar does not work in shadow. */
    public static final VesselPartSpec BATTERY = VesselPartSpec.battery("battery_pack", 55.0);
    /** ChemMod thermal rejector. Not an AP item. */
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
    /** Falcon landing legs. Soft contact without them is NO_LEGS. */
    public static final VesselPartSpec LEGS = VesselPartSpec.legs("landing_legs", 25.0);

    private VesselPartCatalog() {
    }

public static List<VesselPartSpec> all() {
        return List.of(
                STRUCTURE, TANK, FUEL_TANK, FUEL_TANK_SMALL, FUEL_TANK_LARGE,
                OXIDIZER_TANK, OXIDIZER_TANK_SMALL, OXIDIZER_TANK_LARGE, LH2_TANK,
                ENGINE, TURBO_PUMP, COMBUSTION_CHAMBER, ENGINE_NOZZLE_SEA, ENGINE_NOZZLE_VACUUM, GIMBAL_MOUNT, HYDROLOX_ENGINE, SEPARATOR, MOUNT, PAD, FAIRING, FAIRING_PANEL, FAIRING_CONE, HOLD_DOWN_CLAMP, PAYLOAD,
                HABITAT, SOLAR, GYRO, RCS, DOCKING, BATTERY, RADIATOR,
                OMNI_ANTENNA, HIGH_GAIN_ANTENNA, LAB, OXYGEN, TRANSPONDER, TELESCOPE, CONTROLLER, LEGS, WING,
                WING_ROOT, ROCKET_WING, WING_TIP, WING_ELEVON, WING_SPAR);
    }

    public static VesselPartSpec byId(String id) {
        for (VesselPartSpec spec : all()) {
            if (spec.id().equals(id)) {
                return spec;
            }
        }
        throw new IllegalArgumentException("Unknown vessel part: " + id);
    }

    /** Dry mass for the default VesselMassProvider. */
    public static double massKg(String partId) {
        return byId(partId).massKg();
    }
}
