package com.formulasearchengine.mathmltools.io;

import com.formulasearchengine.mathmltools.mml.MathDoc;
import com.google.common.base.Charsets;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

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

    public static String stringify(MathDoc mathDoc) throws IOException {
        return stringify(mathDoc.getDom());
    }

    public static String stringify(MathDoc mathDoc, DocumentOutputFormatConfiguration config) throws IOException {
        return stringify(mathDoc.getDom(), config);
    }

    public static String stringify(Document doc) throws IOException {
        return stringify(doc, new DocumentOutputFormatConfiguration());
    }

    public static String stringify(Document doc, DocumentOutputFormatConfiguration config)
            throws IOException {
        OutputFormat format = configerOutputFormat(doc, config);

        LOG.trace("Start serializing document as String.");
        Writer outxml = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(outxml, format);
        serializer.serialize(doc);
        LOG.debug("Successfully serialized document to string.");
        return outxml.toString();
    }

    public static void writeToFile(MathDoc mathDoc, Path outputFile) throws IOException {
        writeToFile(mathDoc.getDom(), outputFile);
    }

    public static void writeToFile(MathDoc mathDoc, Path outputFile, DocumentOutputFormatConfiguration config) throws IOException {
        writeToFile(mathDoc.getDom(), outputFile, config);
    }

    public static void writeToFile(Document doc, Path outputFile)
            throws IOException {
        writeToFile(doc, outputFile, new DocumentOutputFormatConfiguration());
    }

    public static void writeToFile(Document doc, Path outputFile, DocumentOutputFormatConfiguration config)
            throws IOException {
        // throws exceptions if file already exists.
        Files.createFile(outputFile);

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile.toFile()), Charsets.UTF_8)) {
            LOG.debug("Start writing file {}...", outputFile.getFileName());
            OutputFormat format = configerOutputFormat(doc, config);
            XMLSerializer serializer = new XMLSerializer(writer, format);
            serializer.serialize(doc);
            LOG.info("Successfully writing {}.", outputFile.toString());
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    private static OutputFormat configerOutputFormat(Document doc, DocumentOutputFormatConfiguration config) {
        LOG.trace("Configure output format.");
        OutputFormat format = new OutputFormat(doc);
        if (config.getIndention() > 0) {
            format.setIndenting(true);
            format.setIndent(config.getIndention());
        } else {
            format.setIndenting(false);
        }

        format.setOmitXMLDeclaration(config.omitXMLDeclaration());
        format.setOmitDocumentType(config.omitDoctype());
        format.setOmitComments(config.omitComments());

        format.setLineWidth(config.getMaxLineWidth());
        format.setLineSeparator(config.getLineSeperator());

        format.setMediaType(config.getMediaType());
        return format;
    }
}
