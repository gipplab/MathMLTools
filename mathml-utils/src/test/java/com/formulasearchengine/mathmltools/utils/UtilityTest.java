package com.formulasearchengine.mathmltools.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilityTest {
    private String badExample =
            "H^{*}_\\lambda =\\left(\\prod_{i=1}^{k}n_{i}!\\right)\\prod_{i&lt;j}\\frac{n_{i}+n_{j}%&#10;}{n_{i}-n_{j}}=2^{-\\frac{k}{2}}\\sqrt{H_{\\tilde{\\lambda}}},\\quad\\frac{1}{\\sqrt{%&#10;H_{\\tilde{\\lambda}}}}=\\sqrt{s_{\\tilde{\\lambda}}({\\bf t}_{\\infty})}=2^{-\\frac{k%&#10;}{2}}Q_{\\lambda}(\\frac{{\\bf t}_{\\infty}}{2}),";
    private String goodExample =
            "H^{*}_{\\lambda} =\\left(\\prod_{i=1}^{k}n_{i}!\\right)\\prod_{i<j}\\frac{n_{i}+n_{j}" +
                    "}{n_{i}-n_{j}}=2^{-\\frac{k}{2}}\\sqrt{H_{\\tilde{\\lambda}}},\\quad\\frac{1}{\\sqrt{" +
                    "H_{\\tilde{\\lambda}}}}=\\sqrt{s_{\\tilde{\\lambda}}({\\bf t}_{\\infty})}=2^{-\\frac{k" +
                    "}{2}}Q_{\\lambda}(\\frac{{\\bf t}_{\\infty}}{2})";

    @Test
    public void htmlEscapeSimple() {
        String simpleBad = "n_{j}%&#10;q";
        String simpleGood = "n_{j}q";
        assertEquals(simpleGood, Utility.latexPreProcessing(simpleBad));
    }

    @Test
    public void htmlEscapeMultiSimple() {
        String simpleMultiBad = "n_{%&#10;j}%&#10;q";
        String simpleMultiGood = "n_{j}q";
        assertEquals(simpleMultiGood, Utility.latexPreProcessing(simpleMultiBad));
    }

    @Test
    public void htmlEscapeIntermediate() {
        String intBad = "n_{%&#10;j&lt;i}%&#10;q";
        String intGood = "n_{j<i}q";
        assertEquals(intGood, Utility.latexPreProcessing(intBad));
    }

    @Test
    public void bugCheckUnderscore() {
        String simpleBad = "n_\\lol";
        String simpleGood = "n_{\\lol}";
        assertEquals(simpleGood, Utility.latexPreProcessing(simpleBad));
    }

    @Test
    public void bugCheckUnderscoreMulti() {
        String multiBad = "H^{*}_\\lambda = K_\\Deluxe deluxe";
        String multiGood = "H^{*}_{\\lambda} = K_{\\Deluxe} deluxe";
        assertEquals(multiGood, Utility.latexPreProcessing(multiBad));
    }

    @Test
    public void badEnding() {
        String bad = "ib\\sa,";
        String good = "ib\\sa";
        assertEquals(good, Utility.latexPreProcessing(bad));
    }

    @Test
    public void badStartEnding() {
        String bad = "\\[\\{\\}\\]";
        String good = "\\{\\}";
        assertEquals(good, Utility.latexPreProcessing(bad));
    }

    @Test
    public void realExample() {
        assertEquals(goodExample, Utility.latexPreProcessing(badExample));
    }
}
