package com.formulasearchengine.mathmltools.similarity.similarity.util;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Vincent Stange
 */
public class CMMLHelperTest {

    @Test
    public void getFirstApplyNode() throws IOException, ParserConfigurationException, XPathExpressionException {
        // presentation mml with content semantics
        assertThat(CMMLHelper.getFirstApplyNode(new CMMLInfo(getResourceFile("mathml_apply_1.xml"))), notNullValue());
        // content mml with presentation semantics
        assertThat(CMMLHelper.getFirstApplyNode(new CMMLInfo(getResourceFile("mathml_apply_2.xml"))), notNullValue());
        // only content mml
        assertThat(CMMLHelper.getFirstApplyNode(new CMMLInfo(getResourceFile("mathml_apply_3.xml"))), notNullValue());
        // only presentation mml -> should be null
        assertThat(CMMLHelper.getFirstApplyNode(new CMMLInfo(getResourceFile("mathml_apply_4.xml"))), nullValue());
    }

    @Test
    public void getStrictCmml() throws Exception {
        String mathml = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\" id=\"p1.1.m1.1\" class=\"ltx_Math\" alttext=\"\\EulerGamma@{z}=\\Int{0}{\\infty}@{t}{e^{-t}t^{z-1}}\" display=\"inline\">\n" +
                "  <semantics id=\"p1.1.m1.1a\">\n" +
                "    <mrow id=\"p1.1.m1.1.4\" xref=\"p1.1.m1.1.4.cmml\">\n" +
                "      <mrow id=\"p1.1.m1.1.1.8.2\" xref=\"p1.1.m1.1.1.1.cmml\">\n" +
                "        <mpadded width=\"-1.7pt\" id=\"p1.1.m1.1.1.2\" xref=\"p1.1.m1.1.1.1.cmml\">\n" +
                "          <mi mathvariant=\"normal\" id=\"p1.1.m1.1.1.2.1.2\" xref=\"p1.1.m1.1.1.2.1.1.cmml\">Γ</mi>\n" +
                "        </mpadded>\n" +
                "        <mo id=\"p1.1.m1.1.1.8.2a\" xref=\"p1.1.m1.1.1.1.cmml\">\u2061</mo>\n" +
                "        <mrow id=\"p1.1.m1.1.1.8.2.1\" xref=\"p1.1.m1.1.1.1.cmml\">\n" +
                "          <mo id=\"p1.1.m1.1.1.5\" xref=\"p1.1.m1.1.1.1.cmml\">(</mo>\n" +
                "          <mi id=\"p1.1.m1.1.1.6\" xref=\"p1.1.m1.1.1.6.cmml\">z</mi>\n" +
                "          <mo id=\"p1.1.m1.1.1.7\" xref=\"p1.1.m1.1.1.1.cmml\">)</mo>\n" +
                "        </mrow>\n" +
                "      </mrow>\n" +
                "      <mo id=\"p1.1.m1.1.2\" xref=\"p1.1.m1.1.2.cmml\">=</mo>\n" +
                "      <mrow id=\"p1.1.m1.1.3.8\" xref=\"p1.1.m1.1.3.1.cmml\">\n" +
                "        <msubsup id=\"p1.1.m1.1.3.2.1.6\" xref=\"p1.1.m1.1.3.2.1.1.cmml\">\n" +
                "          <mo largeop=\"true\" symmetric=\"true\" id=\"p1.1.m1.1.3.2.1.2\" xref=\"p1.1.m1.1.3.2.1.1.1.cmml\">∫</mo>\n" +
                "          <mn id=\"p1.1.m1.1.3.2.1.3.1\" xref=\"p1.1.m1.1.3.2.1.3.1.cmml\">0</mn>\n" +
                "          <mi mathvariant=\"normal\" id=\"p1.1.m1.1.3.2.1.4.1\" xref=\"p1.1.m1.1.3.2.1.4.1.cmml\">∞</mi>\n" +
                "        </msubsup>\n" +
                "        <mo id=\"p1.1.m1.1.3.8a\" xref=\"p1.1.m1.1.3.1.cmml\">\u2061</mo>\n" +
                "        <mrow id=\"p1.1.m1.1.3.8.1\" xref=\"p1.1.m1.1.3.1.cmml\">\n" +
                "          <mpadded width=\"+1.7pt\" id=\"p1.1.m1.1.3.4\" xref=\"p1.1.m1.1.3.4.cmml\">\n" +
                "            <mrow id=\"p1.1.m1.1.3.4a\" xref=\"p1.1.m1.1.3.4.cmml\">\n" +
                "              <msup id=\"p1.1.m1.1.3.4.6\" xref=\"p1.1.m1.1.3.4.6.cmml\">\n" +
                "                <mi id=\"p1.1.m1.1.3.4.1\" xref=\"p1.1.m1.1.3.4.1.cmml\">e</mi>\n" +
                "                <mrow id=\"p1.1.m1.1.3.4.2.1\" xref=\"p1.1.m1.1.3.4.2.1.cmml\">\n" +
                "                  <mo id=\"p1.1.m1.1.3.4.2.1.1\" xref=\"p1.1.m1.1.3.4.2.1.1.cmml\">-</mo>\n" +
                "                  <mi id=\"p1.1.m1.1.3.4.2.1.2\" xref=\"p1.1.m1.1.3.4.2.1.2.cmml\">t</mi>\n" +
                "                </mrow>\n" +
                "              </msup>\n" +
                "              <mo id=\"p1.1.m1.1.3.4.5\" xref=\"p1.1.m1.1.3.4.5.cmml\">\u2062</mo>\n" +
                "              <msup id=\"p1.1.m1.1.3.4.7\" xref=\"p1.1.m1.1.3.4.7.cmml\">\n" +
                "                <mi id=\"p1.1.m1.1.3.4.3\" xref=\"p1.1.m1.1.3.4.3.cmml\">t</mi>\n" +
                "                <mrow id=\"p1.1.m1.1.3.4.4.1\" xref=\"p1.1.m1.1.3.4.4.1.cmml\">\n" +
                "                  <mi id=\"p1.1.m1.1.3.4.4.1.1\" xref=\"p1.1.m1.1.3.4.4.1.1.cmml\">z</mi>\n" +
                "                  <mo id=\"p1.1.m1.1.3.4.4.1.2\" xref=\"p1.1.m1.1.3.4.4.1.2.cmml\">-</mo>\n" +
                "                  <mn id=\"p1.1.m1.1.3.4.4.1.3\" xref=\"p1.1.m1.1.3.4.4.1.3.cmml\">1</mn>\n" +
                "                </mrow>\n" +
                "              </msup>\n" +
                "            </mrow>\n" +
                "          </mpadded>\n" +
                "          <mo id=\"p1.1.m1.1.3.8.1.1\" xref=\"p1.1.m1.1.3.1.cmml\">\u2062</mo>\n" +
                "          <mi mathvariant=\"normal\" id=\"p1.1.m1.1.3.6\" xref=\"p1.1.m1.1.3.1.cmml\">d</mi>\n" +
                "          <mo id=\"p1.1.m1.1.3.8.1.1a\" xref=\"p1.1.m1.1.3.1.cmml\">\u2062</mo>\n" +
                "          <mi id=\"p1.1.m1.1.3.7\" xref=\"p1.1.m1.1.3.7.cmml\">t</mi>\n" +
                "        </mrow>\n" +
                "      </mrow>\n" +
                "    </mrow>\n" +
                "    <annotation-xml encoding=\"MathML-Content\" id=\"p1.1.m1.1b\">\n" +
                "      <apply id=\"p1.1.m1.1.4.cmml\" xref=\"p1.1.m1.1.4\">\n" +
                "        <eq id=\"p1.1.m1.1.2.cmml\" xref=\"p1.1.m1.1.2\"/>\n" +
                "        <apply id=\"p1.1.m1.1.1.1.cmml\" xref=\"p1.1.m1.1.1.8.2\">\n" +
                "          <csymbol cd=\"dlmf\" id=\"p1.1.m1.1.1.2.1.1.cmml\" xref=\"p1.1.m1.1.1.2.1.2\">Euler-Gamma</csymbol>\n" +
                "          <ci id=\"p1.1.m1.1.1.6.cmml\" xref=\"p1.1.m1.1.1.6\">\uD835\uDC67</ci>\n" +
                "        </apply>\n" +
                "        <apply id=\"p1.1.m1.1.3.1.cmml\" xref=\"p1.1.m1.1.3.8\">\n" +
                "          <apply id=\"p1.1.m1.1.3.2.1.1.cmml\" xref=\"p1.1.m1.1.3.2.1.6\">\n" +
                "            <csymbol cd=\"dlmf\" id=\"p1.1.m1.1.3.2.1.1.1.cmml\" xref=\"p1.1.m1.1.3.2.1.2\">semantic-definite-integral</csymbol>\n" +
                "            <cn type=\"integer\" id=\"p1.1.m1.1.3.2.1.3.1.cmml\" xref=\"p1.1.m1.1.3.2.1.3.1\">0</cn>\n" +
                "            <infinity id=\"p1.1.m1.1.3.2.1.4.1.cmml\" xref=\"p1.1.m1.1.3.2.1.4.1\"/>\n" +
                "          </apply>\n" +
                "          <ci id=\"p1.1.m1.1.3.7.cmml\" xref=\"p1.1.m1.1.3.7\">\uD835\uDC61</ci>\n" +
                "          <apply id=\"p1.1.m1.1.3.4.cmml\" xref=\"p1.1.m1.1.3.4\">\n" +
                "            <times id=\"p1.1.m1.1.3.4.5.cmml\" xref=\"p1.1.m1.1.3.4.5\"/>\n" +
                "            <apply id=\"p1.1.m1.1.3.4.6.cmml\" xref=\"p1.1.m1.1.3.4.6\">\n" +
                "              <power xmlns:m=\"http://www.w3.org/1998/Math/MathML\" xref=\"p1.1.m1.1.3.4.6\"/>\n" +
                "              <ci id=\"p1.1.m1.1.3.4.1.cmml\" xref=\"p1.1.m1.1.3.4.1\">\uD835\uDC52</ci>\n" +
                "              <apply id=\"p1.1.m1.1.3.4.2.1.cmml\" xref=\"p1.1.m1.1.3.4.2.1\">\n" +
                "                <minus id=\"p1.1.m1.1.3.4.2.1.1.cmml\" xref=\"p1.1.m1.1.3.4.2.1.1\"/>\n" +
                "                <ci id=\"p1.1.m1.1.3.4.2.1.2.cmml\" xref=\"p1.1.m1.1.3.4.2.1.2\">\uD835\uDC61</ci>\n" +
                "              </apply>\n" +
                "            </apply>\n" +
                "            <apply id=\"p1.1.m1.1.3.4.7.cmml\" xref=\"p1.1.m1.1.3.4.7\">\n" +
                "              <power xmlns:m=\"http://www.w3.org/1998/Math/MathML\" xref=\"p1.1.m1.1.3.4.7\"/>\n" +
                "              <ci id=\"p1.1.m1.1.3.4.3.cmml\" xref=\"p1.1.m1.1.3.4.3\">\uD835\uDC61</ci>\n" +
                "              <apply id=\"p1.1.m1.1.3.4.4.1.cmml\" xref=\"p1.1.m1.1.3.4.4.1\">\n" +
                "                <minus id=\"p1.1.m1.1.3.4.4.1.2.cmml\" xref=\"p1.1.m1.1.3.4.4.1.2\"/>\n" +
                "                <ci id=\"p1.1.m1.1.3.4.4.1.1.cmml\" xref=\"p1.1.m1.1.3.4.4.1.1\">\uD835\uDC67</ci>\n" +
                "                <cn type=\"integer\" id=\"p1.1.m1.1.3.4.4.1.3.cmml\" xref=\"p1.1.m1.1.3.4.4.1.3\">1</cn>\n" +
                "              </apply>\n" +
                "            </apply>\n" +
                "          </apply>\n" +
                "        </apply>\n" +
                "      </apply>\n" +
                "    </annotation-xml>\n" +
                "  </semantics>\n" +
                "</math>";
        assertNotNull(CMMLHelper.getStrictCmml(mathml));
    }

    private String getResourceFile(String filename) throws IOException, ParserConfigurationException {
        return IOUtils.toString(this.getClass().getResourceAsStream(filename), "UTF-8");
    }

}