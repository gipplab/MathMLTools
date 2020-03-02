package com.formulasearchengine.mathmltools.io;

import com.formulasearchengine.mathmltools.exceptions.ParsingErrorHandler;
import com.formulasearchengine.mathmltools.exceptions.Severity;
import com.formulasearchengine.mathmltools.mml.MathDoc;
import com.formulasearchengine.mathmltools.xml.PartialLocalEntityResolver;
import com.google.common.base.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helper class to format XML files to Document and Node types
 */
public class XmlDocumentReader {
    private static final Logger LOG = LogManager.getLogger(XmlDocumentReader.class.getName());

    public static final DocumentBuilder ValidationBuilder = XmlDocumentReader.getDefaultValidatingDocBuilder();

    public static final DocumentBuilder NoValidationBuilder = XmlDocumentReader.getDefaultNoValidatingDocBuilder();

    private XmlDocumentReader() {
    }

    private static DocumentBuilderFactory getDocumentBuilderFactory() throws ParserConfigurationException {
        return getStandardDocumentBuilderFactory(true);
    }

    /**
     * Parses XML to a W3C Document object with deactivated validation.
     * Equivalent to {@link #parse(String, boolean)} with validation on.
     *
     * @param xml in string format
     * @return Document of XML object.
     * @throws IOException              if an IO error occurs
     * @throws SAXException             if an error due parsing or validating occurs
     * @throws IllegalArgumentException if the argument is somehow invalid
     */
    public static Document parse(String xml) throws IOException, SAXException, IllegalArgumentException {
        return parse(xml, true);
    }

    /**
     * Parses XML to a W3C Document object with deactivated validation.
     * Equivalent to {@link #parse(Path, boolean)} with validation on.
     *
     * @param path to a XML file
     * @return Document of XML file.
     * @throws IOException              if an IO error occurs
     * @throws SAXException             if an error due parsing or validating occurs
     * @throws IllegalArgumentException if the argument is somehow invalid
     */
    public static Document parse(Path path) throws IOException, SAXException, IllegalArgumentException {
        return parse(path, true);
    }

    /**
     * Parses XML to a W3C Document object with deactivated validation.
     * Equivalent to {@link #parse(File, boolean)} with validation on.
     *
     * @param file XML
     * @return Document of XML file.
     * @throws IOException              if an IO error occurs
     * @throws SAXException             if an error due parsing or validating occurs
     * @throws IllegalArgumentException if the argument is somehow invalid
     */
    public static Document parse(File file) throws IOException, SAXException, IllegalArgumentException {
        return parse(file, true);
    }

    /**
     * Parses XML to a W3C Document object with activated or deactivated validation.
     * In case of validation, a correct header is needed. Therefore, we try to fix the
     * header before we try to parse the input. Also, an error handler is activated
     * that throws {@link SAXException} if an error due the validation or parsing process
     * will be thrown.
     * <p>
     * In case of deactivated validation, there will be no SAXException occur!
     *
     * @param xml        in string format
     * @param validation turn validation on or off
     * @return Document of XML object.
     * @throws IOException              if an IO error occurs
     * @throws SAXException             only occurs when validation is on and an error due parsing the document occurred.
     * @throws IllegalArgumentException if the argument is null
     */
    public static Document parse(String xml, boolean validation) throws IOException, SAXException, IllegalArgumentException {
        // if validation is on, we need to add headers
        if (validation) {
            xml = MathDoc.fixingHeaderAndNS(xml);
        }

        InputSource src = stringToSource(xml);

        if (validation) {
            return ValidationBuilder.parse(src);
        } else {
            return XmlDocumentReader.getDefaultNoValidatingDocBuilder().parse(src);
        }
    }

    /**
     * Parses XML to a W3C Document object with activated or deactivated validation.
     * In case of validation, a correct header is needed. Therefore, we try to fix the
     * header before we try to parse the input. Also, an error handler is activated
     * that throws {@link SAXException} if an error due the validation or parsing process
     * will be thrown.
     * <p>
     * In case of deactivated validation, there will be no SAXException occur!
     *
     * @param path       to a XML file
     * @param validation turn validation on or off
     * @return Document of XML object.
     * @throws IOException              if an IO error occurs
     * @throws SAXException             only occurs when validation is on and an error due parsing the document occurred.
     * @throws IllegalArgumentException if the argument is null
     */
    public static Document parse(Path path, boolean validation) throws IOException, SAXException, IllegalArgumentException {
        return parse(path.toFile(), validation);
    }

    /**
     * Parses XML to a W3C Document object with activated or deactivated validation.
     * In case of validation, a correct header is needed. Therefore, we try to fix the
     * header before we try to parse the input. Also, an error handler is activated
     * that throws {@link SAXException} if an error due the validation or parsing process
     * will be thrown.
     * <p>
     * In case of deactivated validation, there will be no SAXException occur!
     *
     * @param file       XML
     * @param validation turn validation on or off
     * @return Document of XML object.
     * @throws IOException              if an IO error occurs
     * @throws SAXException             only occurs when validation is on and an error due parsing the document occurred.
     * @throws IllegalArgumentException if the argument is null
     */
    public static Document parse(File file, boolean validation) throws IOException, SAXException, IllegalArgumentException {
        if (validation) {
            try {
                return ValidationBuilder.parse(file);
            } catch (SAXException saxe) {
                LOG.debug("Parsing exception occur. It's most likely a wrong header. Try to fixing it...");
                String str = new String(Files.readAllBytes(file.toPath()), Charsets.UTF_8);
                return parse(str, validation);
            }
        } else {
            return NoValidationBuilder.parse(file);
        }
    }

    public static Node parseToNode(String xml) throws IOException, SAXException, IllegalArgumentException {
        Document doc = parse(xml);
        return doc.getDocumentElement();
    }

    public static Node parseToNode(String xml, boolean validation) throws IOException, SAXException, IllegalArgumentException {
        Document doc = parse(xml, validation);
        return doc.getDocumentElement();
    }

    public static Node parseToNode(Path p) throws IOException, SAXException, IllegalArgumentException {
        Document doc = parse(p);
        return doc.getDocumentElement();
    }

    public static Node parseToNode(Path p, boolean validation) throws IOException, SAXException, IllegalArgumentException {
        Document doc = parse(p, validation);
        return doc.getDocumentElement();
    }

    public static Node parseToNode(File file) throws IOException, SAXException, IllegalArgumentException {
        Document doc = parse(file);
        return doc.getDocumentElement();
    }

    public static Node parseToNode(File file, boolean validation) throws IOException, SAXException, IllegalArgumentException {
        Document doc = parse(file, validation);
        return doc.getDocumentElement();
    }

    /**
     * Convert a string to an InputSource object
     *
     * @param str string
     * @return InputSource of input
     */
    private static InputSource stringToSource(String str) {
        InputSource is = new InputSource(new StringReader(str));
        is.setEncoding("UTF-8");
        return is;
    }

    private static DocumentBuilder getDefaultValidatingDocBuilder() {
        try {
            DocumentBuilder db = getStandardDocumentBuilderFactory(true).newDocumentBuilder();
            db.setErrorHandler(new ParsingErrorHandler(Severity.SEVERE));
            db.setEntityResolver(new PartialLocalEntityResolver());
            return db;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Cannot create default DocumentBuilder. " + e.getMessage(), e);
        }
    }

    private static DocumentBuilder getDefaultNoValidatingDocBuilder() {
        try {
            DocumentBuilder db = getStandardDocumentBuilderFactory(false).newDocumentBuilder();
            db.setErrorHandler(new ParsingErrorHandler(Severity.NOTIFY));
            db.setEntityResolver(new PartialLocalEntityResolver());
            return db;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Cannot create default DocumentBuilder. " + e.getMessage(), e);
        }
    }

    /**
     * This method creates a DocumentBuilderFactory that we will always need.
     *
     * @see <a href="http://xerces.apache.org/xerces-j/features.html">Apache XML Features</a>
     * @return the standard document builder factory we usually use
     */
    public static DocumentBuilderFactory getStandardDocumentBuilderFactory(boolean validating) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            // enable validation (must specify a grammar)
            dbf.setValidating(validating);
            dbf.setFeature("http://xml.org/sax/features/validation", validating);

            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", validating);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validating);

            dbf.setFeature("http://apache.org/xml/features/validation/schema", true);
            dbf.setFeature("http://xml.org/sax/features/namespaces", true);

            dbf.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", false);
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException("Cannot load standard DocumentBuilderFactory. " + pce.getMessage(), pce);
        }

        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        dbf.setExpandEntityReferences(true);

        return dbf;
    }

    public static void main(String[] args) throws Exception {
        Path p = Paths.get("query1.xml");
//        String mmlBessel = new String(Files.readAllBytes(p));

        Document doc = parse(p);

//
//        mmlBessel = MathDoc.tryFixHeader(mmlBessel);
//
//        DocumentBuilder builder = getStandardDocumentBuilderFactory(true).newDocumentBuilder();
//        builder.setErrorHandler(new ParsingErrorHandler(Severity.NOTIFY));
//        builder.setEntityResolver(new PartialLocalEntityResolver());
//
//        InputSource input = new InputSource(new StringReader(mmlBessel));
//        Document doc = builder.parse(input);

        System.out.println(XmlDocumentWriter.stringify(doc));
    }
}
