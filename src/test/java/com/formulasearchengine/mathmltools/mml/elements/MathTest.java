package com.formulasearchengine.mathmltools.mml.elements;


import org.junit.jupiter.api.Test;

class MathTest {
    @Test
    void emptyMathTag() throws Exception {
        new Math("<math/>");
    }
}