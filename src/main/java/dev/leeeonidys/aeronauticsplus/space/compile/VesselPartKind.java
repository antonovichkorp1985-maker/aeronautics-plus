package dev.leeeonidys.aeronauticsplus.space.compile;

/** Physical part placed as a Minecraft block. */
public enum VesselPartKind {
    STRUCTURE,
    TANK,
    ENGINE,
    SEPARATOR,
    /** Fixture on a Create train. The train plus this block is the transporter. */
    MOUNT,
    /** Ground launch table with hold-down clamps. Not a flying part. */
    PAD,
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
    LAB,
    /** Cabin oxygen. Not LOX, not engine oxidizer. */
    OXYGEN,
    /** Radio transponder. The antenna is the aperture; this is the electronics. */
    TRANSPONDER,
    /** Optical telescope / Earth-observation tube. */
    TELESCOPE,
    /** Seat / console that marks the pile as a rocket. */
    CONTROLLER
}
