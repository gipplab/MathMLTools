package com.formulasearchengine.mathmltools.mml;

import com.formulasearchengine.mathmlquerygenerator.XQueryGenerator;
import com.formulasearchengine.mathmltools.xmlhelper.NonWhitespaceNodeList;
import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import com.formulasearchengine.mathmltools.xmlhelper.XmlNamespaceTranslator;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryExecutable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.*;

import static com.formulasearchengine.mathmltools.xmlhelper.XMLHelper.getElementsB;


public class CMMLInfo implements Document {
  //For XML math processing
  public static final String NS_MATHML = "http://www.w3.org/1998/Math/MathML";
  protected static final Log LOG = LogFactory.getLog(CMMLInfo.class);
  private static final String FN_PATH_FROM_ROOT = "declare namespace functx = \"http://www.functx.com\";\n" +
      "declare function functx:path-to-node\n" +
      "  ( $nodes as node()* )  as xs:string* {\n" +
      "\n" +
      "$nodes/string-join(ancestor-or-self::*/name(.), '/')\n" +
      " } ;";
  private static final String XQUERY_HEADER = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";\n" +
      FN_PATH_FROM_ROOT +
      "<result>{";
  public static final String ROBERT_MINER_XSL = "com/formulasearchengine/mathmltools/mml/RobertMinerC2s.xsl";
  final String XQUERY_FOOTER = "<element><x>{$x}</x><p>{data(functx:path-to-node($x))}</p></element>}\n" +
      "</result>";
  private final static String FN_PATH_FROM_ROOT2 = "declare function path-from-root($x as node()) {\n" +
      " if ($x/parent::*) then\n" +
      " concat( path-from-root($x/parent::*), \"/\", node-name($x) )\n" +
      " else\n" +
      " concat( \"/\", node-name($x) )\n" +
      " };\n";
  private static final String MATH_HEADER = "<?xml version=\"1.0\" ?>\n" +
      "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
      "<semantics>\n";
  private static final String MATH_FOOTER = "</semantics>\n" +
      "</math>";
  private static final List formulaIndicators = Arrays.asList(
      "eq",
      "neq",
      "le",
      "ge",
      "leq",
      "geq",
      "equivalent"
  );
  private Document cmmlDoc;
  private XQueryExecutable xQueryExecutable;
  private boolean isStrict;

  public CMMLInfo(Document cmml) {
    constructor(cmml, true, false);
  }


  public CMMLInfo(String s) throws IOException, ParserConfigurationException {
    Document cmml = XMLHelper.String2Doc(s, true);
    constructor(cmml, true, false);
  }

  public CMMLInfo(CMMLInfo other) {
    cmmlDoc = (Document) other.cmmlDoc.cloneNode(true);
  }

  public CMMLInfo(Node f2) throws TransformerException, IOException, ParserConfigurationException {
    //TODO: Improve performance here
    Document cmml = XMLHelper.String2Doc(XMLHelper.printDocument(f2), true);
    constructor(cmml, true, false);
  }

  public static CMMLInfo newFromSnippet(String snippet) throws IOException, ParserConfigurationException {
    return new CMMLInfo(MATH_HEADER + snippet + MATH_FOOTER);
  }

  public final Document getDoc() {
    return cmmlDoc;
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
      //Remove if it exists, ignore any errors thrown if it does not exist
    }
    new XmlNamespaceTranslator()
        .setDefaultNamespace(NS_MATHML)
        .addTranslation("m", NS_MATHML)
        .addTranslation("mws", "http://search.mathweb.org/ns")
        //TODO: make option to keep it
        .addUnwantedAttribute("xml:id")
        .translateNamespaces(cmmlDoc);
    try {
      math.getAttributes().removeNamedItem("xmlns:m");
    } catch (final DOMException e) {
      //Ignore any errors thrown if element does not exist
    }
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

  private void removeAnnotations() {
    removeElementsByName("annotation");
    removeElementsByName("annotation-xml");
  }

  private void constructor(Document cmml, Boolean fixNamespace, Boolean preserveAnnotations) {
    cmmlDoc = cmml;
    if (fixNamespace) {
      fixNamespaces();
    }
    if (!preserveAnnotations) {
      removeAnnotations();
    }
    removeElementsByName("id");
  }

  @Override
  public final CMMLInfo clone() {
    return new CMMLInfo(this);
  }

  private void removeNonCD() {

  }

  public final CMMLInfo toStrictCmmlCont() {
    try {
      removeNonCD();
      cmmlDoc = XMLHelper.XslTransform(cmmlDoc, ROBERT_MINER_XSL);
      isStrict = true;
    } catch (final TransformerException | ParserConfigurationException e) {
      LOG.warn("Unable to convert to strict cmml :" + cmmlDoc.toString(), e);
    }
    return this;
  }

  public final CMMLInfo toStrictCmml() throws TransformerException, ParserConfigurationException {
    cmmlDoc = XMLHelper.XslTransform(cmmlDoc, ROBERT_MINER_XSL);
    return this;
  }

  public final boolean isEquation() throws XPathExpressionException {
    Node cmmlMain = XMLHelper.getMainElement(cmmlDoc);
    XPath xpath = XMLHelper.namespaceAwareXpath("m", NS_MATHML);
    XPathExpression xEquation = xpath.compile("./m:apply/*");

    NonWhitespaceNodeList elementsB = new NonWhitespaceNodeList(getElementsB(cmmlMain, xEquation));
    if (elementsB.getLength() > 0) {
      String name = elementsB.item(0).getLocalName();
      if (formulaIndicators.contains(name)) {
        return true;
      }
    }
    return false;
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
      LOG.warn("Unable to parse elements: " + cmmlDoc.toString(), e);
    }
    return HashMultiset.create();
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
      //TODO: Implement CD fallback
      cd = "";
    } catch (NullPointerException e) {
      cd = "";
    }
    if (cd != null && cd.isEmpty()) {
      return;
    }
    try {
      cmmlDoc.renameNode(node, "http://formulasearchengine.com/ns/pseudo/gen/cd", cd);
    } catch (final DOMException e) {
      LOG.error("cannot rename" + node.getLocalName() + cmmlDoc.toString(), e);
      return;
    }
    node.setTextContent("");
  }

  private void abstractNodeDT(Node node, Integer applies) {
    Set<String> levelGenerators = Sets.newHashSet("apply", "bind");
    Map<String, Integer> DTa = new HashMap<>();
    Boolean rename = false;
    DTa.put("cn", 0);
    DTa.put("cs", 0);
    DTa.put("bvar", 0);
    DTa.put("ci", null);
    DTa.put("csymbol", 1);
    DTa.put("share", 5);

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

    if (DTa.containsKey(name)) {
      if (DTa.get(name) != null) {
        level = DTa.get(name);
      }
      rename = true;
    }
    if (name != null && rename) {
      try {
        cmmlDoc.renameNode(node, "http://formulasearchengine.com/ns/pseudo/gen/datatype", "l" + level);
      } catch (final DOMException e) {
        LOG.info("could not rename node" + name);
        return;
      }
    }
    if (node.getNodeType() == TEXT_NODE) {
      node.setTextContent("");
    }
  }

  public final CMMLInfo abstract2CDs() {
    abstractNodeCD(cmmlDoc);
    fixNamespaces();
    return this;
  }

  public final Node abstract2DTs() {
    abstractNodeDT(cmmlDoc, 0);
    fixNamespaces();
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

  public final String getXQueryString() {
    final XQueryGenerator gen = new XQueryGenerator(cmmlDoc);
    gen.setNamespace(XQUERY_HEADER);
    gen.setPathToRoot(".");
    gen.setReturnFormat(XQUERY_FOOTER);
    gen.setAddQvarMap(false);
    final String queryString = gen.toString();
    return queryString;
  }

  public final CMMLInfo toDataCmml() {
    try {
      cmmlDoc = XMLHelper.XslTransform(cmmlDoc, ROBERT_MINER_XSL);
    } catch (TransformerException | ParserConfigurationException e) {
      LOG.warn("Unable to convert to data cmml", e);
    }
    return this;
  }

  public final Double getCoverage(Multiset queryTokens) {
    if (queryTokens.isEmpty()) {
      return 1.0;
    }
    final Multiset<String> our = getElements();
    if (our.contains(queryTokens)) {
      return 1.0;
    } else {
      final HashMultiset<String> tmp = HashMultiset.create();
      tmp.addAll(queryTokens);
      tmp.removeAll(our);
      return 1 - Double.valueOf(tmp.size()) / Double.valueOf(queryTokens.size());
    }
  }

  @Override
  public final DocumentType getDoctype() {
    return cmmlDoc.getDoctype();
  }

  @Override
  public final EntityReference createEntityReference(String s) throws DOMException {
    return cmmlDoc.createEntityReference(s);
  }

  @Override
  public final void normalizeDocument() {
    cmmlDoc.normalizeDocument();
  }

  @Override
  public final Object getUserData(String s) {
    return cmmlDoc.getUserData(s);
  }

  @Override
  public final Node getNextSibling() {
    return cmmlDoc.getNextSibling();
  }

  @Override
  public final CDATASection createCDATASection(String s) throws DOMException {
    return cmmlDoc.createCDATASection(s);
  }

  @Override
  public final Node getPreviousSibling() {
    return cmmlDoc.getPreviousSibling();
  }

  @Override
  public final boolean isSameNode(Node node) {
    return cmmlDoc.isSameNode(node);
  }

  @Override
  public final Attr createAttributeNS(String s, String s1) throws DOMException {
    return cmmlDoc.createAttributeNS(s, s1);
  }

  @Override
  public final NodeList getChildNodes() {
    return cmmlDoc.getChildNodes();
  }

  @Override
  public final Node getFirstChild() {
    return cmmlDoc.getFirstChild();
  }

  @Override
  public final Object setUserData(String s, Object o, UserDataHandler userDataHandler) {
    return cmmlDoc.setUserData(s, o, userDataHandler);
  }

  @Override
  public final String getNamespaceURI() {
    return cmmlDoc.getNamespaceURI();
  }

  @Override
  public final Node renameNode(Node node, String s, String s1) throws DOMException {
    return cmmlDoc.renameNode(node, s, s1);
  }

  @Override
  public final Node insertBefore(Node node, Node node1) throws DOMException {
    return cmmlDoc.insertBefore(node, node1);
  }

  @Override
  public final String getXmlVersion() {
    return cmmlDoc.getXmlVersion();
  }

  @Override
  public final void setXmlVersion(String s) throws DOMException {
    cmmlDoc.setXmlVersion(s);
  }

  @Override
  public final String getDocumentURI() {
    return cmmlDoc.getDocumentURI();
  }

  @Override
  public final void setDocumentURI(String s) {
    cmmlDoc.setDocumentURI(s);
  }

  @Override
  public final String getInputEncoding() {
    return cmmlDoc.getInputEncoding();
  }

  @Override
  public final NodeList getElementsByTagNameNS(String s, String s1) {
    return cmmlDoc.getElementsByTagNameNS(s, s1);
  }

  @Override
  public final DocumentFragment createDocumentFragment() {
    return cmmlDoc.createDocumentFragment();
  }

  @Override
  public final String getPrefix() {
    return cmmlDoc.getPrefix();
  }

  @Override
  public final void setPrefix(String s) throws DOMException {
    cmmlDoc.setPrefix(s);
  }

  @Override
  public final String getTextContent() throws DOMException {
    return cmmlDoc.getTextContent();
  }

  @Override
  public final void setTextContent(String s) throws DOMException {
    cmmlDoc.setTextContent(s);
  }

  @Override
  public final void normalize() {
    cmmlDoc.normalize();
  }

  @Override
  public final Node removeChild(Node node) throws DOMException {
    return cmmlDoc.removeChild(node);
  }

  @Override
  public final boolean isSupported(String s, String s1) {
    return cmmlDoc.isSupported(s, s1);
  }

  @Override
  public final ProcessingInstruction createProcessingInstruction(String s, String s1) throws DOMException {
    return cmmlDoc.createProcessingInstruction(s, s1);
  }

  @Override
  public final short getNodeType() {
    return cmmlDoc.getNodeType();
  }

  @Override
  public final Document getOwnerDocument() {
    return cmmlDoc.getOwnerDocument();
  }

  @Override
  public final Comment createComment(String s) {
    return cmmlDoc.createComment(s);
  }

  @Override
  public final Attr createAttribute(String s) throws DOMException {
    return cmmlDoc.createAttribute(s);
  }

  @Override
  public final boolean getStrictErrorChecking() {
    return cmmlDoc.getStrictErrorChecking();
  }

  @Override
  public final void setStrictErrorChecking(boolean b) {
    cmmlDoc.setStrictErrorChecking(b);
  }

  @Override
  public final NamedNodeMap getAttributes() {
    return cmmlDoc.getAttributes();
  }

  @Override
  public final String getBaseURI() {
    return cmmlDoc.getBaseURI();
  }

  @Override
  public final Element getDocumentElement() {
    return cmmlDoc.getDocumentElement();
  }

  @Override
  public final DOMConfiguration getDomConfig() {
    return cmmlDoc.getDomConfig();
  }

  @Override
  public final DOMImplementation getImplementation() {
    return cmmlDoc.getImplementation();
  }

  @Override
  public final String getNodeValue() throws DOMException {
    return cmmlDoc.getNodeValue();
  }

  @Override
  public final void setNodeValue(String s) throws DOMException {
    cmmlDoc.setNodeValue(s);
  }

  @Override
  public final boolean hasAttributes() {
    return cmmlDoc.hasAttributes();
  }

  @Override
  public final Element createElementNS(String s, String s1) throws DOMException {
    return cmmlDoc.createElementNS(s, s1);
  }

  @Override
  public final Element createElement(String s) throws DOMException {
    return cmmlDoc.createElement(s);
  }

  @Override
  public final Node importNode(Node node, boolean b) throws DOMException {
    return cmmlDoc.importNode(node, b);
  }

  @Override
  public final Text createTextNode(String s) {
    return cmmlDoc.createTextNode(s);
  }

  @Override
  public final String lookupPrefix(String s) {
    return cmmlDoc.lookupPrefix(s);
  }

  @Override
  public final boolean isEqualNode(Node node) {
    return cmmlDoc.isEqualNode(node);
  }

  @Override
  public final NodeList getElementsByTagName(String s) {
    return cmmlDoc.getElementsByTagName(s);
  }

  @Override
  public final Node getLastChild() {
    return cmmlDoc.getLastChild();
  }

  @Override
  public final Node appendChild(Node node) throws DOMException {
    return cmmlDoc.appendChild(node);
  }

  @Override
  public final short compareDocumentPosition(Node node) throws DOMException {
    return cmmlDoc.compareDocumentPosition(node);
  }

  @Override
  public final Object getFeature(String s, String s1) {
    return cmmlDoc.getFeature(s, s1);
  }

  @Override
  public final Element getElementById(String s) {
    return cmmlDoc.getElementById(s);
  }

  @Override
  public final boolean isDefaultNamespace(String s) {
    return cmmlDoc.isDefaultNamespace(s);
  }

  @Override
  public final String lookupNamespaceURI(String s) {
    return cmmlDoc.lookupNamespaceURI(s);
  }

  @Override
  public final String getLocalName() {
    return cmmlDoc.getLocalName();
  }

  @Override
  public final String getXmlEncoding() {
    return cmmlDoc.getXmlEncoding();
  }

  @Override
  public final String getNodeName() {
    return cmmlDoc.getNodeName();
  }

  @Override
  public final Node getParentNode() {
    return cmmlDoc.getParentNode();
  }

  @Override
  public final Node cloneNode(boolean b) {
    return cmmlDoc.cloneNode(b);
  }

  @Override
  public final boolean getXmlStandalone() {
    return cmmlDoc.getXmlStandalone();
  }

  @Override
  public final void setXmlStandalone(boolean b) throws DOMException {
    cmmlDoc.setXmlStandalone(b);
  }

  @Override
  public final Node replaceChild(Node node, Node node1) throws DOMException {
    return cmmlDoc.replaceChild(node, node1);
  }

  @Override
  public final boolean hasChildNodes() {
    return cmmlDoc.hasChildNodes();
  }

  @Override
  public final Node adoptNode(Node node) throws DOMException {
    return cmmlDoc.adoptNode(node);
  }


}
