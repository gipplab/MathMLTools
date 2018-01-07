package com.formulasearchengine.mathmltools.mml.elements;


import static com.formulasearchengine.mathmltools.mathmlquerygenerator.QVarXQueryGeneratorTest.getFileContents;
import static com.formulasearchengine.mathmltools.mml.CMMLInfoTest.MML_TEST_DIR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.xml.parsers.ParserConfigurationException;

class MathTest {

    @ParameterizedTest()
    @ValueSource(strings = {"<math/>", "<math><mi>a</mi></math>"})
    void noWhitespaceTests(String tag) throws Exception {
        Math math = new Math(tag);
        assertEquals(tag, math.toString().replaceAll("\\s*", ""));
    }

    @Test
    void EmcTest() throws IOException, ParserConfigurationException {
        final String sampleMML = getFileContents(MML_TEST_DIR + "Emc2.xml");
        Math math = new Math(sampleMML);
        assertThat(sampleMML, isIdenticalTo(math.getInfoObject()).ignoreWhitespace());
    }
}