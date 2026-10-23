package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Orientation of the vessel body frame in inertial space.
 * There is no GUI: callers set attitude through this API.
 */
public record Attitude(Quaternion bodyToInertial) {
    public static final Attitude IDENTITY = new Attitude(Quaternion.IDENTITY);

    public Attitude {
        if (bodyToInertial == null) {
            throw new IllegalArgumentException("Attitude quaternion must not be null");
        }
        bodyToInertial = bodyToInertial.normalized();
    }

    public Vector3d toInertial(Vector3d bodyVector) {
        return bodyToInertial.rotate(bodyVector);
    }

    public Vector3d toBody(Vector3d inertialVector) {
        return bodyToInertial.conjugate().rotate(inertialVector);
    }

    /** Compose a rotation about a body-frame axis. */
    public Attitude rotateBody(Vector3d bodyAxis, double radians) {
        return new Attitude(bodyToInertial.multiply(Quaternion.fromAxisAngle(bodyAxis, radians)));
    }

    /** Integrate a constant body-frame angular rate over {@code seconds}. */
    public Attitude integrateBodyRate(Vector3d omegaBody, double seconds) {
        if (omegaBody == null) {
            throw new IllegalArgumentException("Angular velocity must not be null");
        }
        if (!(seconds >= 0.0) || !Double.isFinite(seconds)) {
            throw new IllegalArgumentException("Integration duration must be finite and non-negative");
        }
        double rate = omegaBody.magnitude();
        if (!(rate > 1.0e-12) || seconds == 0.0) {
            return this;
        }
        return rotateBody(omegaBody, rate * seconds);
    }

    /** Rotate so that {@code bodyAxis} points along {@code inertialDirection}. */
    public static Attitude pointing(Vector3d bodyAxis, Vector3d inertialDirection) {
        return new Attitude(Quaternion.rotateFromTo(bodyAxis, inertialDirection));
    }

    public Vector3d bodyX() {
        return toInertial(Vector3d.UNIT_X);
    }

    public Vector3d bodyY() {
        return toInertial(Vector3d.UNIT_Y);
    }

    public Vector3d bodyZ() {
        return toInertial(Vector3d.UNIT_Z);
    }
}
