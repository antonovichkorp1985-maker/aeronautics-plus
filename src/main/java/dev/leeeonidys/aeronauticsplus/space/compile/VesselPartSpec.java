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
        if (kind == VesselPartKind.ENGINE) {
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
        return new VesselPartSpec(
                id, VesselPartKind.TANK, dryMassKg, capacityKg, contents, 0.0, 0.0, 0.0,
                contents.fuelId(), contents.oxidizerId(), CellOccupancy.COLUMN);
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

    /**
     * Ports exposed on {@code face}. {@code facing} is exhaust for an engine and the
     * upper-stage direction for a separator.
     */
    public Set<VesselConnection.ConnectionKind> ports(BlockFace face, BlockFace facing) {
        return switch (kind) {
            case STRUCTURE -> EnumSet.of(VesselConnection.ConnectionKind.STRUCTURAL);
            case TANK -> EnumSet.of(
                    VesselConnection.ConnectionKind.STRUCTURAL,
                    VesselConnection.ConnectionKind.FUEL,
                    VesselConnection.ConnectionKind.OXIDIZER);
            case ENGINE -> {
                if (face == facing) {
                    // Nozzle may sit on a decoupler; it is not a structural load path.
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
            case SEPARATOR -> face == facing
                    ? EnumSet.of(VesselConnection.ConnectionKind.SEPARATION)
                    : EnumSet.of(VesselConnection.ConnectionKind.STRUCTURAL);
        };
    }
}
