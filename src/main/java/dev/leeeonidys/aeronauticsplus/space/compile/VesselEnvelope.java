package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.CellOccupancy;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;

/**
 * Axis-aligned envelope of a planted vessel in metres.
 * One cell is storage, not the rocket diameter: a 3-cell-wide stack is ~3 m.
 */
public record VesselEnvelope(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    public VesselEnvelope {
        if (!(maxX >= minX) || !(maxY >= minY) || !(maxZ >= minZ)
                || !Double.isFinite(minX) || !Double.isFinite(minY) || !Double.isFinite(minZ)
                || !Double.isFinite(maxX) || !Double.isFinite(maxY) || !Double.isFinite(maxZ)) {
            throw new IllegalArgumentException("Envelope bounds are invalid");
        }
    }

    public static VesselEnvelope of(VesselBlockGrid grid) {
        if (grid == null || grid.isEmpty()) {
            throw new IllegalArgumentException("Envelope requires planted parts");
        }
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        for (VesselBlockOccupant occupant : grid.occupants()) {
            CellOccupancy occupancy = occupant.spec().occupancy();
            Vector3d origin = occupancy.origin();
            Vector3d size = occupancy.size();
            double x0 = occupant.pos().x() + origin.x();
            double y0 = occupant.pos().y() + origin.y();
            double z0 = occupant.pos().z() + origin.z();
            minX = Math.min(minX, x0);
            minY = Math.min(minY, y0);
            minZ = Math.min(minZ, z0);
            maxX = Math.max(maxX, x0 + size.x());
            maxY = Math.max(maxY, y0 + size.y());
            maxZ = Math.max(maxZ, z0 + size.z());
        }
        return new VesselEnvelope(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public double width() {
        return maxX - minX;
    }

    public double height() {
        return maxY - minY;
    }

    public double depth() {
        return maxZ - minZ;
    }

    /** Planform size of the bounding square. A 3-cell-wide stack is about 3 m. */
    public double diameter() {
        return Math.max(width(), depth());
    }
}
