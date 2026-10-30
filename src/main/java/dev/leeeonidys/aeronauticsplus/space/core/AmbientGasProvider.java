package dev.leeeonidys.aeronauticsplus.space.core;

/**
 * Provider of ambient atmospheric density and composition by geometric altitude
 * above a body surface. Used by wings (lift/drag) and engines (combustion,
 * specific impulse). Defaults to the SolarSystem atmospheric columns;
 * ChemMod may override with real multi-layer density.
 */
@FunctionalInterface
public interface AmbientGasProvider {
    /** Default provider: SolarSystem atmospheric columns. */
    AmbientGasProvider DEFAULT = (bodyId, altitudeMeters) -> SolarSystem.atmosphereOf(
            SolarSystem.body(bodyId)).densityKgPerM3(altitudeMeters);

    /** Atmospheric density at geometric altitude above the body surface, kg/m³. */
    double densityKgPerM3(String bodyId, double geometricAltitudeMeters);
}
