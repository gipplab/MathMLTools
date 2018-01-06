package com.formulasearchengine.mathmlquerygenerator;

import com.formulasearchengine.mathmltools.xmlhelper.NonWhitespaceNodeList;
import com.google.common.collect.Lists;
import org.w3c.dom.Node;

import java.util.ArrayList;
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
 * <br/>
 * Translated from http://git.wikimedia.org/blob/mediawiki%2Fextensions%2FMathSearch.git/31a80ae48d1aaa50da9103cea2e45a8dc2204b39/XQueryGenerator.php
 *
 * @author Moritz Schubotz on 9/3/14.
 */
@SuppressWarnings("WeakerAccess")
public class QVarXQueryGenerator extends BasicXQueryGenerator {

    private boolean findRootApply = false;
    private boolean addQvarMap = false;
    private String qvarConstraint = "";
    private String qvarMapVariable = "";
    private Map<String, ArrayList<String>> qvar = new LinkedHashMap<>();
    private String namespace;

    /**
     * Create a {@see QVarXQueryGenerator} with a default configuration.
     *
     * @return returns {@see QVarXQueryGenerator}
     */
    public static QVarXQueryGenerator getDefaultGenerator() {
        return (QVarXQueryGenerator) new QVarXQueryGenerator()
                .addHeader(XQueryGenerator.DEFAULT_NAMESPACE)
                .addHeader(XQUERY_NAMESPACE_ELEMENT)
                .addHeader(FN_PATH_FROM_ROOT)
                .addHeader("<result> {")
                .setPathToRoot(".")
                .setReturnFormat(XQUERY_FOOTER)
                .addFooter("}\n</result>");
    }

    protected void generateConstraints() {
        qvar = new LinkedHashMap<>();
        super.generateConstraints();
        generateQvarConstraints();
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

    /**
     * Builds the XQuery as a string. Uses the default format of looping through all apply nodes.
     *
     * @return XQuery as string
     */
    protected String getDefaultString() {
        final StringBuilder output = new StringBuilder();
        // append headers e.g. namespaces and functions
        output.append(String.join("\n", getHeaders()));

        if (!qvarMapVariable.isEmpty() && addQvarMap) {
            output.append(qvarMapVariable).append("\n");
        }

        output.append("for $m in ").append(getPathToRoot()).append(" return\n")
                .append("for $x in $m//*:").append(NonWhitespaceNodeList.getFirstChild(getMainElement()).getLocalName())
                .append("\n").append(getExactMatchXQuery());

        if (!getLengthConstraint().isEmpty() || !qvarConstraint.isEmpty()) {
            output.append("\n").append("where").append("\n");
            if (getLengthConstraint().isEmpty()) {
                output.append(qvarConstraint);
            } else {
                output.append(getLengthConstraint())
                        .append(qvarConstraint.isEmpty() ? "" : "\nand ").append(qvarConstraint);
            }
        }
        // append return format
        output.append("\n\n").append("return").append("\n").append(getReturnFormat());

        // append footers e.g. closing fences from headers
        output.append(String.join("\n", getFooters()));
        return output.toString();
    }

    public Map<String, ArrayList<String>> getQvar() {
        if (qvar.isEmpty()) {
            generateConstraints();
        }
        return qvar;
    }

    /**
     * Builds the XQuery as a string. Uses the recursive format of recursively looping through the documents.
     * This enables the $depth and the $rootApply variables.
     *
     * @return XQuery as string
     */
    private String getRecursiveString() {
        final StringBuilder output = new StringBuilder();
        // append headers e.g. namespaces and functions
        output.append(String.join("\n", getHeaders()));

        if (!qvarMapVariable.isEmpty() && addQvarMap) {
            output.append(qvarMapVariable).append("\n");
        }

        output.append("\ndeclare function local:compareApply($rootApply, $depth, $x ) {\n")
                .append("(for $child in $x/* return local:compareApply(\n")
                .append("if (empty($rootApply) and $child/name() = \"apply\") then $child else $rootApply,\n")
                .append("if (empty($rootApply) and $child/name() = \"apply\") then 0 else $depth+1, $child),\n")
                .append("if ($x/name() = \"apply\"\n")
                .append(" and $x").append(getExactMatchXQuery()).append("\n");
        if (!getLengthConstraint().isEmpty()) {
            output.append(" and ").append(getLengthConstraint()).append("\n");
        }
        if (!qvarConstraint.isEmpty()) {
            output.append(" and ").append(qvarConstraint).append("\n");
        }
        output.append(" ) then\n")
                .append(getReturnFormat()).append("\n")
                .append("else ()\n")
                .append(")};\n\n")
                .append("for $m in ").append(getPathToRoot()).append(" return\n")
                .append("local:compareApply((), 0, $m)");

        return output.toString();
    }

    protected boolean handleSpecialElements(Node child, Integer childElementIndex) {
        if (!"mws:qvar".equals(child.getNodeName())) {
            return false;
        }
        //If qvar, add to qvar map
        String qvarName = child.getTextContent();
        if (qvarName.isEmpty()) {
            qvarName = child.getAttributes().getNamedItem("name").getTextContent();
        }
        if (qvar.containsKey(qvarName)) {
            qvar.get(qvarName).add(getRelativeXPath() + "/*[" + childElementIndex + "]");
        } else {
            qvar.put(qvarName, Lists.newArrayList(getRelativeXPath() + "/*[" + childElementIndex + "]"));
        }
        return true;
    }

    public boolean isAddQvarMap() {
        return addQvarMap;
    }

    /**
     * Determines whether or not the $q variable is generated with a map of qvar names to their respective xml:id
     */
    public QVarXQueryGenerator setAddQvarMap(boolean addQvarMap) {
        this.addQvarMap = addQvarMap;
        return this;
    }

    /**
     * Resets the current xQuery expression and sets a new main element.
     *
     * @param mainElement main node of a new document
     */
    public void setMainElement(Node mainElement) {
        super.setMainElement(mainElement);
        qvar = new LinkedHashMap<>();
    }

    /**
     * Determines whether or not the $rootApply and the $depth variables are generated using recursion to find the root
     * node of the matched equation and the depth of the hit.
     */
    public QVarXQueryGenerator setFindRootApply(boolean findRootApply) {
        this.findRootApply = findRootApply;
        return this;
    }

    /**
     * Generates the constraints of the XQuery and then builds the XQuery and returns it as a string
     *
     * @return XQuery as string. Returns null if no main element set.
     */
    public String toString() {
        if (getMainElement() == null) {
            return null;
        }
        generateConstraints();
        return findRootApply ? getRecursiveString() : getDefaultString();
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
