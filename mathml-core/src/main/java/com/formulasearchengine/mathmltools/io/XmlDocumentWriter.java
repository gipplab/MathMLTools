package com.formulasearchengine.mathmltools.io;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

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

}
