package com.formulasearchengine.mathmltools.mml;

import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import net.sf.saxon.s9api.XQueryExecutable;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLIdentical;
import static org.custommonkey.xmlunit.XMLUnit.compareXML;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@SuppressWarnings("JavaDoc")
public class CMMLInfoTest {
    private static final String MML_TEST_DIR = "com/formulasearchengine/mathmltools/mml/";
    private final String rawTests[] = {"<annotation-xml encoding=\"MathML-Content\" id=\"I1.i2.p1.1.m1.1.cmml\" xref=\"I1.i2.p1.1.m1.1\">\n" +
            "  <apply id=\"I1.i2.p1.1.m1.1.6.cmml\" xref=\"I1.i2.p1.1.m1.1.6\">\n" +
            "    <list id=\"I1.i2.p1.1.m1.1.6.1.cmml\"/>\n" +
            "    <apply id=\"I1.i2.p1.1.m1.1.6.2.cmml\" xref=\"I1.i2.p1.1.m1.1.6.2\">\n" +
            "      <csymbol cd=\"ambiguous\" id=\"I1.i2.p1.1.m1.1.6.2.1.cmml\">subscript</csymbol>\n" +
            "      <ci id=\"I1.i2.p1.1.m1.1.1.cmml\" xref=\"I1.i2.p1.1.m1.1.1\">I</ci>\n" +
            "      <cn id=\"I1.i2.p1.1.m1.1.2.1.cmml\" type=\"integer\" xref=\"I1.i2.p1.1.m1.1.2.1\">1</cn>\n" +
            "    </apply>\n" +
            "    <apply id=\"I1.i2.p1.1.m1.1.6.3.cmml\" xref=\"I1.i2.p1.1.m1.1.6.3\">\n" +
            "      <csymbol cd=\"ambiguous\" id=\"I1.i2.p1.1.m1.1.6.3.1.cmml\">subscript</csymbol>\n" +
            "      <ci id=\"I1.i2.p1.1.m1.1.4.cmml\" xref=\"I1.i2.p1.1.m1.1.4\">I</ci>\n" +
            "      <cn id=\"I1.i2.p1.1.m1.1.5.1.cmml\" type=\"integer\" xref=\"I1.i2.p1.1.m1.1.5.1\">2</cn>\n" +
            "    </apply>\n" +
            "  </apply>\n" +
            "</annotation-xml>\n",
            "<annotation-xml encoding=\"MathML-Content\" id=\"I1.i2.p1.1.m2.1.cmml\" xref=\"I1.i2.p1.1.m2.1\">\n" +
                    "  <apply id=\"I1.i2.p1.1.m2.1.8.cmml\" xref=\"I1.i2.p1.1.m2.1.8\">\n" +
                    "    <eq id=\"I1.i2.p1.1.m2.1.6.cmml\" xref=\"I1.i2.p1.1.m2.1.6\"/>\n" +
                    "    <apply id=\"I1.i2.p1.1.m2.1.8.1.cmml\" xref=\"I1.i2.p1.1.m2.1.8.1\">\n" +
                    "      <intersect id=\"I1.i2.p1.1.m2.1.3.cmml\" xref=\"I1.i2.p1.1.m2.1.3\"/>\n" +
                    "      <apply id=\"I1.i2.p1.1.m2.1.8.1.1.cmml\" xref=\"I1.i2.p1.1.m2.1.8.1.1\">\n" +
                    "        <csymbol cd=\"ambiguous\" id=\"I1.i2.p1.1.m2.1.8.1.1.1.cmml\">subscript</csymbol>\n" +
                    "        <ci id=\"I1.i2.p1.1.m2.1.1.cmml\" xref=\"I1.i2.p1.1.m2.1.1\">I</ci>\n" +
                    "        <cn id=\"I1.i2.p1.1.m2.1.2.1.cmml\" type=\"integer\" xref=\"I1.i2.p1.1.m2.1.2.1\">1</cn>\n" +
                    "      </apply>\n" +
                    "      <apply id=\"I1.i2.p1.1.m2.1.8.1.2.cmml\" xref=\"I1.i2.p1.1.m2.1.8.1.2\">\n" +
                    "        <csymbol cd=\"ambiguous\" id=\"I1.i2.p1.1.m2.1.8.1.2.1.cmml\">subscript</csymbol>\n" +
                    "        <ci id=\"I1.i2.p1.1.m2.1.4.cmml\" xref=\"I1.i2.p1.1.m2.1.4\">I</ci>\n" +
                    "        <cn id=\"I1.i2.p1.1.m2.1.5.1.cmml\" type=\"integer\" xref=\"I1.i2.p1.1.m2.1.5.1\">2</cn>\n" +
                    "      </apply>\n" +
                    "    </apply>\n" +
                    "    <emptyset id=\"I1.i2.p1.1.m2.1.7.cmml\" xref=\"I1.i2.p1.1.m2.1.7\"/>\n" +
                    "  </apply>\n" +
                    "</annotation-xml>\n",
            "<annotation-xml encoding=\"MathML-Content\" id=\"I1.i2.p1.1.m3.1.cmml\" xref=\"I1.i2.p1.1.m3.1\">\n" +
                    "  <apply id=\"I1.i2.p1.1.m3.1.16.cmml\" xref=\"I1.i2.p1.1.m3.1.16\">\n" +
                    "    <eq id=\"I1.i2.p1.1.m3.1.14.cmml\" xref=\"I1.i2.p1.1.m3.1.14\"/>\n" +
                    "    <apply id=\"I1.i2.p1.1.m3.1.16.1.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1\">\n" +
                    "      <interval closure=\"closed\" id=\"I1.i2.p1.1.m3.1.16.1.1.cmml\"/>\n" +
                    "      <apply id=\"I1.i2.p1.1.m3.1.16.1.2.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.2\">\n" +
                    "        <times id=\"I1.i2.p1.1.m3.1.16.1.2.1.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.2.1\"/>\n" +
                    "        <ci id=\"I1.i2.p1.1.m3.1.2.cmml\" xref=\"I1.i2.p1.1.m3.1.2\">&#119964;</ci>\n" +
                    "        <apply id=\"I1.i2.p1.1.m3.1.16.1.2.2.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.2.2\">\n" +
                    "          <csymbol cd=\"ambiguous\" id=\"I1.i2.p1.1.m3.1.16.1.2.2.1.cmml\">subscript</csymbol>\n" +
                    "          <ci id=\"I1.i2.p1.1.m3.1.4.cmml\" xref=\"I1.i2.p1.1.m3.1.4\">I</ci>\n" +
                    "          <cn id=\"I1.i2.p1.1.m3.1.5.1.cmml\" type=\"integer\" xref=\"I1.i2.p1.1.m3.1.5.1\">1</cn>\n" +
                    "        </apply>\n" +
                    "      </apply>\n" +
                    "      <apply id=\"I1.i2.p1.1.m3.1.16.1.3.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.3\">\n" +
                    "        <times id=\"I1.i2.p1.1.m3.1.16.1.3.1.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.3.1\"/>\n" +
                    "        <ci id=\"I1.i2.p1.1.m3.1.8.cmml\" xref=\"I1.i2.p1.1.m3.1.8\">&#119964;</ci>\n" +
                    "        <apply id=\"I1.i2.p1.1.m3.1.16.1.3.2.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.3.2\">\n" +
                    "          <csymbol cd=\"ambiguous\" id=\"I1.i2.p1.1.m3.1.16.1.3.2.1.cmml\">subscript</csymbol>\n" +
                    "          <ci id=\"I1.i2.p1.1.m3.1.10.cmml\" xref=\"I1.i2.p1.1.m3.1.10\">I</ci>\n" +
                    "          <cn id=\"I1.i2.p1.1.m3.1.11.1.cmml\" type=\"integer\" xref=\"I1.i2.p1.1.m3.1.11.1\">2</cn>\n" +
                    "        </apply>\n" +
                    "      </apply>\n" +
                    "    </apply>\n" +
                    "    <cn id=\"I1.i2.p1.1.m3.1.15.cmml\" type=\"integer\" xref=\"I1.i2.p1.1.m3.1.15\">0</cn>\n" +
                    "  </apply>\n" +
                    "</annotation-xml>\n"};
    private final String[] abstractCDs = {"<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
            "   <semantics>\n" +
            "      <annotation-xml encoding=\"MathML-Content\"\n" +
            "                      id=\"I1.i2.p1.1.m1.1.cmml\"\n" +
            "                      xref=\"I1.i2.p1.1.m1.1\">\n" +
            "         <apply id=\"I1.i2.p1.1.m1.1.6.cmml\" xref=\"I1.i2.p1.1.m1.1.6\">\n" +
            "            <list id=\"I1.i2.p1.1.m1.1.6.1.cmml\"/>\n" +
            "            <apply id=\"I1.i2.p1.1.m1.1.6.2.cmml\" xref=\"I1.i2.p1.1.m1.1.6.2\">\n" +
            "               <ambiguous xmlns=\"http://formulasearchengine.com/ns/pseudo/gen/cd\"\n" +
            "                          cd=\"ambiguous\"\n" +
            "                          id=\"I1.i2.p1.1.m1.1.6.2.1.cmml\"/>\n" +
            "               <ci id=\"I1.i2.p1.1.m1.1.1.cmml\" xref=\"I1.i2.p1.1.m1.1.1\"/>\n" +
            "               <cn id=\"I1.i2.p1.1.m1.1.2.1.cmml\"\n" +
            "                   type=\"integer\"\n" +
            "                   xref=\"I1.i2.p1.1.m1.1.2.1\"/>\n" +
            "            </apply>\n" +
            "            <apply id=\"I1.i2.p1.1.m1.1.6.3.cmml\" xref=\"I1.i2.p1.1.m1.1.6.3\">\n" +
            "               <ambiguous xmlns=\"http://formulasearchengine.com/ns/pseudo/gen/cd\"\n" +
            "                          cd=\"ambiguous\"\n" +
            "                          id=\"I1.i2.p1.1.m1.1.6.3.1.cmml\"/>\n" +
            "               <ci id=\"I1.i2.p1.1.m1.1.4.cmml\" xref=\"I1.i2.p1.1.m1.1.4\"/>\n" +
            "               <cn id=\"I1.i2.p1.1.m1.1.5.1.cmml\"\n" +
            "                   type=\"integer\"\n" +
            "                   xref=\"I1.i2.p1.1.m1.1.5.1\"/>\n" +
            "            </apply>\n" +
            "         </apply>\n" +
            "      </annotation-xml>\n" +
            "   </semantics>\n" +
            "</math>\n",
            "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
                    "   <semantics>\n" +
                    "      <annotation-xml encoding=\"MathML-Content\"\n" +
                    "                      id=\"I1.i2.p1.1.m2.1.cmml\"\n" +
                    "                      xref=\"I1.i2.p1.1.m2.1\">\n" +
                    "         <apply id=\"I1.i2.p1.1.m2.1.8.cmml\" xref=\"I1.i2.p1.1.m2.1.8\">\n" +
                    "            <eq id=\"I1.i2.p1.1.m2.1.6.cmml\" xref=\"I1.i2.p1.1.m2.1.6\"/>\n" +
                    "            <apply id=\"I1.i2.p1.1.m2.1.8.1.cmml\" xref=\"I1.i2.p1.1.m2.1.8.1\">\n" +
                    "               <intersect id=\"I1.i2.p1.1.m2.1.3.cmml\" xref=\"I1.i2.p1.1.m2.1.3\"/>\n" +
                    "               <apply id=\"I1.i2.p1.1.m2.1.8.1.1.cmml\" xref=\"I1.i2.p1.1.m2.1.8.1.1\">\n" +
                    "                  <ambiguous xmlns=\"http://formulasearchengine.com/ns/pseudo/gen/cd\"\n" +
                    "                             cd=\"ambiguous\"\n" +
                    "                             id=\"I1.i2.p1.1.m2.1.8.1.1.1.cmml\"/>\n" +
                    "                  <ci id=\"I1.i2.p1.1.m2.1.1.cmml\" xref=\"I1.i2.p1.1.m2.1.1\"/>\n" +
                    "                  <cn id=\"I1.i2.p1.1.m2.1.2.1.cmml\"\n" +
                    "                      type=\"integer\"\n" +
                    "                      xref=\"I1.i2.p1.1.m2.1.2.1\"/>\n" +
                    "               </apply>\n" +
                    "               <apply id=\"I1.i2.p1.1.m2.1.8.1.2.cmml\" xref=\"I1.i2.p1.1.m2.1.8.1.2\">\n" +
                    "                  <ambiguous xmlns=\"http://formulasearchengine.com/ns/pseudo/gen/cd\"\n" +
                    "                             cd=\"ambiguous\"\n" +
                    "                             id=\"I1.i2.p1.1.m2.1.8.1.2.1.cmml\"/>\n" +
                    "                  <ci id=\"I1.i2.p1.1.m2.1.4.cmml\" xref=\"I1.i2.p1.1.m2.1.4\"/>\n" +
                    "                  <cn id=\"I1.i2.p1.1.m2.1.5.1.cmml\"\n" +
                    "                      type=\"integer\"\n" +
                    "                      xref=\"I1.i2.p1.1.m2.1.5.1\"/>\n" +
                    "               </apply>\n" +
                    "            </apply>\n" +
                    "            <emptyset id=\"I1.i2.p1.1.m2.1.7.cmml\" xref=\"I1.i2.p1.1.m2.1.7\"/>\n" +
                    "         </apply>\n" +
                    "      </annotation-xml>\n" +
                    "   </semantics>\n" +
                    "</math>\n",
            "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
                    "   <semantics>\n" +
                    "      <annotation-xml encoding=\"MathML-Content\"\n" +
                    "                      id=\"I1.i2.p1.1.m3.1.cmml\"\n" +
                    "                      xref=\"I1.i2.p1.1.m3.1\">\n" +
                    "         <apply id=\"I1.i2.p1.1.m3.1.16.cmml\" xref=\"I1.i2.p1.1.m3.1.16\">\n" +
                    "            <eq id=\"I1.i2.p1.1.m3.1.14.cmml\" xref=\"I1.i2.p1.1.m3.1.14\"/>\n" +
                    "            <apply id=\"I1.i2.p1.1.m3.1.16.1.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1\">\n" +
                    "               <interval closure=\"closed\" id=\"I1.i2.p1.1.m3.1.16.1.1.cmml\"/>\n" +
                    "               <apply id=\"I1.i2.p1.1.m3.1.16.1.2.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.2\">\n" +
                    "                  <times id=\"I1.i2.p1.1.m3.1.16.1.2.1.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.2.1\"/>\n" +
                    "                  <ci id=\"I1.i2.p1.1.m3.1.2.cmml\" xref=\"I1.i2.p1.1.m3.1.2\"/>\n" +
                    "                  <apply id=\"I1.i2.p1.1.m3.1.16.1.2.2.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.2.2\">\n" +
                    "                     <ambiguous xmlns=\"http://formulasearchengine.com/ns/pseudo/gen/cd\"\n" +
                    "                                cd=\"ambiguous\"\n" +
                    "                                id=\"I1.i2.p1.1.m3.1.16.1.2.2.1.cmml\"/>\n" +
                    "                     <ci id=\"I1.i2.p1.1.m3.1.4.cmml\" xref=\"I1.i2.p1.1.m3.1.4\"/>\n" +
                    "                     <cn id=\"I1.i2.p1.1.m3.1.5.1.cmml\"\n" +
                    "                         type=\"integer\"\n" +
                    "                         xref=\"I1.i2.p1.1.m3.1.5.1\"/>\n" +
                    "                  </apply>\n" +
                    "               </apply>\n" +
                    "               <apply id=\"I1.i2.p1.1.m3.1.16.1.3.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.3\">\n" +
                    "                  <times id=\"I1.i2.p1.1.m3.1.16.1.3.1.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.3.1\"/>\n" +
                    "                  <ci id=\"I1.i2.p1.1.m3.1.8.cmml\" xref=\"I1.i2.p1.1.m3.1.8\"/>\n" +
                    "                  <apply id=\"I1.i2.p1.1.m3.1.16.1.3.2.cmml\" xref=\"I1.i2.p1.1.m3.1.16.1.3.2\">\n" +
                    "                     <ambiguous xmlns=\"http://formulasearchengine.com/ns/pseudo/gen/cd\"\n" +
                    "                                cd=\"ambiguous\"\n" +
                    "                                id=\"I1.i2.p1.1.m3.1.16.1.3.2.1.cmml\"/>\n" +
                    "                     <ci id=\"I1.i2.p1.1.m3.1.10.cmml\" xref=\"I1.i2.p1.1.m3.1.10\"/>\n" +
                    "                     <cn id=\"I1.i2.p1.1.m3.1.11.1.cmml\"\n" +
                    "                         type=\"integer\"\n" +
                    "                         xref=\"I1.i2.p1.1.m3.1.11.1\"/>\n" +
                    "                  </apply>\n" +
                    "               </apply>\n" +
                    "            </apply>\n" +
                    "            <cn id=\"I1.i2.p1.1.m3.1.15.cmml\"\n" +
                    "                type=\"integer\"\n" +
                    "                xref=\"I1.i2.p1.1.m3.1.15\"/>\n" +
                    "         </apply>\n" +
                    "      </annotation-xml>\n" +
                    "   </semantics>\n" +
                    "</math>\n"};


    @BeforeClass
    public static void setup() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setXSLTVersion("2.0");
    }

    private static String getFileContents(String fName) throws IOException {
        final InputStream is = CMMLInfoTest.class.getClassLoader().getResourceAsStream(fName);
        try {
            final Scanner s = new Scanner(is, "UTF-8");
            //Stupid scanner tricks to read the entire file as one token
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } finally {
            is.close();
        }
    }

    /**
     * @throws IOException
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     */
    @Test
    public final void testIsEquation() throws IOException, XPathExpressionException, ParserConfigurationException {
        final boolean[] isEquation = {false, true, true};
        int i = 0;
        for (String rawTest : rawTests) {
            CMMLInfo cmmlElement = CMMLInfo.newFromSnippet(rawTest);
            assertEquals("Test " + i + " failed", isEquation[i], cmmlElement.isEquation());
            i++;
        }
    }

    @Test
    public final void testIdentifier() throws Exception {

        final Multiset[] identifiers = {ImmutableMultiset.of("1", "2", "I", "I"),
                ImmutableMultiset.of("1", "2", "I", "I"),
                ImmutableMultiset.of("0", "1", "2", "I", "I", "\uD835\uDC9C", "\uD835\uDC9C")};

        int i = 0;
        for (final String rawTest : rawTests) {
            final CMMLInfo cmmlElement = CMMLInfo.newFromSnippet(rawTest);
            assertEquals("Test " + i + " failed", identifiers[i], cmmlElement.getElements());
            i++;
        }
    }

    @Test
    public final void testToString() throws Exception {
        int i = 0;
        for (final String rawTest : rawTests) {
            final CMMLInfo cmmlElement = new CMMLInfo(rawTest);
            // Ignore Windows style line-breaks
            assertTrue("Test " + i + " failed", compareXML(rawTest, cmmlElement.toString()).similar());
            i++;
        }
    }

    @Test
    public final void testAbstract2CDs() throws Exception {
        int i = 0;
        for (final String rawTest : rawTests) {
            final CMMLInfo cmmlElement = CMMLInfo.newFromSnippet(rawTest);
            assertEquals("Test " + i + " failed", abstractCDs[i], cmmlElement.abstract2CDs().toString());
            i++;
        }
    }

    @Test
    public final void testStrictCmml1() throws Exception {
        String simpleMathTag = getFileContents(MML_TEST_DIR + "sample.mml");
        String simpleStrictMathTag = getFileContents(MML_TEST_DIR + "reference_sample.mml");
        CMMLInfo cmml = new CMMLInfo(simpleMathTag);
        assertTrue(compareXML(simpleStrictMathTag, cmml.toStrictCmml().toString()).identical());
    }


    private XQueryExecutable getQueryI() throws IOException, ParserConfigurationException {
        final String ciI = getFileContents(MML_TEST_DIR + "I.mml.xml");
        CMMLInfo cmml = new CMMLInfo(ciI);
        return cmml.getXQuery();
    }

    @Test
    public final void testGenerateXQuery() throws Exception {
        final String ciI = getFileContents(MML_TEST_DIR + "I.mml.xml");
        final String sampleMML = getFileContents(MML_TEST_DIR + "q1.xml");
        final String res1 = getFileContents(MML_TEST_DIR + "res1.xml");
        CMMLInfo cmml = new CMMLInfo(ciI);
        final XQueryExecutable xQuery = cmml.getXQuery();
        Document doc = XMLHelper.runXQuery(xQuery, sampleMML);
        assertXMLIdentical(compareXML(XMLHelper.string2Doc(res1, true), doc), true);
    }

    @Test
    public final void testGetDepth() throws Exception {
        XQueryExecutable iQuery = getQueryI();
        final String sampleMML = getFileContents(MML_TEST_DIR + "q1.xml");
        CMMLInfo cmml = new CMMLInfo(sampleMML);
        Integer depth = cmml.getDepth(iQuery);
        assertEquals(8, (int) depth);
    }

    @Test
    public final void testIsEquation2() throws Exception {
        final String sampleMML = getFileContents(MML_TEST_DIR + "Emc2.xml");
        CMMLInfo cmmlElement = new CMMLInfo(sampleMML);
        assertTrue(cmmlElement.isEquation());
    }

  @Test
  public final void test2CMML2() throws Exception {
    final String sampleMML = getFileContents(MML_TEST_DIR + "testquery1.xml");
    CMMLInfo cmml = new CMMLInfo(sampleMML);
    XQueryExecutable standardQuery = cmml.getXQuery();

    CMMLInfo strict = new CMMLInfo(sampleMML).toStrictCmml();
    XQueryExecutable CDQuery = strict.getXQuery();
    CMMLInfo cmmlElement = new CMMLInfo(sampleMML);
    System.out.println(cmmlElement.toStrictCmml().getXQuery().getUnderlyingCompiledQuery().getExpression().toString());
  }


  @Test
  public final void testStrictQueryTest() throws Exception {
    final String sampleMML = getFileContents(MML_TEST_DIR + "query1.xml");
    CMMLInfo mml = new CMMLInfo(sampleMML);
    System.out.println(TreeWriter.compactForm(mml.toStrictCmml().abstract2CDs()));

  }

  @Test
  public final void testDtQueryTest() throws Exception {
    String s = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";\n" +
            "declare namespace functx = \"http://www.functx.com\";\n" +
            "declare function functx:path-to-node( $nodes as node()* ) as xs:string* {\n" +
            "$nodes/string-join(ancestor-or-self::*/name(.), '/')\n" +
            " };\n" +
            "<result> {for $m in . return\n" +
            "for $x in $m//*:apply\n" +
            "[*[1]/name() = 'l1' and *[2]/name() = 'apply' and *[2][*[1]/name() = 'l1'] and *[3]/name() = 'apply' and *[3][*[1]/name() = 'l1' and *[2]/name() = 'l0' and *[3]/name() = 'l0']]\n" +
            "where\n" +
            "fn:count($x/*[2]/*) = 3\n" +
            " and fn:count($x/*[3]/*) = 3\n" +
            " and fn:count($x/*) = 3\n" +
            "\n" +
            "return\n" +
            "<element><x>{$x}</x><p>{data(functx:path-to-node($x))}</p></element>}\n" +
            "</result>";
    final String sampleMML = getFileContents(MML_TEST_DIR + "query1.xml");
    CMMLInfo mml = new CMMLInfo(sampleMML);
    System.out.println(TreeWriter.compactForm(mml.toStrictCmml().abstract2DTs()));
    assertEquals(s, mml.getXQueryString());
  }

    @Test
    public final void testLoadArXivDocTest() throws Exception {
        final String sampleMML = getFileContents(MML_TEST_DIR + "arxivSample.mml");
        CMMLInfo mml = new CMMLInfo(sampleMML);
        System.out.println(TreeWriter.compactForm(mml.toStrictCmml().abstract2CDs()));

    }

}