package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.CellOccupancy;
import dev.leeeonidys.aeronauticsplus.space.core.PropellantState;
import dev.leeeonidys.aeronauticsplus.space.core.VesselConnection;
import java.util.EnumSet;
import java.util.Set;

/**
 * Catalog entry for a physical vessel block.
 * Mixture chemistry stays with ChemMod; these numbers are hull and engine placeholders.
 */
public record VesselPartSpec(
        String id,
        VesselPartKind kind,
        double massKg,
        double tankCapacityKg,
        PropellantState defaultPropellant,
        double thrustNewtons,
        double specificImpulseSeconds,
        double mixtureRatio,
        String fuelId,
        String oxidizerId,
        CellOccupancy occupancy) {
    public VesselPartSpec {
        if (id == null || id.isBlank() || kind == null) {
            throw new IllegalArgumentException("Vessel part spec identity is invalid");
        }
        if (massKg < 0.0 || !Double.isFinite(massKg)) {
            throw new IllegalArgumentException("Part mass must be finite and non-negative");
        }
        if (occupancy == null) {
            occupancy = CellOccupancy.FULL;
        }
        if (kind == VesselPartKind.TANK) {
            if (!(tankCapacityKg > 0.0) || defaultPropellant == null) {
                throw new IllegalArgumentException("Tank spec requires capacity and propellant");
            }
        }
        if (kind == VesselPartKind.ENGINE || kind == VesselPartKind.RCS) {
            if (!(thrustNewtons > 0.0) || !(specificImpulseSeconds > 0.0) || !(mixtureRatio > 0.0)
                    || fuelId == null || fuelId.isBlank()
                    || oxidizerId == null || oxidizerId.isBlank()) {
                throw new IllegalArgumentException("Engine spec requires thrust, Isp, mixture ratio and propellant ids");
            }
        }
    }

    public static VesselPartSpec structure(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.STRUCTURE, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.COLUMN);
    }

    public static VesselPartSpec tank(
            String id, double dryMassKg, double capacityKg, PropellantState contents) {
        return tank(id, dryMassKg, capacityKg, contents, CellOccupancy.COLUMN);
    }

    public static VesselPartSpec tank(
            String id, double dryMassKg, double capacityKg, PropellantState contents, CellOccupancy occupancy) {
        return new VesselPartSpec(
                id, VesselPartKind.TANK, dryMassKg, capacityKg, contents, 0.0, 0.0, 0.0,
                contents.fuelId(), contents.oxidizerId(), occupancy);
    }

    public static VesselPartSpec engine(
            String id, double dryMassKg, double thrustNewtons, double ispSeconds,
            String fuelId, String oxidizerId, double mixtureRatio) {
        return engine(id, dryMassKg, thrustNewtons, ispSeconds, fuelId, oxidizerId, mixtureRatio,
                CellOccupancy.NOZZLE);
    }

    public static VesselPartSpec engine(
            String id, double dryMassKg, double thrustNewtons, double ispSeconds,
            String fuelId, String oxidizerId, double mixtureRatio, CellOccupancy occupancy) {
        return new VesselPartSpec(
                id, VesselPartKind.ENGINE, dryMassKg, 0.0, null, thrustNewtons, ispSeconds, mixtureRatio,
                fuelId, oxidizerId, occupancy);
    }

    public static VesselPartSpec separator(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.SEPARATOR, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.RING);
    }

    public static VesselPartSpec mount(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.MOUNT, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.CRADLE);
    }

    public static VesselPartSpec pad(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.PAD, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.PAD);
    }

    public static VesselPartSpec fairing(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.FAIRING, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.OGIVE);
    }

    public static VesselPartSpec payload(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.PAYLOAD, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.BUS);
    }

    public static VesselPartSpec habitat(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.HABITAT, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.COLUMN);
    }

    public static VesselPartSpec solar(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.SOLAR, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.WING);
    }

    public static VesselPartSpec gyro(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.GYRO, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.CAN);
    }

    public static VesselPartSpec rcs(
            String id, double dryMassKg, double thrustNewtons, double ispSeconds,
            String fuelId, String oxidizerId, double mixtureRatio) {
        return new VesselPartSpec(
                id, VesselPartKind.RCS, dryMassKg, 0.0, null, thrustNewtons, ispSeconds, mixtureRatio,
                fuelId, oxidizerId, CellOccupancy.POD);
    }

    public static VesselPartSpec docking(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.DOCKING, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.RING);
    }

    public static VesselPartSpec battery(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.BATTERY, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.CAN);
    }

    public static VesselPartSpec radiator(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.RADIATOR, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.WING);
    }

    public static VesselPartSpec antenna(String id, double massKg, CellOccupancy occupancy) {
        return new VesselPartSpec(
                id, VesselPartKind.ANTENNA, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", occupancy);
    }

    public static VesselPartSpec lab(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.LAB, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.COLUMN);
    }

    /** Cabin O2 bottles. Not a LOX barrel. */
    public static VesselPartSpec oxygen(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.OXYGEN, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.CAN);
    }

    public static VesselPartSpec transponder(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.TRANSPONDER, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.CAN);
    }

    public static VesselPartSpec telescope(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.TELESCOPE, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.COLUMN);
    }

    /** Control seat. Placing it is what makes the ChemMod pile a rocket. */
    public static VesselPartSpec controller(String id, double massKg) {
        return new VesselPartSpec(
                id, VesselPartKind.CONTROLLER, massKg, 0.0, null, 0.0, 0.0, 0.0, "", "", CellOccupancy.CAN);
    }

    /**
     * Common-bulkhead tanks expose both feeds (Centaur / S-II). Dedicated RP-1 or LOX
     * tanks expose only that species — oxidizer does not travel through the fuel hull.
     */
    private Set<VesselConnection.ConnectionKind> tankPorts() {
        EnumSet<VesselConnection.ConnectionKind> ports = EnumSet.of(VesselConnection.ConnectionKind.STRUCTURAL);
        if (defaultPropellant != null) {
            if (defaultPropellant.fuelMassKg() > 0.0) {
                ports.add(VesselConnection.ConnectionKind.FUEL);
            }
            if (defaultPropellant.oxidizerMassKg() > 0.0) {
                ports.add(VesselConnection.ConnectionKind.OXIDIZER);
            }
        }
        return ports;
    }

    /**
     * Ports exposed on {@code face}. {@code facing} is exhaust for an engine and the
     * upper-stage direction for a separator.
     */
    public Set<VesselConnection.ConnectionKind> ports(BlockFace face, BlockFace facing) {
        return switch (kind) {
            case STRUCTURE, FAIRING -> EnumSet.of(VesselConnection.ConnectionKind.STRUCTURAL);
            case PAYLOAD -> EnumSet.of(
                    VesselConnection.ConnectionKind.STRUCTURAL,
                    VesselConnection.ConnectionKind.DATA);
            case HABITAT -> EnumSet.of(
                    VesselConnection.ConnectionKind.STRUCTURAL,
                    VesselConnection.ConnectionKind.THERMAL,
                    VesselConnection.ConnectionKind.DATA);
            case LAB -> EnumSet.of(
                    VesselConnection.ConnectionKind.STRUCTURAL,
                    VesselConnection.ConnectionKind.THERMAL,
                    VesselConnection.ConnectionKind.DATA);
            case ANTENNA, TELESCOPE, CONTROLLER -> EnumSet.of(
                    VesselConnection.ConnectionKind.STRUCTURAL,
                    VesselConnection.ConnectionKind.DATA);
            case TRANSPONDER -> EnumSet.of(
                    VesselConnection.ConnectionKind.STRUCTURAL,
                    VesselConnection.ConnectionKind.DATA,
                    VesselConnection.ConnectionKind.ELECTRIC);
            case OXYGEN -> EnumSet.of(VesselConnection.ConnectionKind.STRUCTURAL);
            case SOLAR -> EnumSet.of(
                    VesselConnection.ConnectionKind.STRUCTURAL,
                    VesselConnection.ConnectionKind.ELECTRIC);
            case GYRO, BATTERY -> EnumSet.of(
                    VesselConnection.ConnectionKind.STRUCTURAL,
                    VesselConnection.ConnectionKind.ELECTRIC,
                    VesselConnection.ConnectionKind.THERMAL);
            case RADIATOR -> EnumSet.of(
                    VesselConnection.ConnectionKind.STRUCTURAL,
                    VesselConnection.ConnectionKind.THERMAL);
            case TANK -> tankPorts();
            case ENGINE -> {
                if (face == facing) {
                    // Nozzle may sit on a decoupler; it is not a structural load path.
                    yield EnumSet.of(VesselConnection.ConnectionKind.SEPARATION);
                }
                // Sides and the tank-facing end carry feed so split RP-1 / LOX tanks
                // can sit beside the engine (Falcon / Soyuz cross-section), not through the hull.
                yield EnumSet.of(
                        VesselConnection.ConnectionKind.STRUCTURAL,
                        VesselConnection.ConnectionKind.FUEL,
                        VesselConnection.ConnectionKind.OXIDIZER);
            }
            case SEPARATOR -> face == facing
                    ? EnumSet.of(VesselConnection.ConnectionKind.SEPARATION)
                    : EnumSet.of(VesselConnection.ConnectionKind.STRUCTURAL);
            case MOUNT, PAD -> EnumSet.of(VesselConnection.ConnectionKind.STRUCTURAL);
            case RCS -> {
                if (face == facing) {
                    yield EnumSet.of(VesselConnection.ConnectionKind.SEPARATION);
                }
                if (face == facing.opposite()) {
                    yield EnumSet.of(
                            VesselConnection.ConnectionKind.STRUCTURAL,
                            VesselConnection.ConnectionKind.FUEL,
                            VesselConnection.ConnectionKind.OXIDIZER);
                }
                yield EnumSet.of(VesselConnection.ConnectionKind.STRUCTURAL);
            }
            case DOCKING -> {
                if (face == facing) {
                    // Capture ring. Two ports looking at each other share DOCKING, not hull.
                    yield EnumSet.of(VesselConnection.ConnectionKind.DOCKING);
                }
                yield EnumSet.of(VesselConnection.ConnectionKind.STRUCTURAL);
            }
        };
    }
}
