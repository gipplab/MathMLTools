package com.formulasearchengine.mathmltools.io;

import com.formulasearchengine.mathmltools.mml.MathDoc;
import com.google.common.base.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This utility class allows to write documents to files or convert them into strings.
 *
 * @author Andre Greiner-Petter
 * @see DocumentOutputFormatConfiguration
 * @see XMLSerializer
 */
public class XmlDocumentWriter {

    private static final Logger LOG = LogManager.getLogger(XmlDocumentWriter.class.getName());

    private XmlDocumentWriter() {
    }

    public static String stringify(MathDoc mathDoc) throws TransformerException {
        return stringify(mathDoc.getDom());
    }

    public static String stringify(MathDoc mathDoc, DocumentOutputFormatConfiguration config)
            throws TransformerException {
        return stringify(mathDoc.getDom(), config);
    }

    public static String stringify(Node node) throws TransformerException {
        return stringify(node, new DocumentOutputFormatConfiguration());
    }

    private static String stringify(Node node, DocumentOutputFormatConfiguration config) throws TransformerException {
        LOG.trace("Start serializing document as String.");
        Writer outxml = new StringWriter();

        transform(node, config, outxml);

        LOG.debug("Successfully serialized document to string.");
        return outxml.toString();
    }

    public static void writeToFile(MathDoc mathDoc, Path outputFile)
            throws TransformerException, IOException {
        writeToFile(mathDoc.getDom(), outputFile);
    }

    public static void writeToFile(MathDoc mathDoc, Path outputFile, DocumentOutputFormatConfiguration config)
            throws TransformerException, IOException {
        writeToFile(mathDoc.getDom(), outputFile, config);
    }

    public static void writeToFile(Node node, Path outputFile)
            throws TransformerException, IOException {
        writeToFile(node, outputFile, new DocumentOutputFormatConfiguration());
    }

    public static void writeToFile(Node node, Path outputFile, DocumentOutputFormatConfiguration config)
            throws TransformerException, IOException {
        // throws exceptions if file already exists.
        Files.createFile(outputFile);

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile.toFile()), Charsets.UTF_8)) {
            LOG.debug("Start writing file {}...", outputFile.getFileName());
            transform(node, config, writer);
            LOG.info("Successfully writing {}.", outputFile.toString());
        } catch (TransformerException ioe) {
            throw ioe;
        }
    }

    private static void transform(Node node, DocumentOutputFormatConfiguration config, Writer writer) throws TransformerException {
        Transformer transformer = getTransformer(config);
        transformer.transform(new DOMSource(node), new StreamResult(writer));
    }

    private static Transformer getTransformer(DocumentOutputFormatConfiguration config) {
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();

            // define settings
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                    config.omitXMLDeclaration() ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.METHOD, config.getMethod());
            transformer.setOutputProperty(OutputKeys.INDENT,
                    config.getIndention() > 0 ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, config.getEncoding());
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                    Integer.toString(config.getIndention()));

            return transformer;
        } catch (TransformerConfigurationException e) {
            LOG.fatal("Something strange happened. Cannot create a default tranformer object!", e);
            return null;
        }
    }
}
