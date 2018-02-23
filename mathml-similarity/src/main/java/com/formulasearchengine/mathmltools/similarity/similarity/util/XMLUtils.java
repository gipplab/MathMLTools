package com.formulasearchengine.mathmltools.similarity.similarity.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Utility class with static methods to printMathNode or navigate through
 * XML document nodes.
 *
 * @author Vincent Stange
 */
public class XMLUtils {

    private XMLUtils() {
        // not visible, utility class only
    }

    /**
     * Only return child nodes that are elements - text nodes are ignored.
     *
     * @param node We will take the children from this node.
     * @return New ordered list of child elements.
     */
    public static ArrayList<Element> getChildElements(Node node) {
        ArrayList<Element> childElements = new ArrayList<>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                childElements.add((Element) childNodes.item(i));
            }
        }
        return childElements;
    }

    /**
     * Prints out a XML node as a String.
     *
     * @param node   node to be printed
     * @param indent pretty printMathNode via indent on?
     * @return String representation
     * @throws TransformerException mostly not xml conform
     */
    public static String nodeToString(Node node, boolean indent) throws TransformerException {
        StringWriter sw = new StringWriter();
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
        t.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString();
    }
}
