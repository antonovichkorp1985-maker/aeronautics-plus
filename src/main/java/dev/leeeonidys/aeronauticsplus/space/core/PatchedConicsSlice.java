package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Earth–Moon SOI capture and escape. Not a Moon dimension.
 */
public final class PatchedConicsSlice {
    private PatchedConicsSlice() {
    }

    public record Result(double soiKm, boolean captured, boolean escaped, boolean parkedStays) {
    }

    public static Result execute() {
        double soi = PatchedConics.moonSphereOfInfluenceMeters();
        if (soi < 50_000_000.0 || soi > 80_000_000.0) {
            throw new IllegalStateException("Moon SOI should be ~66 000 km, soi=" + soi);
        }
        CelestialBody earth = SolarSystem.earth();
        CelestialBody moon = SolarSystem.moon();
        Vector3d moonR = PatchedConics.moonPositionInertial(0.0);
        Vector3d toward = moonR.subtract(moonR.normalized().multiply(20_000_000.0));
        OrbitState inbound = new OrbitState(
                earth, toward, new Vector3d(0.0, 0.0, 1_000.0), 0.0);
        OrbitState captured = PatchedConics.retarget(inbound);
        if (!"moon".equals(captured.centralBody().id())) {
            throw new IllegalStateException("20 000 km from the Moon must enter the SOI");
        }
        OrbitState lunarPark = SolarSystem.circular(moon, 100_000.0);
        OrbitState stayed = PatchedConics.retarget(lunarPark);
        if (!"moon".equals(stayed.centralBody().id())) {
            throw new IllegalStateException("100 km lunar parking must stay on the Moon");
        }
        if (WorldHandoff.inWorld(stayed)) {
            throw new IllegalStateException("Moon parking is the map, not the Overworld");
        }
        if (SolarSystem.atmosphereOf(moon).densityKgPerM3(0.0) != 0.0) {
            throw new IllegalStateException("Moon has no air column");
        }
        OrbitState far = new OrbitState(
                moon,
                new Vector3d(soi + 5_000_000.0, 0.0, 0.0),
                new Vector3d(0.0, 0.0, 200.0),
                0.0);
        OrbitState escaped = PatchedConics.retarget(far);
        if (!"earth".equals(escaped.centralBody().id())) {
            throw new IllegalStateException("Outside the SOI must return to Earth");
        }
        return new Result(soi / 1000.0, true, true, true);
    }
}
