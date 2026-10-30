package dev.leeeonidys.aeronauticsplus.space.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ChemModIntegrationTest {

    @Test
    void chemModNotPresentByDefault() {
        // Without ChemMod on the classpath, isChemModPresent() must be false.
        assertFalse(ChemModIntegration.isChemModPresent());
    }

    @Test
    void massProviderFallsBackToDefault() {
        var provider = ChemModIntegration.massProvider();
        assertNotNull(provider);
        // Default provider returns the catalog mass for a known part.
        assertEquals(45.0, provider.massKg("wing_root"), 1e-9);
        assertEquals(30.0, provider.massKg("rocket_wing"), 1e-9);
    }

    @Test
    void ambientGasProviderFallsBackToDefault() {
        var provider = ChemModIntegration.ambientGasProvider();
        assertNotNull(provider);
        // Earth surface density ~1.225 kg/m3 at altitude 0.
        double earthSurface = provider.densityKgPerM3("earth", 0.0);
        assertTrue(earthSurface > 1.0);
        assertTrue(earthSurface < 2.0);
        // Vacuum on the Moon: density ~0.
        double moonSurface = provider.densityKgPerM3("moon", 0.0);
        assertEquals(0.0, moonSurface, 1e-9);
    }

    @Test
    void densityDecreasesWithAltitude() {
        var provider = ChemModIntegration.ambientGasProvider();
        double seaLevel = provider.densityKgPerM3("earth", 0.0);
        double tenKm = provider.densityKgPerM3("earth", 10_000.0);
        assertTrue(tenKm < seaLevel);
    }
}
