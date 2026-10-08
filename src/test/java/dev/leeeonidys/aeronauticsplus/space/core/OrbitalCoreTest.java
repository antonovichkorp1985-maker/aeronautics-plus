package dev.leeeonidys.aeronauticsplus.space.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrbitalCoreTest {
    private static final CelestialBody EARTH =
            new CelestialBody("earth", 3.986004418e14, 6.371e6);

    @Test
    void circularOrbitKeepsRadiusWithinNumericalTolerance() {
        double radius = EARTH.radiusMeters() + 400_000.0;
        double circularSpeed = Math.sqrt(EARTH.gravitationalParameter() / radius);
        OrbitState initial = new OrbitState(
                EARTH,
                new Vector3d(radius, 0.0, 0.0),
                new Vector3d(0.0, circularSpeed, 0.0),
                0.0);

        double period = 2.0 * Math.PI * Math.sqrt(Math.pow(radius, 3) / EARTH.gravitationalParameter());
        OrbitState result = OrbitalSimulator.propagate(initial, period, 2.0);

        assertEquals(radius, result.positionMeters().magnitude(), 2_000.0);
        assertEquals(circularSpeed, result.velocityMetersPerSecond().magnitude(), 2.0);
    }

    @Test
    void impulseChangesVelocityWithoutChangingPosition() {
        OrbitState initial = new OrbitState(
                EARTH,
                new Vector3d(7.0e6, 0.0, 0.0),
                new Vector3d(0.0, 7_500.0, 0.0),
                100.0);
        OrbitState after = OrbitalSimulator.applyImpulse(
                initial,
                new Maneuver(100.0, new Vector3d(0.0, 100.0, 0.0), 10.0));

        assertEquals(initial.positionMeters(), after.positionMeters());
        assertEquals(7_600.0, after.velocityMetersPerSecond().y(), 1.0e-9);
    }

    @Test
    void rocketEquationAndMassConsumptionAreDeterministic() {
        double deltaV = PropulsionMath.idealDeltaV(3_000.0, 10_000.0, 20_000.0);
        assertEquals(3_295.836866, deltaV, 1.0e-6);

        CraftState craft = new CraftState("test-craft", 10_000.0, 20_000.0,
                new OrbitState(EARTH, new Vector3d(7.0e6, 0, 0), Vector3d.ZERO, 0));
        assertEquals(20_000.0, craft.consumePropellant(5_000.0).propellantMassKg(), 1.0e-9);
        assertThrows(IllegalArgumentException.class, () -> craft.consumePropellant(25_000.0));
        assertTrue(craft.totalMassKg() > craft.dryMassKg());
    }
}
