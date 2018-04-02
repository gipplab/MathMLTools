package com.formulasearchengine.mathmltools.querygenerator;

import com.formulasearchengine.mathmltools.xml.NonWhitespaceNodeList;
import com.formulasearchengine.mathmltools.helper.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moritz on 08.11.2014.
 * <p/>
 * Reads the topic format specified in
 * http://ntcir-math.nii.ac.jp/wp-content/blogs.dir/13/files/2014/05/NTCIR11-Math-topics.pdf
 */
public class NtcirTopicReader {
    public static final String NS_NII = "http://ntcir-math.nii.ac.jp/";
    private final Document topics;
    private final List<NtcirPattern> patterns = new ArrayList<>();
    private final QVarXQueryGenerator queryGenerator;

    public NtcirTopicReader(Document topics) {
        this.topics = topics;
        queryGenerator = new QVarXQueryGenerator();
    }

    public NtcirTopicReader(File topicFile) throws ParserConfigurationException, IOException, SAXException {
        this(XMLHelper.getDocumentBuilder(true).parse(topicFile));
        queryGenerator.addDefaultHeader();
    }

    public NtcirTopicReader(Document topics, String namespace, String pathToRoot, String returnFormat, boolean restrictLength) {
        this(topics);
        this.setNamespace(namespace).setReturnFormat(returnFormat).setPathToRoot(pathToRoot).setRestrictLength(restrictLength);
    }

    public final NtcirTopicReader setReturnFormat(String returnFormat) {
        queryGenerator.setReturnFormat(returnFormat);
        return this;
    }

    public final NtcirTopicReader setNamespace(String namespace) {
        queryGenerator.addHeader(namespace);
        return this;
    }

    public final NtcirTopicReader setPathToRoot(String pathToRoot) {
        queryGenerator.setPathToRoot(pathToRoot);
        return this;
    }

    public final NtcirTopicReader setFindRootApply(boolean findRootApply) {
        queryGenerator.setFindRootApply(findRootApply);
        return this;
    }

    public final NtcirTopicReader setRestrictLength(boolean restrictLength) {
        queryGenerator.setRestrictLength(restrictLength);
        return this;
    }

    public final NtcirTopicReader setAddQvarMap(boolean addQvarMap) {
        queryGenerator.setAddQvarMap(addQvarMap);
        return this;
    }

    /**
     * Splits the given NTCIR query file into individual queries, converts each query into an XQuery using
     * QVarXQueryGenerator, and returns the result as a list of NtcirPatterns for each individual query.
     *
     * @return List of NtcirPatterns for each query
     * @throws XPathExpressionException Thrown if xpaths fail to compile or fail to evaluate
     *                                  +
     */
    public final List<NtcirPattern> extractPatterns() throws XPathExpressionException {
        final XPath xpath = XMLHelper.namespaceAwareXpath("t", NS_NII);
        final XPathExpression xNum = xpath.compile("./t:num");
        final XPathExpression xFormula = xpath.compile("./t:query/t:formula");
        final NonWhitespaceNodeList topicList = new NonWhitespaceNodeList(
                topics.getElementsByTagNameNS(NS_NII, "topic"));
        for (final Node node : topicList) {
            final String num = xNum.evaluate(node);
            final NonWhitespaceNodeList formulae = new NonWhitespaceNodeList((NodeList)
                    xFormula.evaluate(node, XPathConstants.NODESET));
            for (final Node formula : formulae) {
                final String id = formula.getAttributes().getNamedItem("id").getTextContent();
                final Node mathMLNode = NonWhitespaceNodeList.getFirstChild(formula);
                queryGenerator.setMainElement(NonWhitespaceNodeList.getFirstChild(mathMLNode));
                patterns.add(new NtcirPattern(num, id, queryGenerator.toString(), mathMLNode));
            }
        }
        return patterns;
    }
}
