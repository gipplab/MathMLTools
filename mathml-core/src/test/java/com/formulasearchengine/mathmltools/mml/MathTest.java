package com.formulasearchengine.mathmltools.mml;

import com.formulasearchengine.mathmltools.helper.XMLHelper;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.lang3.NotImplementedException;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MathTest {
    public static final String SIMPLE_WITH_DOCTYPE = "<!DOCTYPE math PUBLIC \"-//W3C//DTD MATHML 3.0 Transitional//EN\" \n"
            + "     \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\">\n"
            + "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n"
            + "     <ci>some content 1</ci>\n"
            + "</math>";

    public static final String TEST_DIR = "com/formulasearchengine/mathmltools/mml/tests/";
    private static final String PID_E = "p1.1.m1.1.1";
    private static final String PID_m = "p1.1.m1.1.3";
    private static final String PID_k1 = "p1.1.m1.1.5";
    private static final String PID_k2 = "p1.1.m1.1.9.1";
    private static final String PID_k3 = "p1.1.m1.1.11";


    static public String getFileContents(String fname) throws IOException {
        try (InputStream is = MathTest.class.getClassLoader().getResourceAsStream(fname)) {
            final Scanner s = new Scanner(is, "UTF-8");
            //Stupid scanner tricks to read the entire file as one token
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

    @Test
    void isValid() throws Exception {
        final String sampleMML = getFileContents(TEST_DIR + "Emc2.mml");
        MathDoc math = new MathDoc(sampleMML);
        assertThat(math.getValidationProblems().spliterator().getExactSizeIfKnown(), is(equalTo(0L)));
        //assertTrue(math.isValid());
    }

    @ParameterizedTest()
    @ValueSource(strings = {
            SIMPLE_WITH_DOCTYPE,
            "<math/>",
            "<math><mi>a</mi></math>"})
    void noWhitespaceTests(String tag) throws Exception {
        new MathDoc(tag);
    }

    @Test
    void EmcTest() throws IOException, ParserConfigurationException, SAXException {
        final String sampleMML = getFileContents(TEST_DIR + "Emc2.mml");
        new MathDoc(sampleMML);
    }

    @Test
    void altTest() throws IOException, ParserConfigurationException, SAXException {
        final String sampleMML = getFileContents(TEST_DIR + "measurable-space.mml");
        final MathDoc math = new MathDoc(sampleMML);
        math.changeTeXAnnotation("asdf");
        assertThat(math.toString(), new StringContains("alttext=\"asdf\""));
        assertThat(math.toString(), StringContains.containsString("asdf</annotation>"));
    }

    @Test
    void cdRewriter() throws IOException, ParserConfigurationException, SAXException {
        final String sampleMML = getFileContents(TEST_DIR + "Van_der_Waerden.mml");
        final MathDoc math = new MathDoc(sampleMML);
        math.fixGoldCd();
        assertThat(math.toString(), new StringContains("cd=\"wikidata\""));
    }

    @Test
    void latexMLMacroExtracter() throws IOException, ParserConfigurationException, SAXException {
        final String sampleMML = getFileContents(TEST_DIR + "measurable-space.mml");
        final MathDoc math = new MathDoc(sampleMML);
        math.fixGoldCd();
    }

    @Test
    void altTestFail() throws IOException, ParserConfigurationException, SAXException {
        final MathDoc math = new MathDoc(SIMPLE_WITH_DOCTYPE);
        assertThrows(NotImplementedException.class, () -> math.changeTeXAnnotation("asdf"));
    }

    @Test()
    void parsingError() {
        assertThrows(Exception.class, () -> new MathDoc("Test"));
    }


    @ParameterizedTest()
    @ValueSource(strings = {
            "<!DOCTYPE math PUBLIC \"-//W3C//DTD MATHML 3.0 Transitional//EN\" \n"
                    + "     \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\">\n"
                    + "<?xml version=\"1.0\"?>"
                    + "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n"
                    + "     <cx>some content 1</cx>\n"
                    + "</math>",
            "<ns:mathi/>",
            "<math><XMLDocument >a</XMLDocument></math>"})
    void failingExamples(String tag) {
        assertThrows(Exception.class, () -> new MathDoc(tag));
    }

    private void isHighlighted(MathDoc mml, String id) throws XPathExpressionException {
        final Element element = (Element) XMLHelper.getElementById(mml.getDom(), id);
        isHighlighted(element);
    }

    private void isNotHighlighted(MathDoc mml, String id) throws XPathExpressionException {
        final Element element = (Element) XMLHelper.getElementById(mml.getDom(), id);
        isNotHighlighted(element);
    }

    private void isHighlighted(Element element) {
        assertEquals("highlightedIdentifier", element.getAttribute("class"));
    }

    private void isNotHighlighted(Element element) {
        assertFalse(element.hasAttribute("class"));
    }

    @Test
    void HighlightMTest() throws Exception {
        final String sampleMML = getFileContents(TEST_DIR + "Emc2.mml");
        final MathDoc mml = new MathDoc(sampleMML);
        final int mHash = "E".hashCode();
        final ArrayList<Integer> toHighlight = new ArrayList<>();
        toHighlight.add(mHash);
        mml.highlightConsecutiveIdentifiers(toHighlight, false);
        isHighlighted(mml, PID_E);
        isNotHighlighted(mml, PID_m);
    }

    @Test
    void HighlightMTestFirstk() throws Exception {
        final String sampleMML = getFileContents(TEST_DIR + "Van_der_Waerden_CI.mml");
        final MathDoc mml = new MathDoc(sampleMML);
        final int kHash = "Q12503".hashCode();
        final ArrayList<Integer> toHighlight = new ArrayList<>();
        //toHighlight.add(WHash);
        toHighlight.add(kHash);
        mml.highlightConsecutiveIdentifiers(toHighlight, false);
        isHighlighted(mml, PID_k1);
        isNotHighlighted(mml, PID_k2);
        isNotHighlighted(mml, PID_k3);
    }

    @Test
    void HighlightMTestLastk() throws Exception {
        final String sampleMML = getFileContents(TEST_DIR + "Van_der_Waerden_CI.mml");
        final MathDoc mml = new MathDoc(sampleMML);
        final int kHash = "Q12503".hashCode();
        final ArrayList<Integer> toHighlight = new ArrayList<>();
        toHighlight.add(kHash);
        mml.highlightConsecutiveIdentifiers(toHighlight, true);
        isNotHighlighted(mml, PID_k1);
        isNotHighlighted(mml, PID_k2);
        isHighlighted(mml, PID_k3);
    }

    @Test
    void HighlightMTestFirst2k() throws Exception {
        final String sampleMML = getFileContents(TEST_DIR + "Van_der_Waerden_CI.mml");
        final MathDoc mml = new MathDoc(sampleMML);
        final int WHash = "Q7913892".hashCode();
        final int kHash = "Q12503".hashCode();
        final ArrayList<Integer> toHighlight = new ArrayList<>();
        toHighlight.add(WHash);
        toHighlight.add(kHash);
        toHighlight.add(kHash);
        mml.highlightConsecutiveIdentifiers(toHighlight, true);
        isHighlighted(mml, PID_k1);
        isHighlighted(mml, PID_k2);
        isNotHighlighted(mml, PID_k3);
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
