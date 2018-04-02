package com.formulasearchengine.mathmltools.io;

import com.formulasearchengine.mathmltools.mml.MathDoc;
import com.formulasearchengine.mathmltools.exceptions.ThrowAllErrorHandler;
import com.formulasearchengine.mathmltools.xml.PartialLocalEntityResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Helper class to format XML files to Document and Node types
 */
public class XmlDocumentReader {
    private XmlDocumentReader() {
    }

    private static final Logger LOG = LogManager.getLogger(XmlDocumentReader.class.getName());

    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

    static {
        builderFactory.setIgnoringComments(true);
        //builderFactory.setIgnoringElementContentWhitespace(true);
        //builderFactory.setValidating(true);
        builderFactory.setExpandEntityReferences(true);
    }

    public static Document getDocumentFromXML(Path xmlF) {
        final Source source = Input.fromFile(xmlF.toAbsolutePath().toString()).build();
        String orig = null;
        try {
            orig = new String(Files.readAllBytes(xmlF));
        } catch (IOException ioe) {
            LOG.fatal("Cannot read xml file " + xmlF.toString());
        }
        return getDocumentFromSource(source, orig);
    }

    private static Document getDocument(InputSource inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = getDocumentBuilder();
        return builder.parse(inputStream);
    }

    private static Document oldgetDocumentFromXMLString(String xml) {
        try {
            LOG.debug("Start reading process from XML file.");
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            InputSource input = new InputSource(new StringReader(xml));
            Document doc = builder.parse(input);
            LOG.debug("Successfully read from XML file.");
            return doc;
        } catch (ParserConfigurationException pce) {
            // how could this happen, without any configurations? ---
            LOG.error("Cannot create DocumentBuilder...", pce);
        } catch (SAXException e) {
            LOG.error("Cannot parse XML file: " + xml, e);
        } catch (IOException e) {
            LOG.error("Cannot read file: " + xml, e);
        }
        return null;
    }

    public static Document oldgetDocumentFromXML(Path xmlF) {
        try {
            LOG.debug("Start reading process from XML file.");
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            InputStream inputStream = Files.newInputStream(xmlF.toAbsolutePath());
            Document doc = builder.parse(inputStream);
            LOG.debug("Successfully read from XML file.");
            return doc;
        } catch (ParserConfigurationException pce) {
            // how could this happen, without any configurations? ---
            LOG.error("Cannot create DocumentBuilder...", pce);
        } catch (SAXException e) {
            LOG.error("Cannot parse XML file: " + xmlF.toString(), e);
        } catch (IOException e) {
            LOG.error("Cannot read file: " + xmlF.toString(), e);
        }
        return null;
    }

    public static Document getDocumentFromXMLString(String xml) {
        Source source = Input.fromString(xml).build();
        return getDocumentFromSource(source, xml);
    }

    private static Document getDocumentFromSource(Source source, String orig) {
        final InputSource is = Convert.toInputSource(source);
        try {
            return getDocument(is);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            LOG.warn("Cannot parse document directly '{}'.", e.getMessage());
        }
        try {
            return parse(source);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            LOG.warn("Cannot parse on second attempt '{}'.", e.getMessage());
        }
        return oldgetDocumentFromXMLString(orig);
    }

    private static Document parse(Source s) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder b =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return b.parse(Convert.toInputSource(s));
    }

    public static Node getNodeFromXML(Path xmlF) {
        return getDocumentFromXML(xmlF).getDocumentElement();
    }

    public static Document strictLoader(String xml) throws Exception {
        DocumentBuilder builder = getDocumentBuilder();
        InputSource input = new InputSource(new StringReader(xml));
        Document doc = builder.parse(input);
        return doc;
    }

    public static Document loadAndRepair(String xml, String prefix) throws Exception {
        xml = MathDoc.tryFixHeader(xml, prefix);

        DocumentBuilder builder = getDocumentBuilder();
        InputSource input = new InputSource(new StringReader(xml));
        Document doc = builder.parse(input);
        return doc;
    }

    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = getDocumentBuilderFactory();
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(new ThrowAllErrorHandler());
        db.setEntityResolver(new PartialLocalEntityResolver());
        return db;
    }

    private static DocumentBuilderFactory getDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);

        // for all features available check: http://xerces.apache.org/xerces-j/features.html

        // enable validation (must specify a grammar)
        dbf.setFeature("http://xml.org/sax/features/validation", true);

        dbf.setFeature("http://apache.org/xml/features/validation/schema", true);
        dbf.setFeature("http://xml.org/sax/features/namespaces", true);

        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", true);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);

        // delete whitespaces (might appear after canonicalize MML)
        dbf.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", false);

        dbf.setNamespaceAware(true);
        return dbf;
    }
}
