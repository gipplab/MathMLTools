package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.converters.latexml.LaTeXMLConverterTest;
import com.formulasearchengine.mathmltools.converters.latexml.LateXMLConfig;
import com.formulasearchengine.mathmltools.converters.mathoid.MathoidConfig;
import com.formulasearchengine.mathmltools.converters.mathoid.MathoidConverterTest;
import com.formulasearchengine.mathmltools.converters.error.MathConverterException;
import com.formulasearchengine.mathmltools.helper.XMLHelper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;

/**
 * @author Vincent Stange
 */
public class MathMLConverterTest {

    // TODO: Failed with "enriched mathml transformation failed" message
    @Disabled
    @Test
    public void convertPmml() throws Exception, MathConverterException {
        // prepare configuration and objects
        Document mathNode = XMLHelper.string2Doc(getResourceContent("mathml_pmml.txt"), true);
        MathMLConverterConfig mathConfig = new MathMLConverterConfig().setMathoid(new MathoidConfig().setActive(true).setUrl(MathoidConverterTest.HTTP_MATHOID_TEXT));
        MathMLConverter mathMLConverter = new MathMLConverter(mathConfig);
        // test
        String result = mathMLConverter.convertPmml((Element) mathNode.getFirstChild());
        assertThat(result, is(getResourceContent("mathml_pmml_expected.txt")));
    }

    @Test
    public void transform_latex_1() throws Exception, MathConverterException {
        // prepare configuration and objects
        Document formulaNode = XMLHelper.string2Doc(getResourceContent("mathml_latex_1.txt"), true);
        MathMLConverterConfig mathConfig = new MathMLConverterConfig().setLatexml(LateXMLConfig.getDefaultConfiguration().setUrl(LaTeXMLConverterTest.HTTP_LATEXML_TEST));
        MathMLConverter mathMLConverter = new MathMLConverter(mathConfig);
        // test
        String result = mathMLConverter.transform((Element) formulaNode.getFirstChild());
        assertThat(result, equalToIgnoringWhiteSpace(getResourceContent("mathml_latex_1_expected.txt")));
    }

    @Test
    public void transform_latex_2() throws Exception {
        // prepare configuration and objects
        Document formulaNode = XMLHelper.string2Doc(getResourceContent("mathml_latex_2.txt"), true);
        MathMLConverterConfig mathConfig = new MathMLConverterConfig().setLatexml(LateXMLConfig.getDefaultConfiguration().setUrl(LaTeXMLConverterTest.HTTP_LATEXML_TEST));
        MathMLConverter mathMLConverter = new MathMLConverter(mathConfig);
        // test

        Assertions.assertThrows(
                MathConverterException.class,
                () -> mathMLConverter.transform((Element) formulaNode.getFirstChild())
        );
//        String result = mathMLConverter.transform((Element) formulaNode.getFirstChild());
//        assertThat(result, is(getResourceContent("mathml_latex_2_expected.txt")));
    }

    @Test
    public void transform_mathml_1() throws Exception, MathConverterException {
        Document formulaNode = XMLHelper.string2Doc(getResourceContent("mathml_perfect_1.txt"), true);
        MathMLConverter mathMLConverter = new MathMLConverter();
        String result = mathMLConverter.transform((Element) formulaNode.getFirstChild());
        assertThat(result, equalToIgnoringWhiteSpace(getResourceContent("mathml_perfect_1_expected.txt")));
    }


    @Test
    public void consolidateNamespace_m() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc("<m:math xmlns:m=\"http://www.w3.org/1998/Math/MathML\">\n" +
                "<m:mrow>Test</m:mrow>\n" +
                "</m:math>", false);
        Element result = new MathMLConverter().consolidateMathMLNamespace((Element) mathNode.getFirstChild());
        String actual = XMLHelper.printDocument(result);
        assertThat(actual, equalToIgnoringWhiteSpace("<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n<mrow>Test</mrow>\n</math>"));
    }

    @Test
    public void consolidateNamespace_mml() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc("<mml:math xmlns:mml=\"http://www.w3.org/1998/Math/MathML\">\n" +
                "<mml:mrow><mml:mi>%</mml:mi></mml:mrow>\n" +
                "</mml:math>", false);
        Element result = new MathMLConverter().consolidateMathMLNamespace((Element) mathNode.getFirstChild());
        String actual = XMLHelper.printDocument(result);
        assertThat(actual, is("<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
                "   <mrow>\n" +
                "      <mi>%</mi>\n" +
                "   </mrow>\n" +
                "</math>"));
    }


    @Test
    public void scanFormulaNode_pmml() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc("<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
                "<mrow><mi>%</mi></mrow></math>", false);
        MathMLConverter.Content result = new MathMLConverter().scanFormulaNode((Element) mathNode.getFirstChild());
        assertThat(result, is(MathMLConverter.Content.pmml));
    }

    @Test
    public void scanFormulaNode_pmml_2() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc("<formula xmlns=\"http://www.w3.org/1998/Math/MathML\" rend=\"display\">\n" +
                "   <math xmlns:mml=\"http://www.w3.org/1998/Math/MathML\"\n" +
                "         xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
                "         id=\"m1\">\n" +
                "      <mrow>\n" +
                "         <mi mathvariant=\"italic\">Circularity</mi>\n" +
                "         <mo>=</mo>\n" +
                "         <mn>4</mn>\n" +
                "         <mo>π</mo>\n" +
                "         <mo>×</mo>\n" +
                "         <mfrac>\n" +
                "            <mrow>\n" +
                "               <mi mathvariant=\"italic\">Area</mi>\n" +
                "            </mrow>\n" +
                "            <mrow>\n" +
                "               <msup>\n" +
                "                  <mi mathvariant=\"italic\">Perimeter</mi>\n" +
                "                  <mn>2</mn>\n" +
                "               </msup>\n" +
                "            </mrow>\n" +
                "         </mfrac>\n" +
                "      </mrow>\n" +
                "   </math>\n" +
                "</formula>", false);
        MathMLConverter.Content result = new MathMLConverter().scanFormulaNode((Element) mathNode.getFirstChild());
        assertThat(result, is(MathMLConverter.Content.pmml));
    }

    @Test
    public void scanFormulaNode_cmml() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc("<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
                "<apply><times/><ci>x</ci><ci>y</ci></apply></math>", false);
        MathMLConverter.Content result = new MathMLConverter().scanFormulaNode((Element) mathNode.getFirstChild());
        assertThat(result, is(MathMLConverter.Content.cmml));
    }

    @Test
    public void scanFormulaNode_mathml() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc(getResourceContent("mathml_perfect_1.txt"), true);
        MathMLConverter.Content result = new MathMLConverter().scanFormulaNode((Element) mathNode.getFirstChild());
        assertThat(result, is(MathMLConverter.Content.mathml));
    }

    @Test
    public void scanFormulaNode_mathml_example_1() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc("<m:math xmlns:m=\"http://www.w3.org/1998/Math/MathML\" alttext=\"N_{n}^{+}\" display=\"inline\"><m:semantics><m:msubsup id=\"m1.4\" xref=\"m1.4.cmml\"><m:mi id=\"m1.1\" xref=\"m1.1.cmml\">N</m:mi><m:mi id=\"m1.2\" xref=\"m1.2.cmml\">n</m:mi><m:mo id=\"m1.3\" xref=\"m1.3.cmml\">+</m:mo></m:msubsup><m:annotation-xml encoding=\"MathML-Content\"><m:apply id=\"m1.4.cmml\" xref=\"m1.4\"><m:csymbol cd=\"ambiguous\" id=\"m1.4.1.cmml\" xref=\"m1.4\">superscript</m:csymbol><m:apply id=\"m1.4.3.cmml\" xref=\"m1.4\"><m:csymbol cd=\"ambiguous\" id=\"m1.4.2.cmml\" xref=\"m1.4\">subscript</m:csymbol><m:ci id=\"m1.1.cmml\" xref=\"m1.1\">\uD835\uDC41</m:ci><m:ci id=\"m1.2.cmml\" xref=\"m1.2\">\uD835\uDC5B</m:ci></m:apply><m:plus id=\"m1.3.cmml\" xref=\"m1.3\"/></m:apply></m:annotation-xml></m:semantics></m:math>", true);
        MathMLConverter.Content result = new MathMLConverter().scanFormulaNode((Element) mathNode.getFirstChild());
        assertThat(result, is(MathMLConverter.Content.mathml));
    }

    @Test
    public void scanFormulaNode_mathml_example_2() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc("<m:math xmlns:m=\"http://www.w3.org/1998/Math/MathML\" alttext=\"\\phi_{5}\" display=\"inline\"><m:semantics><m:apply id=\"p10.m1.4\" xref=\"p10.m1.4.pmml\"><m:csymbol cd=\"ambiguous\" id=\"p10.m1.1\" xref=\"p10.m1.4.pmml\">subscript</m:csymbol><m:ci id=\"p10.m1.2\" xref=\"p10.m1.2.pmml\">italic-ϕ</m:ci><m:cn id=\"p10.m1.3\" type=\"integer\" xref=\"p10.m1.3.pmml\">5</m:cn></m:apply><m:annotation-xml encoding=\"MathML-Presentation\"><m:msub id=\"p10.m1.4.pmml\" xref=\"p10.m1.4\"><m:mi id=\"p10.m1.2.pmml\" xref=\"p10.m1.2\">ϕ</m:mi><m:mn id=\"p10.m1.3.pmml\" xref=\"p10.m1.3\">5</m:mn></m:msub></m:annotation-xml><m:annotation encoding=\"application/x-tex\">\\phi_{5}</m:annotation></m:semantics></m:math>", true);
        MathMLConverter.Content result = new MathMLConverter().scanFormulaNode((Element) mathNode.getFirstChild());
        assertThat(result, is(MathMLConverter.Content.mathml));
    }

    @Test
    public void scanFormulaNode_mathml_example_3() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc("<m:math xmlns:m=\"http://www.w3.org/1998/Math/MathML\" alttext=\"\\square\" display=\"inline\"><m:semantics><m:ci id=\"p65.m3.1\" xref=\"p65.m3.1.pmml\">□</m:ci><m:annotation-xml encoding=\"MathML-Presentation\"><m:mi id=\"p65.m3.1.pmml\" mathvariant=\"normal\" xref=\"p65.m3.1\">□</m:mi></m:annotation-xml><m:annotation encoding=\"application/x-tex\">\\square</m:annotation></m:semantics></m:math>", true);
        MathMLConverter.Content result = new MathMLConverter().scanFormulaNode((Element) mathNode.getFirstChild());
        assertThat(result, is(MathMLConverter.Content.mathml));
    }

    @Test
    public void scanFormulaNode_LaTeXML_example() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc("<m:math xmlns:m=\"http://www.w3.org/1998/Math/MathML\" alttext=\"N_{n}^{+}\" display=\"inline\"><m:semantics><m:msubsup id=\"m1.4\" xref=\"m1.4.cmml\"><m:mi id=\"m1.1\" xref=\"m1.1.cmml\">N</m:mi><m:mi id=\"m1.2\" xref=\"m1.2.cmml\">n</m:mi><m:mo id=\"m1.3\" xref=\"m1.3.cmml\">+</m:mo></m:msubsup><m:annotation-xml encoding=\"MathML-Content\"><m:apply id=\"m1.4.cmml\" xref=\"m1.4\"><m:csymbol cd=\"ambiguous\" id=\"m1.4.1.cmml\" xref=\"m1.4\">superscript</m:csymbol><m:apply id=\"m1.4.3.cmml\" xref=\"m1.4\"><m:csymbol cd=\"ambiguous\" id=\"m1.4.2.cmml\" xref=\"m1.4\">subscript</m:csymbol><m:ci id=\"m1.1.cmml\" xref=\"m1.1\">\uD835\uDC41</m:ci><m:ci id=\"m1.2.cmml\" xref=\"m1.2\">\uD835\uDC5B</m:ci></m:apply><m:plus id=\"m1.3.cmml\" xref=\"m1.3\"/></m:apply></m:annotation-xml></m:semantics></m:math>", true);
        MathMLConverter.Content result = new MathMLConverter().scanFormulaNode((Element) mathNode.getFirstChild());
        assertThat(result, is(MathMLConverter.Content.mathml));
    }

    @Test
    public void scanFormulaNode_latex() throws Exception, MathConverterException {
        Document mathNode = XMLHelper.string2Doc("<mml:math xmlns:mml=\"http://www.w3.org/1998/Math/MathML\">\n" +
                "a+b\n" +
                "</mml:math>", false);
        MathMLConverter.Content result = new MathMLConverter().scanFormulaNode((Element) mathNode.getFirstChild());
        assertThat(result, is(MathMLConverter.Content.latex));
    }


    @Test
    public void canonilize() throws Exception, MathConverterException {
        // prepare and execute
        String actualMathML = new MathMLConverter().canonicalize(getResourceContent("mathml_canonilize_1.xml"));
        String expected = getResourceContent("mathml_canonilize_expected.xml");
        // they should be equal - if not, the UnaryOperatorRemover is active and this should not be!
        assertThat(actualMathML, equalToIgnoringWhiteSpace(expected));
    }


    @Test
    public void verifyMathML() throws Exception, MathConverterException {
        String shouldbeOkay = new MathMLConverter().verifyMathML(getResourceContent("canonicalize/mathml_real_1_test.xml"));
        assertThat(shouldbeOkay, notNullValue());
    }

    @Test
    public void verifyMathML_exception() {
        // test empty document
        Assertions.assertThrows(
                MathConverterException.class,
                () -> new MathMLConverter().verifyMathML("<math />")
        );
    }

    private String getResourceContent(String resourceFilename) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(resourceFilename), "UTF-8");
    }

}