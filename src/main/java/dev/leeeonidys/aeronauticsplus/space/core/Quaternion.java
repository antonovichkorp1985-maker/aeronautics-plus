package dev.leeeonidys.aeronauticsplus.space.core;

/** Unit quaternion mapping body vectors into the inertial frame. */
public record Quaternion(double w, double x, double y, double z) {
    public static final Quaternion IDENTITY = new Quaternion(1.0, 0.0, 0.0, 0.0);

    public Quaternion {
        if (!Double.isFinite(w) || !Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Quaternion components must be finite");
        }
    }

    public static Quaternion fromAxisAngle(Vector3d axis, double radians) {
        Vector3d unit = axis.normalized();
        double half = 0.5 * radians;
        double s = Math.sin(half);
        return new Quaternion(Math.cos(half), unit.x() * s, unit.y() * s, unit.z() * s).normalized();
    }

    /** Shortest rotation that takes {@code from} onto {@code to}. */
    public static Quaternion rotateFromTo(Vector3d from, Vector3d to) {
        Vector3d a = from.normalized();
        Vector3d b = to.normalized();
        double dot = a.dot(b);
        if (dot > 1.0 - 1.0e-12) {
            return IDENTITY;
        }
        if (dot < -1.0 + 1.0e-12) {
            Vector3d orthogonal = Math.abs(a.x()) < 0.9 ? Vector3d.UNIT_X : Vector3d.UNIT_Y;
            return fromAxisAngle(a.cross(orthogonal), Math.PI);
        }
        Vector3d axis = a.cross(b);
        // q = (1 + dot, axis); then normalize. Equivalent to half-angle formula.
        return new Quaternion(1.0 + dot, axis.x(), axis.y(), axis.z()).normalized();
    }

    public Quaternion conjugate() {
        return new Quaternion(w, -x, -y, -z);
    }

    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
                w * other.w - x * other.x - y * other.y - z * other.z,
                w * other.x + x * other.w + y * other.z - z * other.y,
                w * other.y - x * other.z + y * other.w + z * other.x,
                w * other.z + x * other.y - y * other.x + z * other.w);
    }

    public Vector3d rotate(Vector3d vector) {
        Quaternion qv = new Quaternion(0.0, vector.x(), vector.y(), vector.z());
        Quaternion rotated = multiply(qv).multiply(conjugate());
        return new Vector3d(rotated.x, rotated.y, rotated.z);
    }

    public Quaternion normalized() {
        double length = Math.sqrt(w * w + x * x + y * y + z * z);
        if (!(length > 0.0) || !Double.isFinite(length)) {
            throw new IllegalStateException("Cannot normalize a zero quaternion");
        }
        return new Quaternion(w / length, x / length, y / length, z / length);
    }
}
