package com.formulasearchengine.mathmltools.xmlhelper;

import com.formulasearchengine.mathmltools.mml.elements.MathDoc;
import com.formulasearchengine.mathmltools.mml.elements.MathTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import static com.formulasearchengine.mathmltools.mml.CMMLInfoTest.MML_TEST_DIR;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

class XmlDocumentReaderTest {

    private static boolean filterXml(Path f) {
        String n = f.getFileName().toString();
        return n.endsWith("mml") || n.endsWith("xml");
    }

    @ParameterizedTest
    @MethodSource("xmlResources")
    void testResources(Path f) {
        final Document xml = XmlDocumentReader.oldgetDocumentFromXML(f);
        assertNotNull(xml);
    }

    @Test
    void simpleDocTest(){
        final Document document = XmlDocumentReader.getDocumentFromXMLString(MathTest.SIMPLE_WITH_DOCTYPE);
        assertNotNull(document);
    }

    @ParameterizedTest
    @MethodSource("xmlResources")
    void testNewResouces(Path f) {
        final Document xml = XmlDocumentReader.getDocumentFromXML(f);
        assertNotNull(xml);
    }

    @ParameterizedTest
    @MethodSource("xmlResources")
    void testNewResoucesNode(Path f) {
        final Node xml = XmlDocumentReader.getNodeFromXML(f);
        assertNotNull(xml);
    }

    @ParameterizedTest
    @MethodSource("xmlResources")
    void testResourcesAsString(Path f) throws IOException {
        String s = new String(Files.readAllBytes(f));
        s = MathDoc.tryFixHeader(s);
        final Document xml = XmlDocumentReader.getDocumentFromXMLString(s);
        assertNotNull(xml);
    }

    @ParameterizedTest
    @MethodSource("otherResources")
    void testOtherResourcesAsString(Path f) throws IOException {
        final String s = new String(Files.readAllBytes(f));
        final Document xml = XmlDocumentReader.getDocumentFromXMLString(s);
        assertNull(xml);
    }

    @ParameterizedTest
    @MethodSource("otherResources")
    void testOtherResources(Path f) throws IOException {
        final Document xml = XmlDocumentReader.getDocumentFromXML(f);
        assertNull(xml);
    }



    private static Stream<Path> xmlResources() throws IOException {
        return fileResources()
                .filter(XmlDocumentReaderTest::filterXml);
    }

    private static Stream<Path> fileResources() throws IOException {
        final URL url = XmlDocumentReaderTest.class.getClassLoader().getResource(MML_TEST_DIR);
        return Files.walk(Paths.get(url.getFile()))
                .filter(Files::isRegularFile);
    }

    private static Stream<Path> otherResources() throws IOException {
        return fileResources().filter(f->!filterXml(f));
    }
}