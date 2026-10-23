package dev.leeeonidys.aeronauticsplus.space.core;

/** Inertial aiming mode for a planned burn. Attitude is still identity. */
public enum BurnAim {
    ENGINE,
    PROGRADE,
    RETROGRADE,
    RADIAL_OUT,
    RADIAL_IN,
    NORMAL,
    ANTINORMAL
}
