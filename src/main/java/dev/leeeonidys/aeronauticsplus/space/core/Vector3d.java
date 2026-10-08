package dev.leeeonidys.aeronauticsplus.space.core;

/** Immutable 3D vector used by the dependency-free orbital core. */
public record Vector3d(double x, double y, double z) {
    public static final Vector3d ZERO = new Vector3d(0.0, 0.0, 0.0);

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
