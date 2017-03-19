package com.formulasearchengine.mathmltools.mathmlquerygenerator;

import com.formulasearchengine.mathmlquerygenerator.XQueryGeneratorBase;
import junit.framework.TestCase;

public class XQueryGeneratorBaseTest extends TestCase {

    public void testNoMath() throws Exception {
        XQueryGeneratorBase qg = new XQueryGeneratorBase();
        assertNull("Input without math should return null", qg.toString());
    }


}