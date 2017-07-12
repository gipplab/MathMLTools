package com.formulasearchengine.mathmltools.xmlhelper;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class XMLHelperTest {

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