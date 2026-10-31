package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Read-only view of a rotating shaft: angular speed (rad/s) and torque (N·m).
 * Implemented by Create kinetic blocks (shaft adapters, gearboxes) so that
 * helicopter rotors and propellers can consume shaft power without depending
 * directly on Create's kinetic API.
 */
public interface ShaftPowerApi {
    /** Rotational speed about the shaft axis, radians per second. May be negative for reverse. */
    double shaftSpeedRadiansPerSecond();

    /** Shaft torque, newton-metres. Sign follows the rotation direction. */
    double shaftTorqueNewtonMetres();

    /** Mechanical power: speed × torque, watts. */
    default double shaftPowerWatts() {
        return shaftSpeedRadiansPerSecond() * shaftTorqueNewtonMetres();
    }

    /** True when the shaft is turning fast enough to produce useful thrust. */
    default boolean isSpinning() {
        return Math.abs(shaftSpeedRadiansPerSecond()) > 1.0;
    }
}
