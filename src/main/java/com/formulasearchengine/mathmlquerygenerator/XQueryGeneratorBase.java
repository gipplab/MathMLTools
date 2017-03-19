package com.formulasearchengine.mathmlquerygenerator;

import com.formulasearchengine.mathmltools.xmlhelper.NonWhitespaceNodeList;
import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Created by Moritz on 19.03.2017.
 */
public class XQueryGeneratorBase {
    private String namespace = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";";
    //Qvar map of qvar name to XPaths referenced by each qvar
    private String relativeXPath = "";
    private String exactMatchXQuery = "";
    private String lengthConstraint = "";
    private String pathToRoot = "db2-fn:xmlcolumn(\"math.math_mathml\")";
    private String returnFormat = "data($m/*[1]/@alttext)";
    private Node mainElement = null;
    private boolean restrictLength = true;


    /**
     * Constructs a generator from a Document XML object.
     *
     * @param xml Document XML object
     */
    public XQueryGeneratorBase(Document xml, String namespace, String pathToRoot, String returnFormat) {
        this.mainElement = XMLHelper.getMainElement(xml);
        this.namespace = namespace;
        this.pathToRoot = pathToRoot;
        this.returnFormat = returnFormat;
    }

    public XQueryGeneratorBase() {
    }

    protected void generateConstraints() {
        exactMatchXQuery = generateSimpleConstraints(mainElement, true);
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

                if (handleSpecialElements(child, childElementIndex) || child.getLocalName() != null
                        && XMLHelper.ANNOTATION_XML_PATTERN.matcher(child.getLocalName()).matches()) {
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

    /**
     * Builds the XQuery as a string. Uses the default format of looping through all apply nodes.
     *
     * @return XQuery as string
     */
    protected String getDefaultString() {
        final StringBuilder outBuilder = new StringBuilder();
        if (!namespace.isEmpty()) {
            outBuilder.append(namespace).append("\n");
        }
        outBuilder.append("for $m in ").append(pathToRoot).append(" return\n")
                .append("for $x in $m//*:").append(NonWhitespaceNodeList.getFirstChild(mainElement).getLocalName())
                .append("\n").append(exactMatchXQuery);
        if (!lengthConstraint.isEmpty()) {
            outBuilder.append("\n").append("where").append("\n");
            outBuilder.append(lengthConstraint);
        }
        outBuilder.append("\n\n").append("return").append("\n").append(returnFormat);
        return outBuilder.toString();
    }

    protected boolean handleSpecialElements(Node child, Integer childElementIndex) {
        return false;
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
        return getDefaultString();
    }
}
