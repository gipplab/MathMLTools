package com.formulasearchengine.mathmltools.io;

import com.formulasearchengine.mathmltools.mml.MathTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class XmlDocumentReaderTest {

    @Test
    public void simpleDocTest(){
        try {
            Document xml = XmlDocumentReader.parse(MathTest.SIMPLE_WITH_DOCTYPE);
            assertNotNull(xml);
        } catch (Exception e){
            fail("Parsing document throws an exception.", e);
        }
    }

    @Test
    public void simpleDocParallelTest() throws IOException {
        mmlResources().parallel().forEach(p -> {
            try {
                XmlDocumentReader.parse(p, true);
            } catch (Exception e) {
                fail("Parsing document throws an exception.", e);
            }
        });
    }

    @Test
    public void invalidTestWithNoValidation() {
        assertThrows(SAXException.class, () -> XmlDocumentReader.parse("<open><open2></open2>", false));
    }

    @Test
    public void singleNodeTest() throws IOException, SAXException {
        assertNotNull(XmlDocumentReader.parse("<simple />", false));
    }

    @ParameterizedTest
    @MethodSource("mmlResources")
    void validateMMLPaths(Path p) {
        try {
            Document xml = XmlDocumentReader.parse(p);
            assertNotNull(xml);
        } catch (Exception e){
            fail("Parsing document throws an exception.", e);
        }
    }

    @ParameterizedTest
    @MethodSource("mmlResources")
    void validateMMLStrings(Path p) throws IOException {
        final String s = new String(Files.readAllBytes(p));
        try {
            Node xml = XmlDocumentReader.parseToNode(s);
            assertNotNull(xml);
        } catch (Exception e){
            fail("Parsing document throws an exception.", e);
        }
    }

    @ParameterizedTest
    @MethodSource("xmlResources")
    void validateInvalidXMLPaths(Path p) {
        assertThrows(SAXException.class, () -> XmlDocumentReader.parse(p));
    }

    @ParameterizedTest
    @MethodSource("xmlResources")
    void loadXMLWithoutValidation(Path p) {
        try {
            Document xml = XmlDocumentReader.parse(p, false);
            assertNotNull(xml);
        } catch (Exception e){
            fail("Parsing document throws an exception.", e);
        }
    }

    @ParameterizedTest
    @MethodSource("otherResources")
    void loadInvalidInput(Path p) {
        assertThrows(SAXException.class, () -> XmlDocumentReader.parse(p));
    }

    private static Stream<Path> xmlResources() throws IOException {
        return fileResources().filter( p -> p.toString().endsWith("xml") );
    }

    private static Stream<Path> mmlResources() throws IOException {
        return fileResources().filter( p -> p.toString().endsWith("mml") );
    }

    private static Stream<Path> otherResources() throws IOException {
        return fileResources().filter( p -> !(p.toString().endsWith("ml")) );
    }

    private static Stream<Path> fileResources() throws IOException {
        final URL url = XmlDocumentReaderTest.class.getClassLoader().getResource(MathTest.TEST_DIR);
        return Files.walk(Paths.get(url.getFile()))
                .filter(Files::isRegularFile);
    }
}