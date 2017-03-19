package com.formulasearchengine.mathmlquerygenerator;


import com.formulasearchengine.mathmltools.xmlhelper.NonWhitespaceNodeList;
import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import com.google.common.collect.Lists;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts MathML queries into XQueries, given a namespace, a xquery/xpath to the root elements, and a xquery return format.
 * The variable $x always represents a hit, so you can refer to $x in the return format as the result node.
 * If addQvarMap is turned on, the function local:qvarMap($parentNode) always represents a map of qvars to their
 * respective formula ID, so you can refer to local:qvarMap($parentNode) in the footer to return qvar results.
 * If findRootApply is turned on, the xquery takes on a recursive format. The variable $rootApply represents the root
 * apply node and the variable $depth represents the depth of the matched node. The root apply node has a depth of 0.
 * Created by Moritz Schubotz on 9/3/14.
 * Translated from http://git.wikimedia.org/blob/mediawiki%2Fextensions%2FMathSearch.git/31a80ae48d1aaa50da9103cea2e45a8dc2204b39/XQueryGenerator.php
 */
@SuppressWarnings("WeakerAccess")
public class XQueryGenerator {
    //Qvar map of qvar name to XPaths referenced by each qvar
    private Map<String, ArrayList<String>> qvar = new LinkedHashMap<>();
    private String relativeXPath = "";
    private String exactMatchXQuery = "";
    private String lengthConstraint = "";
    private String qvarConstraint = "";
    private String qvarMapVariable = "";
    private String namespace = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";";
    private String pathToRoot = "db2-fn:xmlcolumn(\"math.math_mathml\")";
    private String returnFormat = "data($m/*[1]/@alttext)";
    private Node mainElement = null;
    private boolean restrictLength = true;
    private boolean addQvarMap = true;
    private boolean findRootApply = false;

    /**
     * Constructs a basic generator from an XML document given as a string.
     *
     * @param input XML Document as a string
     */
    public XQueryGenerator(String input)
            throws IOException, SAXException, ParserConfigurationException {
        final Document xml = XMLHelper.String2Doc(input, true);
        this.mainElement = XMLHelper.getMainElement(xml);
    }

    /**
     * Constructs a generator from a Document XML object.
     *
     * @param xml Document XML object
     */
    public XQueryGenerator(Document xml) {
        this.mainElement = XMLHelper.getMainElement(xml);
    }

    public boolean isRestrictLength() {
        return restrictLength;
    }

    /**
     * If set to true a query like $x+y$ does not match $x+y+z$.
     *
     * @param restrictLength
     */
    public XQueryGenerator setRestrictLength(boolean restrictLength) {
        this.restrictLength = restrictLength;
        this.lengthConstraint = "";
        this.relativeXPath = "";
        qvar = new HashMap<>();
        return this;
    }

    public boolean isAddQvarMap() {
        return addQvarMap;
    }

    /**
     * Determines whether or not the $q variable is generated with a map of qvar names to their respective xml:id
     */
    public XQueryGenerator setAddQvarMap(boolean addQvarMap) {
        this.addQvarMap = addQvarMap;
        return this;
    }

    /**
     * Determines whether or not the $rootApply and the $depth variables are generated using recursion to find the root
     * node of the matched equation and the depth of the hit.
     */
    public XQueryGenerator setFindRootApply(boolean findRootApply) {
        this.findRootApply = findRootApply;
        return this;
    }

    public String getReturnFormat() {
        return returnFormat;
    }

    public XQueryGenerator setReturnFormat(String returnFormat) {
        this.returnFormat = returnFormat;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public XQueryGenerator setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public XQueryGenerator setPathToRoot(String pathToRoot) {
        this.pathToRoot = pathToRoot;
        return this;
    }

    /**
     * Resets the current xQuery expression and sets a new main element.
     *
     * @param mainElement
     */
    public void setMainElement(Node mainElement) {
        this.mainElement = mainElement;
        qvar = new LinkedHashMap<>();
        relativeXPath = "";
        lengthConstraint = "";
    }

    /**
     * Generates the constraints of the XQuery and then builds the XQuery and returns it as a string
     *
     * @return XQuery as string. Returns null if no main element set.
     */
    public String toString() {
        if (mainElement == null) {
            return null;
        }
        generateConstraints();
        if (findRootApply) {
            return getRecursiveString();
        } else {
            return getDefaultString();
        }
    }

    private void generateConstraints() {
        qvar = new LinkedHashMap<>();
        exactMatchXQuery = generateSimpleConstraints(mainElement, true);
        generateQvarConstraints();
    }

    /**
     * Builds the XQuery as a string. Uses the default format of looping through all apply nodes.
     *
     * @return XQuery as string
     */
    private String getDefaultString() {
        final StringBuilder outBuilder = new StringBuilder();
        if (!namespace.isEmpty()) {
            outBuilder.append(namespace).append("\n");
        }
        if (!qvarMapVariable.isEmpty() && addQvarMap) {
            outBuilder.append(qvarMapVariable).append("\n");
        }
        outBuilder.append("for $m in ").append(pathToRoot).append(" return\n")
                .append("for $x in $m//*:").append(NonWhitespaceNodeList.getFirstChild(mainElement).getLocalName())
                .append("\n").append(exactMatchXQuery);
        if (!lengthConstraint.isEmpty() || !qvarConstraint.isEmpty()) {
            outBuilder.append("\n").append("where").append("\n");
            if (lengthConstraint.isEmpty()) {
                outBuilder.append(qvarConstraint);
            } else {
                outBuilder.append(lengthConstraint)
                        .append(qvarConstraint.isEmpty() ? "" : "\n and ").append(qvarConstraint);
            }
        }
        outBuilder.append("\n\n").append("return").append("\n").append(returnFormat);
        return outBuilder.toString();
    }

    /**
     * Builds the XQuery as a string. Uses the recursive format of recursively looping through the documents.
     * This enables the $depth and the $rootApply variables.
     *
     * @return XQuery as string
     */
    private String getRecursiveString() {
        final StringBuilder outBuilder = new StringBuilder();
        if (!namespace.isEmpty()) {
            outBuilder.append(namespace).append("\n");
        }
        if (!qvarMapVariable.isEmpty() && addQvarMap) {
            outBuilder.append(qvarMapVariable).append("\n");
        }

        outBuilder.append("\ndeclare function local:compareApply($rootApply, $depth, $x ) {\n")
                .append("(for $child in $x/* return local:compareApply(\n")
                .append("if (empty($rootApply) and $child/name() = \"apply\") then $child else $rootApply,\n")
                .append("if (empty($rootApply) and $child/name() = \"apply\") then 0 else $depth+1, $child),\n")
                .append("if ($x/name() = \"apply\"\n")
                .append(" and $x").append(exactMatchXQuery).append("\n");
        if (!lengthConstraint.isEmpty()) {
            outBuilder.append(" and ").append(lengthConstraint).append("\n");
        }
        if (!qvarConstraint.isEmpty()) {
            outBuilder.append(" and ").append(qvarConstraint).append("\n");
        }
        outBuilder.append(" ) then\n")
                .append(returnFormat).append("\n")
                .append("else ()\n")
                .append(")};\n\n")
                .append("for $m in ").append(pathToRoot).append(" return\n")
                .append("local:compareApply((), 0, $m)");

        return outBuilder.toString();
    }

    /**
     * Uses the qvar map to generate a XQuery string containing qvar constraints,
     * and the qvar map variable which maps qvar names to their respective formula ID's in the result.
     */
    private void generateQvarConstraints() {
        final StringBuilder qvarConstrBuilder = new StringBuilder();
        final StringBuilder qvarMapStrBuilder = new StringBuilder();
        final Iterator<Map.Entry<String, ArrayList<String>>> entryIterator = qvar.entrySet().iterator();
        if (entryIterator.hasNext()) {
            qvarMapStrBuilder.append("declare function local:qvarMap($x) {\n map {");

            while (entryIterator.hasNext()) {
                final Map.Entry<String, ArrayList<String>> currentEntry = entryIterator.next();

                final Iterator<String> valueIterator = currentEntry.getValue().iterator();
                final String firstValue = valueIterator.next();

                qvarMapStrBuilder.append('"').append(currentEntry.getKey()).append('"')
                        .append(" : (data($x").append(firstValue).append("/@xml:id)");

                //check if there are additional values that we need to constrain
                if (valueIterator.hasNext()) {
                    if (qvarConstrBuilder.length() > 0) {
                        //only add beginning and if it's an additional constraint in the aggregate qvar string
                        qvarConstrBuilder.append("\n and ");
                    }
                    while (valueIterator.hasNext()) {
                        //process second value onwards
                        final String currentValue = valueIterator.next();
                        qvarMapStrBuilder.append(",data($x").append(currentValue).append("/@xml-id)");
                        //These constraints specify that the same qvars must refer to the same nodes,
                        //using the XQuery "=" equality
                        //This is equality based on: same text, same node names, and same children nodes
                        qvarConstrBuilder.append("$x").append(firstValue).append(" = $x").append(currentValue);
                        if (valueIterator.hasNext()) {
                            qvarConstrBuilder.append(" and ");
                        }
                    }
                }
                qvarMapStrBuilder.append(')');
                if (entryIterator.hasNext()) {
                    qvarMapStrBuilder.append(',');
                }
            }
            qvarMapStrBuilder.append("}\n};");
        }
        qvarMapVariable = qvarMapStrBuilder.toString();
        qvarConstraint = qvarConstrBuilder.toString();
    }


    private String generateSimpleConstraints(Node node) {
        return generateSimpleConstraints(node, false);
    }

    /**
     * Generates qvar map, length constraint, and returns exact match XQuery query for all child nodes of the given node.
     * Called recursively to generate the query for the entire query document.
     *
     * @param node   Element from which to get children to generate constraints.
     * @param isRoot Whether or not node should be treated as the root element of the document (the root element
     *               is not added as a constraint here, but in getString())
     * @return Exact match XQuery string
     */
    private String generateSimpleConstraints(Node node, boolean isRoot) {
        //Index of child node
        int childElementIndex = 0;
        final StringBuilder out = new StringBuilder();
        boolean queryHasText = false;
        final NonWhitespaceNodeList nodeList = new NonWhitespaceNodeList(node.getChildNodes());

        for (final Node child : nodeList) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                //If an element node and not an attribute or text node, add to xquery and increment index
                childElementIndex++;

                if (child.getNodeName().equals("mws:qvar")) {
                    //If qvar, add to qvar map
                    String qvarName = child.getTextContent();
                    if (qvarName.isEmpty()) {
                        qvarName = child.getAttributes().getNamedItem("name").getTextContent();
                    }
                    if (qvar.containsKey(qvarName)) {
                        qvar.get(qvarName).add(relativeXPath + "/*[" + childElementIndex + "]");
                    } else {
                        qvar.put(qvarName, Lists.newArrayList(relativeXPath + "/*[" + childElementIndex + "]"));
                    }
                } else if (child.getLocalName() != null && XMLHelper.ANNOTATION_XML_PATTERN.matcher(child.getLocalName()).matches()) {
                    //Ignore annotations and presentation mathml
                } else {
                    if (queryHasText) {
                        //Add another condition on top of existing condition in query
                        out.append(" and ");
                    } else {
                        queryHasText = true;
                    }

                    //The first direct child of the root element is added as a constraint in getString()
                    //so ignore it here
                    if (!isRoot) {
                        //Add constraint for current child element
                        out.append("*[").append(childElementIndex).append("]/name() = '").
                                append(child.getLocalName()).append("'");
                    }
                    if (child.hasChildNodes()) {
                        if (!isRoot) {
                            relativeXPath += "/*[" + childElementIndex + "]";
                            //Add relative constraint so this can be recursively called
                            out.append(" and *[").append(childElementIndex).append("]");
                        }
                        final String constraint = generateSimpleConstraints(child);
                        if (!constraint.isEmpty()) {
                            //This constraint appears as a child of the relative constraint above (e.g. [*1][constraint])
                            out.append("[").append(constraint).append("]");
                        }
                    }
                }
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                //Text nodes are always leaf nodes
                out.append("./text() = '").append(child.getNodeValue().trim()).append("'");
            }
        }
        //for child : nodelist

        if (!isRoot && restrictLength) {
            if (lengthConstraint.isEmpty()) {
                //Initialize constraint
                lengthConstraint += "fn:count($x" + relativeXPath + "/*) = " + childElementIndex;
            } else {
                //Add as additional constraint
                lengthConstraint += "\n" + " and fn:count($x" + relativeXPath + "/*) = " + childElementIndex;
            }
        }

        if (!relativeXPath.isEmpty()) {
            relativeXPath = relativeXPath.substring(0, relativeXPath.lastIndexOf("/"));
        }

        return out.toString();
    }

    public Map<String, ArrayList<String>> getQvar() {
        if (qvar.isEmpty()) {
            generateConstraints();
        }
        return qvar;
    }
}
