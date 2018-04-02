package com.formulasearchengine.mathmltools.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class XMLHelperTest {
    @Test
    public void removeDoctype() throws Exception {
        StringBuffer xml =  new StringBuffer("");
        XMLHelper.removeDoctype(xml);
        assertEquals("", xml.toString());
        xml.append("<!DOCTYPE blah \\+=\"" +
                " >content");
        XMLHelper.removeDoctype(xml);
        assertEquals("content", xml.toString());
    }

    @Test
    public void removeXmlDeclaration() throws Exception {
        StringBuffer xml = new StringBuffer("");
        XMLHelper.removeXmlDeclaration(xml);
        assertEquals("", xml.toString());
        xml = xml.append("<?xML blah \\+=\"\n" +
                " ?>content");
        XMLHelper.removeXmlDeclaration(xml);
        assertEquals("content", xml.toString());
    }

    @Test
    public void testString2Doc() throws Exception {
        assertNull(XMLHelper.string2Doc("<open><open2></open2>", true));
        assertNotNull(XMLHelper.string2Doc("<simple />", true));
    }
}