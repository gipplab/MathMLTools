package com.formulasearchengine.mathmltools.xmlhelper;


import org.junit.jupiter.api.Test;
import org.w3c.dom.ls.LSInput;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Moritz on 18.03.2017.
 */
public class PartialLocalEntityResolverTest {

    public static final String BASE_URI = "https://www.w3.org/Math/XMLSchema/mathml3/mathml3.xsd";
    public static final String MATHML3_CONTENT_XSD = "mathml3-content.xsd";
    public static final String NAMESPACE_URI = "http://www.w3.org/1998/Math/MathML";
    public static final String HTTP_WWW_W3_ORG_2001_XMLSCHEMA = "http://www.w3.org/2001/XMLSchema";

    @Test
    public void resolveEntity() throws Exception {

        final PartialLocalEntityResolver resolver = new PartialLocalEntityResolver();
        assertNull(resolver.resolveEntity("a", "b"));
        assertNotNull(resolver.resolveEntity("a", "http://www.w3.org/Math/DTD/mathml2/xhtml-math11-f.dtd"));
    }

    @Test
    public void resolvePublicID() throws Exception {

        final PartialLocalEntityResolver resolver = new PartialLocalEntityResolver();
        assertNull(resolver.resolveEntity("a", "b"));
        assertNotNull(resolver.resolveEntity("-//W3C//ENTITIES HTML MathML Set//EN//XML", "local path"));
    }

    @Test
    public void resolveXSDnull() throws Exception {
        final PartialLocalEntityResolver resolver = new PartialLocalEntityResolver();
        assertNull(resolver.resolveResource(null, null, null, null, null));
    }

    @Test
    public void resolveXSDmathUnkownKey() throws Exception {
        final PartialLocalEntityResolver resolver = new PartialLocalEntityResolver();
        final LSInput lsInput = resolver.resolveResource(HTTP_WWW_W3_ORG_2001_XMLSCHEMA,
                NAMESPACE_URI,
                null,
                "this is not there",
                BASE_URI);
        assertNull(lsInput);
    }

    @Test
    public void resolveXSDmath() throws Exception {
        final PartialLocalEntityResolver resolver = new PartialLocalEntityResolver();
        final LSInput lsInput = resolver.resolveResource(HTTP_WWW_W3_ORG_2001_XMLSCHEMA,
                NAMESPACE_URI,
                null,
                MATHML3_CONTENT_XSD,
                BASE_URI);
        assertAll(
                () -> assertNotNull(lsInput),
                () -> assertEquals(BASE_URI, lsInput.getBaseURI()),
                () -> assertNull(lsInput.getPublicId()),
                () -> assertNull(lsInput.getStringData()),
                () -> assertFalse(lsInput.getCertifiedText())
        );

        lsInput.setPublicId("1");
        assertEquals("1", lsInput.getPublicId());

        lsInput.setStringData("string");
        assertEquals("string", lsInput.getStringData());

        lsInput.setCertifiedText(true);
        assertTrue(lsInput.getCertifiedText());

    }


}