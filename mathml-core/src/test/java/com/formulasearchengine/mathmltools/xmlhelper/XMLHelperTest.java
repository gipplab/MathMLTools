package com.formulasearchengine.mathmltools.xmlhelper;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

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
    public void testMainElement() throws Exception {
        Node mainElement = XMLHelper.getMainElement(new CMMLInfo(getResourceFile("mathml_mainelement.xml")));
        assertThat(mainElement, notNullValue());
    }

    @Test
    public void testString2Doc() throws Exception {
        assertNull(XMLHelper.string2Doc("<open><open2></open2>", true));
        assertNotNull(XMLHelper.string2Doc("<simple />", true));
    }

    private String getResourceFile(String filename) throws IOException, ParserConfigurationException {
        return IOUtils.toString(this.getClass().getResourceAsStream(filename), "UTF-8");
    }
}