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
    PAYLOAD
}
