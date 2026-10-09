package dev.leeeonidys.aeronauticsplus.space.compile;

/** Physical part placed as a Minecraft block. */
public enum VesselPartKind {
    STRUCTURE,
    TANK,
    ENGINE,
    SEPARATOR,
    /** Fixture on a Create train. The train plus this block is the transporter. */
    MOUNT,
    /** Ogive covering the payload through dense atmosphere. */
    FAIRING,
    /** Orbital apparatus under the fairing. */
    PAYLOAD,
    /** Pressurized living / working module (Soyuz бытовой отсек). */
    HABITAT,
    /** Photovoltaic wing. */
    SOLAR,
    /** Control-moment gyro / гиродин. */
    GYRO,
    /** Reaction-control thruster. Not a main engine. */
    RCS,
    /** Androgynous capture ring (APAS / IDSS silhouette). */
    DOCKING,
    /** Eclipse energy store. Solar does not work in shadow. */
    BATTERY,
    /** Vacuum heat rejection. There is no convection off-atmosphere. */
    RADIATOR,
    /** Radio: omni or high-gain. */
    ANTENNA,
    /** Pressurized station laboratory / experiment rack. */
    LAB
}
