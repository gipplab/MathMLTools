package com.formulasearchengine.mathmltools.xmlhelper;

import static org.xmlunit.util.Convert.toInputSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;

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
        final InputSource is = toInputSource(source);
        try {
            return getDocument(is); //getDocument(is);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            LOG.warn("Cannot parse XML file with grammar: " + xmlF.toString(), e);
        }
        return oldgetDocumentFromXML(xmlF);


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
        final InputSource is = toInputSource(source);
        try {
            return getDocument(is);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        try {
            return parse(source);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return oldgetDocumentFromXMLString(xml);
    }

    private static Document parse(Source s) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder b =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return b.parse(Convert.toInputSource(s));
    }

    public static Node getNodeFromXML(Path xmlF) {
        return getDocumentFromXML(xmlF).getDocumentElement();
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
        dbf.setFeature("http://xml.org/sax/features/validation", true);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setNamespaceAware(true);
        return dbf;
    }
}
