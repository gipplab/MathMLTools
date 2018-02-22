package com.formulasearchengine.mathmltools.converters.mathoid;

import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Vincent Stange
 */
public class EnrichedMathMLTransformerTest {

    @Test
    public void copyIdField() throws Exception {
        // prepare
        Document document = XMLHelper.string2Doc("<ele data-semantic-id=\"1\" />", false);
        EnrichedMathMLTransformer transformer = new EnrichedMathMLTransformer("<math />");
        Element firstChild = (Element) document.getFirstChild();
        // execute
        transformer.copyIdField(firstChild);
        // validate
        assertThat(firstChild.getAttribute("id"), is("p1"));
    }

}