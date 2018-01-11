package com.formulasearchengine.mathosphere.pomlp.xml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import static com.formulasearchengine.mathmltools.mml.CMMLInfoTest.MML_TEST_DIR;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

class XmlDocumentReaderTest {

    @ParameterizedTest
    @MethodSource("mmlResources")
    void testResources(Path f) {
        final Document xml = XmlDocumentReader.oldgetDocumentFromXML(f);
        assertNotNull(xml);
    }

    /**
     * Defines the range of the tests
     *
     * @return
     */
    private static Stream<Path> mmlResources() throws IOException {
        final URL url = XmlDocumentReaderTest.class.getClassLoader().getResource(MML_TEST_DIR);
        return Files.walk(Paths.get(url.getFile()))
                .filter(Files::isRegularFile)
                .filter(f -> {
                    String n = f.getFileName().toString();
                    return n.endsWith("mml") || n.endsWith("xml");
                });
    }
}