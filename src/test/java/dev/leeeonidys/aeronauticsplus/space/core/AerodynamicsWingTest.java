package dev.leeeonidys.aeronauticsplus.space.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class AerodynamicsWingTest {

    @Test
    void clAlphaZeroAtZeroAoA() {
        double cl = Aerodynamics.wingClAlpha(0.0, 8.0, 0.0, false);
        assertEquals(0.0, cl, 1e-9);
    }

    @Test
    void clAlphaPositiveAtPositiveAoA() {
        double cl = Aerodynamics.wingClAlpha(Math.toRadians(5.0), 8.0, 0.0, false);
        assertTrue(cl > 0.0);
        assertTrue(cl < 1.8);
    }

    @Test
    void clAlphaNegativeAtNegativeAoA() {
        double cl = Aerodynamics.wingClAlpha(Math.toRadians(-5.0), 8.0, 0.0, false);
        assertTrue(cl < 0.0);
        assertTrue(cl > -1.8);
    }

    @Test
    void higherAspectRatioHigherLiftSlope() {
        double clLow = Aerodynamics.wingClAlpha(Math.toRadians(5.0), 4.0, 0.0, false);
        double clHigh = Aerodynamics.wingClAlpha(Math.toRadians(5.0), 12.0, 0.0, false);
        assertTrue(clHigh > clLow);
    }

    @Test
    void stallReducesLift() {
        double before = Aerodynamics.wingClAlpha(Math.toRadians(10.0), 8.0, 0.0, false);
        double atStall = Aerodynamics.wingClAlpha(Math.toRadians(15.0), 8.0, 0.0, false);
        double after = Aerodynamics.wingClAlpha(Math.toRadians(30.0), 8.0, 0.0, false);
        assertTrue(before > 0.0);
        assertTrue(after < before);
        assertTrue(atStall > 0.0);
    }

    @Test
    void inducedDragPositive() {
        double cd = Aerodynamics.wingInducedCd(0.5, 8.0, false);
        assertTrue(cd > 0.0);
    }

    @Test
    void tipReducesInducedDrag() {
        double cdNoTip = Aerodynamics.wingInducedCd(0.5, 8.0, false);
        double cdWithTip = Aerodynamics.wingInducedCd(0.5, 8.0, true);
        assertTrue(cdWithTip < cdNoTip);
    }

    @Test
    void profileCdPositive() {
        assertTrue(Aerodynamics.wingProfileCd() > 0.0);
    }

    @Test
    void totalCdIncludesProfile() {
        double induced = Aerodynamics.wingInducedCd(0.5, 8.0, false);
        double total = Aerodynamics.wingCd(0.5, 8.0, false);
        assertEquals(induced + Aerodynamics.wingProfileCd(), total, 1e-12);
    }

    @Test
    void elevonRollMomentScalesWithDeflection() {
        double m0 = Aerodynamics.elevonRollMomentNm(0.0, 1.0, 1000.0);
        double m1 = Aerodynamics.elevonRollMomentNm(0.5, 1.0, 1000.0);
        assertEquals(0.0, m0, 1e-12);
        assertTrue(m1 > m0);
    }

    @Test
    void elevonPitchMomentScalesWithDeflection() {
        double m0 = Aerodynamics.elevonPitchMomentNm(0.0, 1.0, 1000.0);
        double m1 = Aerodynamics.elevonPitchMomentNm(0.5, 1.0, 1000.0);
        assertEquals(0.0, m0, 1e-12);
        assertTrue(m1 > m0);
    }

    @Test
    void spanFactorIntactIsOne() {
        assertEquals(1.0, Aerodynamics.wingSpanFactor(5, 5), 1e-12);
    }

    @Test
    void spanFactorHalfIsHalf() {
        assertEquals(0.5, Aerodynamics.wingSpanFactor(3, 6), 1e-12);
    }

    @Test
    void spanFactorZeroIsZero() {
        assertEquals(0.0, Aerodynamics.wingSpanFactor(0, 5), 1e-12);
    }

    @Test
    void invalidInputsThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> Aerodynamics.wingClAlpha(Double.NaN, 8.0, 0.0, false));
        assertThrows(IllegalArgumentException.class,
                () -> Aerodynamics.wingInducedCd(0.5, -1.0, false));
        assertThrows(IllegalArgumentException.class,
                () -> Aerodynamics.wingSpanFactor(6, 5));
        assertThrows(IllegalArgumentException.class,
                () -> Aerodynamics.elevonRollMomentNm(Double.NaN, 1.0, 1000.0));
        assertThrows(IllegalArgumentException.class,
                () -> Aerodynamics.elevonPitchMomentNm(0.1, -1.0, 1000.0));
    }
}
