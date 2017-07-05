package com.formulasearchengine.mathmlquerygenerator;

import com.formulasearchengine.mathmltools.xmlhelper.NonWhitespaceNodeList;
import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Moritz on 19.03.2017.
 */
public class BasicXQueryGenerator extends XQueryGenerator<BasicXQueryGenerator> {

    public static final String XQUERY_NAMESPACE_ELEMENT = "declare namespace functx = \"http://www.functx.com\";";

    public static final String FN_PATH_FROM_ROOT = "declare function functx:path-to-node( $nodes as node()* ) as xs:string* {\n"
            + "$nodes/string-join(ancestor-or-self::*/name(.), '/')\n"
            + " };";

    public static final String XQUERY_FOOTER = "<element><x>{$x}</x><p>{data(functx:path-to-node($x))}</p></element>";

    /**
     * Create a {@see BasicXQueryGenerator} with a default configuration.
     *
     * @return returns {@see BasicXQueryGenerator}
     */
    public static BasicXQueryGenerator getDefaultGenerator() {
        return new BasicXQueryGenerator()
                .addHeader(XQueryGenerator.DEFAULT_NAMESPACE)
                .addHeader(XQUERY_NAMESPACE_ELEMENT)
                .addHeader(FN_PATH_FROM_ROOT)
                .addHeader("<result> {")
                .setPathToRoot(".")
                .setReturnFormat(XQUERY_FOOTER)
                .addFooter("}\n</result>");
    }

    @Override
    public String generateQuery(Document document) {
        if (document == null) {
            return null;
        }
        setMainElement(XMLHelper.getMainElement(document));
        return toString();
    }

    @Override
    protected void generateConstraints() {
        String exactMatchXQuery = generateSimpleConstraints(getMainElement(), true);
        setExactMatchXQuery(exactMatchXQuery);
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

                // Only consider content mathml and ignore annotations or presentation mathml
                if (!handleSpecialElements(child, childElementIndex) && (child.getLocalName() == null
                        || !XMLHelper.ANNOTATION_XML_PATTERN.matcher(child.getLocalName()).matches())) {
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
                            setRelativeXPath(getRelativeXPath() + "/*[" + childElementIndex + "]");
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
        } //for child : nodelist

        if (!isRoot && isRestrictLength()) {
            // Initialize constraint or add as additional constraint
            setLengthConstraint(
                    (getLengthConstraint().isEmpty() ? "" : getLengthConstraint() + "\n and ")
                            + "fn:count($x" + getRelativeXPath() + "/*) = " + childElementIndex
            );
        }

        if (!getRelativeXPath().isEmpty()) {
            String tmpRelativeXPath = getRelativeXPath().substring(0, getRelativeXPath().lastIndexOf("/"));
            setRelativeXPath(tmpRelativeXPath);
        }

        return out.toString();
    }

    protected boolean handleSpecialElements(Node child, Integer childElementIndex) {
        return false;
    }

    /**
     * Generates the constraints of the XQuery and then builds the XQuery and returns it as a string
     *
     * @return XQuery as string. Returns null if no main element is set.
     */
    public String toString() {
        if (getMainElement() == null) {
            return null;
        }
        generateConstraints();
        return getDefaultString();
    }
}
