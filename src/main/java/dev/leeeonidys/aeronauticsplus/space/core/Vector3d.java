package dev.leeeonidys.aeronauticsplus.space.core;

/** Immutable 3D vector used by the dependency-free orbital core. */
public record Vector3d(double x, double y, double z) {
    public static final Vector3d ZERO = new Vector3d(0.0, 0.0, 0.0);
    public static final Vector3d UNIT_X = new Vector3d(1.0, 0.0, 0.0);
    public static final Vector3d UNIT_Y = new Vector3d(0.0, 1.0, 0.0);
    public static final Vector3d UNIT_Z = new Vector3d(0.0, 0.0, 1.0);

    public Vector3d {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Vector components must be finite");
        }
    }

    public Vector3d add(Vector3d other) {
        return new Vector3d(x + other.x, y + other.y, z + other.z);
    }

    public Vector3d subtract(Vector3d other) {
        return new Vector3d(x - other.x, y - other.y, z - other.z);
    }

    public Vector3d multiply(double scalar) {
        return new Vector3d(x * scalar, y * scalar, z * scalar);
    }

    public double dot(Vector3d other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3d cross(Vector3d other) {
        return new Vector3d(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x);
    }

    public double magnitudeSquared() {
        return dot(this);
    }

    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    public Vector3d normalized() {
        double length = magnitude();
        if (!(length > 0.0) || !Double.isFinite(length)) {
            throw new IllegalStateException("Cannot normalize a zero or non-finite vector");
        }
        return multiply(1.0 / length);
    }
}
