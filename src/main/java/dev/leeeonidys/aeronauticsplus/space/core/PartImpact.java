package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartKind;

/**
 * Local impact model for a block vessel hitting rock.
 *
 * <p>A hit is judged by <em>where</em> it lands and by the closing speed along that
 * contact normal, not by the ship's total speed. A solar wing clipped edge-on at
 * 12 m/s shears off; the same 12 m/s against a 120 kg structural cell only dents it.
 * Glancing contacts rub and add a little abrasion; a head-on contact spends the
 * full closing speed on the part.
 *
 * <p>Numbers are Minecraft-world speed (blocks per second), not orbital velocity in
 * the Kepler map. Chemistry stays with ChemMod; this is only hull response.
 */
public final class PartImpact {
    private PartImpact() {
    }

    /** What stops working when this kind of cell is destroyed. */
    public enum Consequence {
        /** Load path, hull skin, clamps. */
        STRUCTURE,
        /** Photovoltaic wing gone; the ship runs on batteries only. */
        POWER,
        /** Battery bank gone; eclipse power is gone. */
        STORAGE,
        /** Antenna or transponder gone; the ship is deaf. */
        COMMS,
        /** Pressure shell opened: habitat, lab or cabin O2 bottles. */
        PRESSURE,
        /** Propellant barrel opened: that load is lost. */
        PROPELLANT,
        /** Nozzle or turbopump wrecked: that engine is out. */
        THRUST,
        /** Seat and console smashed: nobody can fly it. */
        CONTROL,
        /** Gyro or RCS jet gone: attitude authority reduced. */
        ATTITUDE,
        /** Radiator punctured: heat has nowhere to go. */
        THERMAL,
        /** Fairing panel gone: the payload is exposed to the airstream. */
        AERO,
        /** Telescope or instrument tube gone. */
        SENSOR,
        /** Capture ring deformed: this port no longer docks. */
        DOCKING,
        /** Aerodynamic surface (wing or fin cell) torn off its root. */
        SURFACE,
        /** Payload bus hit. */
        CARGO
    }

    /**
     * Closing speed, metres per second, that destroys an undamaged cell in one hit.
     * Thin shells and masts are fragile; structural cells and the pad table are not.
     */
    public static double toughnessMetersPerSecond(VesselPartKind kind) {
        return switch (kind) {
            case STRUCTURE -> 42.0;
            case PAD -> 75.0;
            case MOUNT -> 45.0;
            case SEPARATOR -> 30.0;
            case TANK -> 26.0;
            case ENGINE -> 30.0;
            case HABITAT, LAB -> 18.0;
            /** Cabin O2 bottles are thin-walled. */
            case OXYGEN -> 14.0;
            case PAYLOAD -> 22.0;
            /** Ogive shell over the payload; thin, but it is meant to be hit first. */
            case FAIRING -> 9.0;
            /** Photovoltaic wing. Fragile, and weakest when hit edge-on. */
            case SOLAR -> 7.0;
            case RADIATOR -> 8.0;
            case ANTENNA -> 5.0;
            case BATTERY, GYRO -> 13.0;
            case CONTROLLER -> 15.0;
            /** Wing / fin cell: a plate on a root, not a barrel. */
            case WING -> 8.0;
            case DOCKING -> 20.0;
            case LEGS -> 18.0;
            case TELESCOPE -> 11.0;
            case RCS -> 20.0;
            case TRANSPONDER -> 6.0;
        };
    }

    public static Consequence consequence(VesselPartKind kind) {
        return switch (kind) {
            case STRUCTURE, PAD, MOUNT, SEPARATOR, LEGS -> Consequence.STRUCTURE;
            case TANK -> Consequence.PROPELLANT;
            case ENGINE -> Consequence.THRUST;
            case HABITAT, LAB, OXYGEN -> Consequence.PRESSURE;
            case PAYLOAD -> Consequence.CARGO;
            case FAIRING -> Consequence.AERO;
            case SOLAR -> Consequence.POWER;
            case RADIATOR -> Consequence.THERMAL;
            case ANTENNA, TRANSPONDER -> Consequence.COMMS;
            case BATTERY -> Consequence.STORAGE;
            case GYRO, RCS -> Consequence.ATTITUDE;
            case CONTROLLER -> Consequence.CONTROL;
            case DOCKING -> Consequence.DOCKING;
            case TELESCOPE -> Consequence.SENSOR;
            case WING -> Consequence.SURFACE;
        };
    }

    /**
     * Thin plates care about the direction of the hit. A wing hit broadside spreads the
     * load over its face; the same wing hit on the edge is sheared off its mount.
     * {@code normalDotFacing} is 1.0 for a face-on hit along the panel normal, 0.0 for
     * a hit sliding in the plane of the panel.
     */
    public static double orientationFactor(VesselPartKind kind, double normalDotFacing) {
        return switch (kind) {
            case SOLAR, RADIATOR, FAIRING, WING -> normalDotFacing >= 0.7 ? 0.45 : 1.25;
            default -> 1.0;
        };
    }

    /**
     * Damage this single contact adds to the cell. 1.0 destroys an undamaged cell.
     *
     * <p>Scaling is quadratic in speed, not linear: the destructive term of a hard impact
     * is stagnation pressure {@code ½ρv²} (Hugoniot / hydrodynamic penetration), so a
     * 2× faster contact carries 4× the load. That is why a low-speed nudge dents and a
     * fast clip tears a wing off. Glancing contacts still scrape, with shear damped by
     * a factor of five.
     */
    public static double load(
            VesselPartKind kind,
            double closingMetersPerSecond,
            double shearMetersPerSecond,
            double normalDotFacing) {
        double toughness = toughnessMetersPerSecond(kind);
        double closing = Math.max(0.0, closingMetersPerSecond);
        double shear = Math.max(0.0, shearMetersPerSecond);
        double normalLoad = square(closing / toughness) * orientationFactor(kind, normalDotFacing);
        double abrasion = square(shear / (toughness * 5.0));
        return normalLoad + abrasion;
    }

    private static double square(double value) {
        return value * value;
    }

    /**
     * A hit that does not destroy a cell but is worth telling the pilot about:
     * roughly half the destruction speed for that part. Below this it is a scrape.
     */
    public static boolean penetration(double load) {
        return load >= 0.25;
    }
}
