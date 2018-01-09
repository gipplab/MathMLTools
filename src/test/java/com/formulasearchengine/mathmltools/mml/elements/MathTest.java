package com.formulasearchengine.mathmltools.mml.elements;


import static com.formulasearchengine.mathmltools.mathmlquerygenerator.QVarXQueryGeneratorTest.getFileContents;
import static com.formulasearchengine.mathmltools.mml.CMMLInfoTest.MML_TEST_DIR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.xml.sax.SAXException;

class MathTest {
    @Test
    void isValid() throws Exception {
        final String sampleMML = getFileContents(MML_TEST_DIR + "Emc2.xml");
        Math math = new Math(sampleMML);
        assertThat(math.getValidationProblems().spliterator().getExactSizeIfKnown(), is(equalTo(0L)));
        //assertTrue(math.isValid());
    }

    @ParameterizedTest()
    @ValueSource(strings = {
            "<!DOCTYPE math PUBLIC \"-//W3C//DTD MATHML 3.0 Transitional//EN\" \n"
                    + "     \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\">\n"
                    + "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n"
                    + "     <ci>some content 1</ci>\n"
                    + "</math>",
            "<math/>",
            "<math><mi>a</mi></math>"})
    void noWhitespaceTests(String tag) throws Exception {
        new Math(tag);
    }

    @Test
    void EmcTest() throws IOException, ParserConfigurationException, SAXException {
        final String sampleMML = getFileContents(MML_TEST_DIR + "Emc2.xml");
        new Math(sampleMML);
    }

    @ParameterizedTest()
    @ValueSource(strings = {
            "<!DOCTYPE math PUBLIC \"-//W3C//DTD MATHML 3.0 Transitional//EN\" \n"
                    + "     \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\">\n"
                    + "<?xml version=\"1.0\"?>"
                    + "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n"
                    + "     <ci>some content 1</ci>\n"
                    + "</math>",
            "<ns:math/>",
            "<math><XMLDocument >a</XMLDocument></math>"})
    void failingExamples(String tag) {
        assertThrows(Exception.class, () -> new Math(tag));
    }
}

/*
Isolating test for debugging purposes
    @Test
    public void isolatedTest() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(true);
        dbf.setFeature("http://xml.org/sax/features/validation", true);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        Input.Builder builder = Input.fromString(
                "<!DOCTYPE math PUBLIC \"-//W3C//DTD MATHML 3.0 Transitional//EN\" \n"
                        + "     \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\">\n"
                        + "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n"
                        + "     <ci>some content 1</ci>\n"
                        + "</math>");

        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new PartialLocalEntityResolver());
        db.parse(toInputSource(builder.build()));
    }
 */