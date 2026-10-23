package dev.leeeonidys.aeronauticsplus.space.core;

import dev.leeeonidys.aeronauticsplus.space.compile.SpaceBodies;

/**
 * Equator is the cheap pad. TFC Z is latitude. Not ChemMod world gas.
 */
public final class LatitudeSlice {
    private LatitudeSlice() {
    }

    public record Result(
            double equatorEast,
            double midEast,
            double poleEast,
            double spawnDegrees,
            double equatorDegrees) {
    }

    public static Result execute() {
        double scale = 10_000.0;
        double spawnDeg = PadLatitude.latitudeDegrees(0, scale);
        double equatorDeg = PadLatitude.latitudeDegrees(10_000, scale);
        double northPoleDeg = PadLatitude.latitudeDegrees(-10_000, scale);
        double southPoleDeg = PadLatitude.latitudeDegrees(30_000, scale);
        if (Math.abs(spawnDeg - 45.0) > 0.6) {
            throw new IllegalStateException("TFC spawn z=0 is 45°N, got " + spawnDeg);
        }
        if (Math.abs(equatorDeg) > 0.6) {
            throw new IllegalStateException("TFC z=+scale is the equator, got " + equatorDeg);
        }
        if (Math.abs(northPoleDeg - 90.0) > 0.6) {
            throw new IllegalStateException("TFC z=-scale is the north pole, got " + northPoleDeg);
        }
        if (Math.abs(southPoleDeg + 90.0) > 0.6) {
            throw new IllegalStateException("TFC z=3·scale is the south pole, got " + southPoleDeg);
        }

        OrbitState equator = SpaceBodies.padAtLatitude(0.0);
        OrbitState mid = SpaceBodies.padAtLatitude(Math.toRadians(45.0));
        OrbitState pole = SpaceBodies.padAtLatitude(Math.PI / 2.0);
        double equatorEast = equator.horizontalSpeedMetersPerSecond();
        double midEast = mid.horizontalSpeedMetersPerSecond();
        double poleEast = pole.horizontalSpeedMetersPerSecond();
        if (Math.abs(equatorEast - 465.0) > 10.0) {
            throw new IllegalStateException("Equator must keep ~465 m/s east, got " + equatorEast);
        }
        if (Math.abs(midEast - equatorEast * Math.cos(Math.toRadians(45.0))) > 5.0) {
            throw new IllegalStateException("45° must be ωR cos φ, got " + midEast);
        }
        if (poleEast > 1.0) {
            throw new IllegalStateException("Pole pad must sit still, east=" + poleEast);
        }
        if (!(equatorEast > midEast + 100.0)) {
            throw new IllegalStateException("Equator must beat 45°N, eq=" + equatorEast + " mid=" + midEast);
        }
        if (Math.abs(equator.altitudeMeters()) > 1.0 || Math.abs(pole.altitudeMeters()) > 1.0) {
            throw new IllegalStateException("Latitude pads stay on the surface");
        }
        return new Result(equatorEast, midEast, poleEast, spawnDeg, equatorDeg);
    }
}
