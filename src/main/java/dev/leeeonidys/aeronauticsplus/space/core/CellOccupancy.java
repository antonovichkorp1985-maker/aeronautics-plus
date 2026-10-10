package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Occupied volume inside a 1 m Minecraft cell. Parts are not required to fill the cube:
 * Chisels & Bits allows several small mechanisms in one block. This occupancy is the
 * Minecraft-free description of that volume; Chisels & Bits multi-state mapping comes later.
 */
public record CellOccupancy(Vector3d origin, Vector3d size) {
    /** Full 1 m cube. */
    public static final CellOccupancy FULL = fromPixels(0, 0, 0, 16, 16, 16);
    /** 12×16×12 px column used by tanks and structure. */
    public static final CellOccupancy COLUMN = fromPixels(2, 0, 2, 14, 16, 14);
    /** Narrower nozzle column; an engine is not a full block. */
    public static final CellOccupancy NOZZLE = fromPixels(4, 0, 4, 12, 16, 12);
    /** Thin separator ring. */
    public static final CellOccupancy RING = fromPixels(1, 5, 1, 15, 11, 15);
    /** Low cradle on a train car; not a full cube. */
    public static final CellOccupancy CRADLE = fromPixels(1, 0, 1, 15, 6, 15);
    /** Launch table: deck and clamps. Stays on the ground. */
    public static final CellOccupancy PAD = fromPixels(0, 0, 0, 16, 8, 16);
    /** Ogive payload fairing; not a full cube. */
    public static final CellOccupancy OGIVE = fromPixels(2, 0, 2, 14, 16, 14);
    /** Compact satellite bus under the fairing. */
    public static final CellOccupancy BUS = fromPixels(3, 2, 3, 13, 14, 13);
    /** Thin solar wing; not a full cube. */
    public static final CellOccupancy WING = fromPixels(0, 5, 3, 16, 11, 13);
    /** CMG can inside a cell. */
    public static final CellOccupancy CAN = fromPixels(4, 4, 4, 12, 12, 12);
    /** Small RCS pod. */
    public static final CellOccupancy POD = fromPixels(5, 2, 5, 11, 14, 11);
    /** Skinny upper-stage barrel. Still one 1 m cell. */
    public static final CellOccupancy SLIM = fromPixels(5, 0, 5, 11, 16, 11);
    /** Fat booster barrel. Still one 1 m cell, not a 3 m rocket. */
    public static final CellOccupancy WIDE = fromPixels(1, 0, 1, 15, 16, 15);
    /** Omni / whip antenna mast. */
    public static final CellOccupancy MAST = fromPixels(6, 0, 6, 10, 16, 10);
    /** High-gain dish. */
    public static final CellOccupancy DISH = fromPixels(2, 3, 2, 14, 13, 14);

    public CellOccupancy {
        if (origin == null || size == null) {
            throw new IllegalArgumentException("Occupancy origin and size are required");
        }
        if (origin.x() < -1.0e-9 || origin.y() < -1.0e-9 || origin.z() < -1.0e-9) {
            throw new IllegalArgumentException("Occupancy origin must lie inside the cell");
        }
        if (size.x() <= 0.0 || size.y() <= 0.0 || size.z() <= 0.0) {
            throw new IllegalArgumentException("Occupancy size must be positive");
        }
        if (origin.x() + size.x() > 1.0 + 1.0e-9
                || origin.y() + size.y() > 1.0 + 1.0e-9
                || origin.z() + size.z() > 1.0 + 1.0e-9) {
            throw new IllegalArgumentException("Occupancy must fit inside the 1 m cell");
        }
        // Compact constructors assign fields after this body, so do not call volume().
        if (size.x() * size.y() * size.z() > 1.0 + 1.0e-9) {
            throw new IllegalArgumentException("Occupancy volume cannot exceed one cubic metre");
        }
    }

    public static CellOccupancy fromPixels(int x0, int y0, int z0, int x1, int y1, int z1) {
        if (x1 <= x0 || y1 <= y0 || z1 <= z0) {
            throw new IllegalArgumentException("Pixel occupancy box is inverted");
        }
        return new CellOccupancy(
                new Vector3d(x0 / 16.0, y0 / 16.0, z0 / 16.0),
                new Vector3d((x1 - x0) / 16.0, (y1 - y0) / 16.0, (z1 - z0) / 16.0));
    }

    public double volume() {
        return size.x() * size.y() * size.z();
    }

    public Vector3d centroid() {
        return origin.add(size.multiply(0.5));
    }

    public boolean isFullBlock() {
        return volume() > 1.0 - 1.0e-9;
    }

    public Vector3d max() {
        return origin.add(size);
    }

    /** True when the open boxes overlap. Faces that only touch are allowed. */
    public boolean intersects(CellOccupancy other) {
        if (other == null) {
            throw new IllegalArgumentException("Occupancy to test is required");
        }
        Vector3d a1 = max();
        Vector3d b1 = other.max();
        return origin.x() < b1.x() - 1.0e-9 && other.origin.x() < a1.x() - 1.0e-9
                && origin.y() < b1.y() - 1.0e-9 && other.origin.y() < a1.y() - 1.0e-9
                && origin.z() < b1.z() - 1.0e-9 && other.origin.z() < a1.z() - 1.0e-9;
    }
}
