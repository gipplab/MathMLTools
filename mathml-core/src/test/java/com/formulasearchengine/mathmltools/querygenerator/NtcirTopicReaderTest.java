package com.formulasearchengine.mathmltools.querygenerator;

import static com.formulasearchengine.mathmltools.querygenerator.QVarXQueryGeneratorTest.getFileContents;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.formulasearchengine.mathmltools.helper.XMLHelper;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class NtcirTopicReaderTest {
    private static final Logger LOG = LogManager.getLogger(NtcirTopicReaderTest.class.getName());

    public static final String BASEX_NAMESPACE = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";";
    public static final String BASEX_PATHTOROOT = "//*:expr";
    public static final String BASEX_RETURNFORMAT = "<a href=\"http://demo.formulasearchengine.com/index.php?curid={$m/@url}\">result</a>";
    public static final String WIKIPEDIA_RESOURCE = "jp/ac/nii/Ntcir11MathWikipediaTopicsParticipants.xml";
    public static final String ARXIV_RESOURCE = "jp/ac/nii/NTCIR-11-Math-test.xml";


    @Test
    public void testExtractPatterns() throws Exception {
        assertEquals(100, countFormulaeInTopics(WIKIPEDIA_RESOURCE), "Count in Wikipedia testfile incorrect");
        assertEquals(55, countFormulaeInTopics(ARXIV_RESOURCE), "Count in arXiv testfile incorrect");
    }


    private int countFormulaeInTopics(String resourceName) throws URISyntaxException, IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        final List<NtcirPattern> ntcirPatterns = getTopicReader(resourceName).extractPatterns();
        return ntcirPatterns.size();
    }

    private NtcirTopicReader getTopicReader(String resourceName) throws ParserConfigurationException, IOException, SAXException, URISyntaxException, XPathExpressionException, NullPointerException {
        final URL resource = getClass().getClassLoader().getResource(resourceName);
        return new NtcirTopicReader(new File(resource.toURI())).setAddQvarMap(false);
    }

    public NtcirPattern getFirstTopic() throws URISyntaxException, ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        final NtcirTopicReader tr = getTopicReader(WIKIPEDIA_RESOURCE);
        return tr.extractPatterns().get(0);
    }

    @Test
    public void checkBaseX() throws Exception {
        final String referenceString = getFileContents("jp/ac/nii/basexReferenceQueries.txt");
        final NtcirTopicReader tr = getTopicReader(WIKIPEDIA_RESOURCE);
        //tr.setNamespace(BASEX_NAMESPACE);
        tr.setPathToRoot(BASEX_PATHTOROOT);
        tr.setReturnFormat(BASEX_RETURNFORMAT);
        final StringBuilder sb = new StringBuilder();
        for (final NtcirPattern ntcirPattern : tr.extractPatterns()) {
            sb.append(ntcirPattern.getxQueryExpression()).append("\n");
        }
        assertEquals(referenceString, sb.toString());

    }

    @Test
    public void checkRecursiveBaseX() throws Exception {
        final String referenceString = getFileContents("jp/ac/nii/basexRecursiveReferenceQueries.txt");
        final NtcirTopicReader tr = getTopicReader(WIKIPEDIA_RESOURCE);
        //tr.setNamespace(BASEX_NAMESPACE);
        tr.setPathToRoot(BASEX_PATHTOROOT);
        tr.setReturnFormat(BASEX_RETURNFORMAT);
        tr.setFindRootApply(true);
        final StringBuilder sb = new StringBuilder();
        for (final NtcirPattern ntcirPattern : tr.extractPatterns()) {
            sb.append(ntcirPattern.getxQueryExpression()).append("\n");
        }
        LOG.debug(sb.toString());
        assertEquals(referenceString, sb.toString());
    }

    @Test
    public void extractPattern() throws Exception {
        final NtcirTopicReader tr = getTopicReader(WIKIPEDIA_RESOURCE);
        tr.setNamespace(BASEX_NAMESPACE);
        tr.setPathToRoot(BASEX_PATHTOROOT);
        tr.setReturnFormat(BASEX_RETURNFORMAT);
        for (final NtcirPattern ntcirPattern : tr.extractPatterns()) {
            if (ntcirPattern.getNum().endsWith("29")) {
                LOG.debug(ntcirPattern.getxQueryExpression());
            }
        }
    }

    @Test
    public void testSetRestricLength() throws Exception {
        final String NoLenghtxQuery = getTopicReader(WIKIPEDIA_RESOURCE).setRestrictLength(false)
                .extractPatterns().get(0).getxQueryExpression();
        assertEquals("declare default element namespace \"http://www.w3.org/1998/Math/MathML\";\n" +
                "for $m in db2-fn:xmlcolumn(\"math.math_mathml\") return\n" +
                "for $x in $m//*:apply\n" +
                "[*[1]/name() = 'gt' and *[2]/name() = 'apply' and *[2][*[1]/name() = 'times' and *[2]/name() = 'ci' and *[2][./text() = 'W'] and *[3]/name() = 'interval' and *[3][*[1]/name() = 'cn' and *[1][./text() = '2'] and *[2]/name() = 'ci' and *[2][./text() = 'k']]] and *[3]/name() = 'apply' and *[3][*[1]/name() = 'divide' and *[2]/name() = 'apply' and *[2][*[1]/name() = 'csymbol' and *[1][./text() = 'superscript'] and *[2]/name() = 'cn' and *[2][./text() = '2'] and *[3]/name() = 'ci' and *[3][./text() = 'k']] and *[3]/name() = 'apply' and *[3][*[1]/name() = 'csymbol' and *[1][./text() = 'superscript'] and *[2]/name() = 'ci' and *[2][./text() = 'k'] and *[3]/name() = 'ci' and *[3][./text() = 'ε']]]]\n\n" +
                "return\n" +
                "data($m/*[1]/@alttext)", NoLenghtxQuery);
    }

    @Test
    public void testAlternativeConstructor() throws Exception {
        final URL resource = getClass().getClassLoader().getResource(ARXIV_RESOURCE);
        DocumentBuilder documentBuilder = XMLHelper.getDocumentBuilder(true);
        Document topics = documentBuilder.parse(new File(resource.toURI()));
        new NtcirTopicReader(topics);
        new NtcirTopicReader(topics, "", "", "", false);
    }
}