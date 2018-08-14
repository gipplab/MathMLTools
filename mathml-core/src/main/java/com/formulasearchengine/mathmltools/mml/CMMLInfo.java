package com.formulasearchengine.mathmltools.mml;

import com.formulasearchengine.mathmltools.helper.XMLHelper;
import com.formulasearchengine.mathmltools.querygenerator.FirstXQueryGenerator;
import com.formulasearchengine.mathmltools.querygenerator.QVarXQueryGenerator;
import com.formulasearchengine.mathmltools.querygenerator.XQueryGenerator;
import com.formulasearchengine.mathmltools.xml.NonWhitespaceNodeList;
import com.formulasearchengine.mathmltools.xml.XmlNamespaceTranslator;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryExecutable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.util.*;

/**
 * @author Moritz Schubotz
 */
public class CMMLInfo extends CMMLInfoBase implements Document {

    //For XML math processing
    public static final String NS_MATHML = "http://www.w3.org/1998/Math/MathML";
    public static final String ROBERT_MINER_XSL = "com/formulasearchengine/mathmltools/mml/RobertMinerC2s.xsl";

    protected static final Logger LOG = LogManager.getLogger(CMMLInfo.class.getName());

    private static final String MATH_HEADER = "<?xml version=\"1.0\" ?>\n"
            + "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n"
            + "<semantics>\n";

    private static final String MATH_FOOTER = "</semantics>\n</math>";

    private static final List FORMULA_INDICATORS = Arrays.asList(
            "eq",
            "neq",
            "le",
            "ge",
            "leq",
            "geq",
            "equivalent"
    );


    private XQueryGenerator queryGenerator = null;
    private XQueryExecutable xQueryExecutable;
    private boolean isStrict;

    private Multiset<String> cachedElements = null;
    private Boolean cachedIsEquation = null;

    public CMMLInfo(Document cmml) {
        constructor(cmml, true, false);
    }

    public CMMLInfo(String s, boolean preserveAnnotations) {
        Document cmml = XMLHelper.string2Doc(s, true);
        constructor(cmml, true, preserveAnnotations);
    }

    public CMMLInfo(String s) {
        Document cmml = XMLHelper.string2Doc(s, true);
        constructor(cmml, true, false);
    }

    public CMMLInfo(CMMLInfo other) {
        cmmlDoc = (Document) other.cmmlDoc.cloneNode(true);
    }

    public CMMLInfo(Node f2) throws TransformerException {
        //TODO: Improve performance here
        Document cmml = XMLHelper.string2Doc(XMLHelper.printDocument(f2), true);
        constructor(cmml, true, false);
    }

    public CMMLInfo(Document document, boolean preserveAnnotations) {
        constructor(document, false, preserveAnnotations);
    }

    public CMMLInfo(CMMLInfoBase cmmlInfoBase) {
        new CMMLInfoBase();
    }

    public static CMMLInfo newFromSnippet(String snippet) {
        return new CMMLInfo(MATH_HEADER + snippet + MATH_FOOTER);
    }

    public final CMMLInfo abstract2CDs() {
        abstractNodeCD(cmmlDoc);
        fixNamespaces();
        return this;
    }

    public final CMMLInfo abstract2DTs() {
        abstractNodeDT(cmmlDoc, 0);
        fixNamespaces();
        return this;
    }

    private void abstractNodeCD(Node node) {
        final NonWhitespaceNodeList childNodes = new NonWhitespaceNodeList(node.getChildNodes());
        if (childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                abstractNodeCD(childNodes.item(i));
            }
        } else {
            node.setTextContent("");
            return;
        }
        String cd;
        try {
            cd = node.getAttributes().getNamedItem("cd").getNodeValue();
        } catch (final DOMException e) {
            LOG.warn("attribute not accessible or not found", e);
            //TODO: Implement CD fallback
            cd = "";
        } catch (final NullPointerException e) {
            cd = "";
        }
        if (cd != null && cd.isEmpty()) {
            return;
        }
        try {
            //LOG.info("Na guck: " + node.getNodeName());
            cmmlDoc.renameNode(node, "http://formulasearchengine.com/ns/pseudo/gen/cd", cd);
        } catch (final DOMException e) {
            LOG.error("cannot rename"
                    + node.getLocalName()
                    + cmmlDoc.toString(), e);
            return;
        }
        node.setTextContent("");
    }

    private void abstractNodeDT(Node node, Integer applies) {
        Set<String> levelGenerators = Sets.newHashSet("apply", "bind");
        Map<String, Integer> dTa = new HashMap<>();
        Boolean rename = false;
        dTa.put("cn", 0);
        dTa.put("cs", 0);
        dTa.put("bvar", 0);
        dTa.put("ci", null);
        dTa.put("csymbol", 1);
        dTa.put("share", 5);

        Integer level = applies;
        final String name = node.getLocalName();
        if (node.hasChildNodes()) {
            if (name != null && levelGenerators.contains(name)) {
                applies++;
            } else {
                applies = 0;
            }
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (i == 0) {
                    abstractNodeDT(childNodes.item(i), applies);
                } else {
                    abstractNodeDT(childNodes.item(i), 0);
                }
            }
        } else {
            node.setTextContent("");
            return;
        }

        if (dTa.containsKey(name)) {
            if (dTa.get(name)
                    != null) {
                level = dTa.get(name);
            }
            rename = true;
        }
        if (name != null && rename) {
            try {
                cmmlDoc.renameNode(node, "http://formulasearchengine.com/ns/pseudo/gen/datatype", "l"
                        + level);
            } catch (final DOMException e) {
                LOG.info("could not rename node" + name);
                return;
            }
        }
        if (node.getNodeType() == TEXT_NODE) {
            node.setTextContent("");
        }
    }


    private void constructor(Document cmml, Boolean fixNamespace, Boolean preserveAnnotations) {
        cmmlDoc = cmml;
        queryGenerator = QVarXQueryGenerator.getDefaultGenerator();
        if (fixNamespace) {
            fixNamespaces();
        }
        if (!preserveAnnotations) {
            removeAnnotations();
        }
        removeElementsByName("id");
    }

    private void fixNamespaces() {
        Node math = new NonWhitespaceNodeList(cmmlDoc.getElementsByTagNameNS("*", "math")).getFirstElement();
        if (math == null) {
            try {
                LOG.error("No mathml element found in:\n" + XMLHelper.printDocument(cmmlDoc));
            } catch (TransformerException e) {
                LOG.error("No mathml element found in unpritnabel input.");
            }
            return;
        }
        try {
            math.getAttributes().removeNamedItem("xmlns");
        } catch (final DOMException e) {
            //Remove if it exists, ignore any error thrown if it does not exist
        }
        new XmlNamespaceTranslator()
                .setDefaultNamespace(NS_MATHML)
                .addTranslation("m", NS_MATHML)
                .addTranslation("mws", "http://search.mathweb.org/ns")
                //TODO: make option to keep it
                .addUnwantedAttribute("xml:id")
                .translateNamespaces(cmmlDoc, "");
        try {
            math.getAttributes().removeNamedItem("xmlns:m");
        } catch (final DOMException e) {
            //Ignore any error thrown if element does not exist
        }
    }

    public final Double getCoverage(Multiset<String> queryTokens) {
        if (queryTokens.isEmpty()) {
            return 1.0;
        }
        final Multiset<String> our = getElements(true);
        if (our.contains(queryTokens)) {
            return 1.0;
        } else {
            final HashMultiset<String> tmp = HashMultiset.create();
            tmp.addAll(queryTokens);
            tmp.removeAll(our);
            return 1 - (double) tmp.size() / (double) queryTokens.size();
        }
    }

    public final Integer getDepth(XQueryExecutable query) {
        Document doc = null;
        try {
            doc = XMLHelper.runXQuery(query, toString());
        } catch (final SaxonApiException | ParserConfigurationException e) {
            LOG.error("Problem during document preparation for depth processing", e);
            return null;
        }
        final NodeList elementsB = doc.getElementsByTagName("p");
        if (elementsB.getLength() == 0) {
            return null;
        }
        Integer depth = Integer.MAX_VALUE;
        //find the match with lowest depth
        for (int i = 0; i < elementsB.getLength(); i++) {
            final String path = elementsB.item(i).getTextContent();
            final int currentDepth = path.split("/").length;
            if (currentDepth < depth) {
                depth = currentDepth;
            }
        }
        return depth;
    }

    public final Document getDoc() {
        return cmmlDoc;
    }

    public final Multiset<String> getElements() {
        try {
            Multiset<String> list = HashMultiset.create();
            XPath xpath = XMLHelper.namespaceAwareXpath("m", NS_MATHML);
            XPathExpression xEquation = xpath.compile("*//m:ci|*//m:co|*//m:cn");
            NonWhitespaceNodeList identifiers = new NonWhitespaceNodeList((NodeList) xEquation.evaluate(cmmlDoc, XPathConstants.NODESET));
            for (Node identifier : identifiers) {
                list.add(identifier.getTextContent().trim());
            }
            return list;
        } catch (final XPathExpressionException e) {
            LOG.warn("Unable to parse elements: "
                    + cmmlDoc.toString(), e);
        }
        return HashMultiset.create();
    }

    public final Multiset<String> getElements(boolean useCache) {
        if (cachedElements == null || !useCache) {
            synchronized (this) {
                if (cachedElements == null) {
                    cachedElements = getElements();
                }
            }
        }
        return cachedElements;
    }

    public final XQueryExecutable getXQuery(Boolean useCache) {
        if (xQueryExecutable == null && !useCache) {
            getXQuery();
        }
        return xQueryExecutable;
    }

    public final XQueryExecutable getXQuery() {
        final String queryString = getXQueryString();
        if (queryString == null) {
            return null;
        }
        xQueryExecutable = XMLHelper.compileXQuerySting(queryString);
        return xQueryExecutable;
    }

    /**
     * This method was used to test the "first" version of the query generator
     * and for comparison against the current implementation.
     */
    @Deprecated
    public String getXQueryStringBackup() {
        return new FirstXQueryGenerator(cmmlDoc).toString();
    }

    public final String getXQueryString() {
        return queryGenerator.generateQuery(cmmlDoc);
    }

    public XQueryGenerator getQueryGenerator() {
        return queryGenerator;
    }

    public void setQueryGenerator(XQueryGenerator queryGenerator) {
        this.queryGenerator = queryGenerator;
    }

    public final boolean isEquation(boolean useCache) throws XPathExpressionException {
        if (cachedIsEquation == null || !useCache) {
            synchronized (this) {
                if (cachedIsEquation == null) {
                    cachedIsEquation = isEquation();
                }
            }
        }
        return cachedIsEquation;
    }

    public final boolean isEquation() throws XPathExpressionException {
        Node cmmlMain = XMLHelper.getMainElement(cmmlDoc);
        XPath xpath = XMLHelper.namespaceAwareXpath("m", NS_MATHML);
        XPathExpression xEquation = xpath.compile("./m:apply/*");

        NonWhitespaceNodeList elementsB = new NonWhitespaceNodeList(XMLHelper.getElementsB(cmmlMain, xEquation));
        if (elementsB.getLength() > 0) {
            String name = elementsB.item(0).getLocalName();
            return FORMULA_INDICATORS.contains(name);
        }
        return false;
    }

    public final Boolean isMatch(XQueryExecutable query) {
        Document doc = null;
        try {
            doc = XMLHelper.runXQuery(query, toString());
            final NodeList elementsB = doc.getElementsByTagName("p");
            return elementsB.getLength() != 0;
        } catch (final SaxonApiException | ParserConfigurationException e) {
            LOG.warn("Unable to parse if query is a match. ", e);
        }
        return null;
    }

    private void removeAnnotations() {
        removeElementsByName("annotation");
        removeElementsByName("annotation-xml");
    }

    private void removeElementsByName(String name) {
        final NonWhitespaceNodeList nodes = new NonWhitespaceNodeList(cmmlDoc.getElementsByTagNameNS("*", name));
        for (final Node node : nodes) {
            // be sure not to remove content MathML
            if (!node.getAttributes().getNamedItem("encoding").getTextContent().equals("MathML-Content")) {
                node.getParentNode().removeChild(node);
            }
        }
    }

    public final CMMLInfo toDataCmml() {
        try {
            cmmlDoc = XMLHelper.xslTransform(cmmlDoc, ROBERT_MINER_XSL);
        } catch (TransformerException | ParserConfigurationException e) {
            LOG.warn("Unable to convert to data cmml", e);
        }
        return this;
    }

    public final CMMLInfo toStrictCmml() {
        try {
            cmmlDoc = XMLHelper.xslTransform(cmmlDoc, ROBERT_MINER_XSL);
            isStrict = true;
        } catch (final TransformerException | ParserConfigurationException e) {
            LOG.warn("Unable to convert to strict cmml :"
                    + cmmlDoc.toString(), e);
        }
        return this;
    }

    @Override
    public final String toString() {
        try {
            return XMLHelper.printDocument(cmmlDoc);
        } catch (final TransformerException e) {
            return "cmml not printable";
        }
    }
}
