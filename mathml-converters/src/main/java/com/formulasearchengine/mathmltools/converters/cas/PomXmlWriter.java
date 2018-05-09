package com.formulasearchengine.mathmltools.converters.cas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.util.List;

import static com.formulasearchengine.mathmltools.utils.Utility.NL;
import static com.formulasearchengine.mathmltools.converters.cas.POMLoader.Methods.*;

/**
 * A class to parse
 */
public class PomXmlWriter {

    private static final Logger LOG = LogManager.getLogger(PomXmlWriter.class.getName());

    private static final String TAB = "  ";
    private static final String EXPR_NODE = "expression";
    private static final String TERM_NODE = "term";
    private static final String ATTR_PRIME_TAG = "prime-tag";
    private static final String ATTR_SEC_TAG = "secondary-tags";

    private PomXmlWriter() {
    }

    public static void writeStraightXML(POMLoader pom, OutputStream outputStream)
            throws XMLStreamException {
        LOG.debug("Start XML writing process...");
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(outputStream);

        LOG.trace("Start document writing..");
        // start
        writer.writeStartDocument();
        writer.writeCharacters(NL);
        writer.writeStartElement("math");
        writer.writeAttribute("type", "pom-exported");
        writer.writeCharacters(NL);

        LOG.trace("Start recursive writing..");
        recursiveInnerXML(writer, pom, pom.getParsedTree(), 1);
        // write inner elements

        LOG.trace("Close document.");
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
        LOG.info("XML writing successful!");
    }

    @SuppressWarnings("unchecked")
    private static void recursiveInnerXML(XMLStreamWriter writer, POMLoader pom, Object pte, int tabLevel)
            throws XMLStreamException {
        writer.writeCharacters(generateTab(tabLevel));

        try {
            // List<PomTaggedExpression>
            Object listObj = pom.invoke(pteGetComponents, pte);
            List<Object> comps = (List<Object>) listObj;

            Object mathTermObj = pom.invoke(pteGetRoot, pte);
            String pTag = (String) pom.invoke(pteGetTag, pte);
            List<String> secTags = (List<String>) pom.invoke(pteGetSecondaryTags, pte);

            boolean termMode = false;

            boolean isEmpty = (Boolean) pom.invoke(mtIsEmpty, mathTermObj);
            if (mathTermObj != null && !isEmpty) {
                pTag = (String) pom.invoke(mtGetTag, mathTermObj);
                secTags = (List<String>) pom.invoke(mtGetSecondaryTags, mathTermObj);
                termMode = true;
            }

            if (comps.isEmpty()) { // recursive anchor
                if (termMode) {
                    writer.writeStartElement(TERM_NODE);
                } else {
                    writer.writeEmptyElement(EXPR_NODE);
                }

                if (pTag != null) {
                    writer.writeAttribute(ATTR_PRIME_TAG, pTag);
                }
                if (!secTags.isEmpty()) {
                    writer.writeAttribute(ATTR_SEC_TAG, createListString(secTags));
                }

                if (termMode) {
                    String termText = (String) pom.invoke(mtGetTermText, mathTermObj);
                    writer.writeCharacters(termText);
                    writer.writeEndElement();
                }

                writer.writeCharacters(NL);
                return;
            }

            writer.writeStartElement(EXPR_NODE);
            if (pTag != null) {
                writer.writeAttribute(ATTR_PRIME_TAG, pTag);
            }
            if (!secTags.isEmpty()) {
                writer.writeAttribute(ATTR_SEC_TAG, createListString(secTags));
            }
            writer.writeCharacters(NL);

            for (Object exp : comps) {
                recursiveInnerXML(writer, pom, exp, tabLevel + 1);
            }

            writer.writeCharacters(generateTab(tabLevel));
            writer.writeEndElement();
            writer.writeCharacters(NL);
        } catch (Exception e) {
            LOG.error("Recursive Inner Parse exception.", e);
        }
    }

    public static String createListString(List<String> list) {
        if (list.isEmpty()) {
            return "";
        }
        String out = "";
        for (String s : list) {
            out += s + ", ";
        }
        return out.substring(0, out.length() - 2);
    }

    public static String generateTab(int level) {
        String str = "";
        for (int i = 1; i <= level; i++) {
            str += TAB;
        }
        return str;
    }
}
