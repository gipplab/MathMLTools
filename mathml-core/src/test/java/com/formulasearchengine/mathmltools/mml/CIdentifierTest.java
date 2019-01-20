package com.formulasearchengine.mathmltools.mml;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import static com.formulasearchengine.mathmltools.mml.MathTest.getFileContents;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

class CIdentifierTest {
    public static final String TEST_DIR = "com/formulasearchengine/mathmltools/mml/tests/";

    private CIdentifier getE() throws IOException, SAXException {
        final String sampleMML = getFileContents(TEST_DIR + "Emc2.mml");
        final MathDoc mml = new MathDoc(sampleMML);
        return mml.getIdentifiers().get(0);
    }

    @Test
    void getName() throws Exception {
        final CIdentifier E = getE();
        assertEquals("E",E.getName());
    }

    @Test
    void getPresentation() throws Exception {
        final CIdentifier E = getE();
        String id = E.getPresentation().getAttributes().getNamedItem("id").getNodeValue();
        assertEquals("p1.1.m1.1.1", id);
    }

    @Test
    void getXRef() throws Exception {
        final CIdentifier E = getE();
        assertEquals("p1.1.m1.1.1", E.getXref());

    }

    @Test
    void testHashCode() throws Exception {
        final CIdentifier E = getE();
        final int eHash = "E".hashCode();
        assertEquals(eHash, E.hashCode());
    }

    @Test
    void testToString() throws Exception {
        final CIdentifier E = getE();
        assertEquals("E", E.toString());
    }

    @Test
    void equalsTo()  throws Exception {
        final CIdentifier E = getE();
        final CIdentifier E2 = getE();
        assertEquals(E2, E);
    }
    @Test
    void equalsTo2()  throws Exception {
        final CIdentifier E = getE();
        assertNotEquals("E", E);
    }
}
