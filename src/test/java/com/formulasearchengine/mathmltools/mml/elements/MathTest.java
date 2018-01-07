package com.formulasearchengine.mathmltools.mml.elements;

import org.junit.Test;

public class MathTest {
    @Test
    public void emptyMathTag() throws Exception {
        new Math("<math/>");
    }
}