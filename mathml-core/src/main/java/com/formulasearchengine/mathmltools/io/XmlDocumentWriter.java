package com.formulasearchengine.mathmltools.io;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Andre Greiner-Petter
 */
public class XmlDocumentWriter {

    private XmlDocumentWriter() {
    }

    public static String stringify(Document doc) throws IOException {
        OutputFormat format = new OutputFormat(doc);
        format.setIndenting(true);
        format.setIndent(2);
        format.setOmitXMLDeclaration(false);
        format.setLineWidth(Integer.MAX_VALUE);

        Writer outxml = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(outxml, format);
        serializer.serialize(doc);
        return outxml.toString();
    }

    public static void writeToFile(Document doc, Path outputFile) throws IOException {
        String stringify = stringify(doc);
        if (Files.notExists(outputFile)) {
            Files.createFile(outputFile);
        }
        Files.write(outputFile, stringify.getBytes());
    }

}
