package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;

/** Axis-aligned face of a one-metre vessel block. Independent of Minecraft. */
public enum BlockFace {
    DOWN(0, -1, 0),
    UP(0, 1, 0),
    NORTH(0, 0, -1),
    SOUTH(0, 0, 1),
    WEST(-1, 0, 0),
    EAST(1, 0, 0);

    private final int dx;
    private final int dy;
    private final int dz;

    BlockFace(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    public int dz() {
        return dz;
    }

    public BlockFace opposite() {
        return switch (this) {
            case DOWN -> UP;
            case UP -> DOWN;
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
        };
    }

    public Vector3d vector() {
        return new Vector3d(dx, dy, dz);
    }

    public static BlockFace fromDelta(int dx, int dy, int dz) {
        for (BlockFace face : values()) {
            if (face.dx == dx && face.dy == dy && face.dz == dz) {
                return face;
            }
        }
        throw new IllegalArgumentException("Not an axis-aligned unit delta: " + dx + "," + dy + "," + dz);
    }
}
