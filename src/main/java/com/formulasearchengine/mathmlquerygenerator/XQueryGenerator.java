package com.formulasearchengine.mathmlquerygenerator;

import com.formulasearchengine.mathmltools.xmlhelper.NonWhitespaceNodeList;
import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for every XQueryGenerator.
 * <br/>
 * Don't run the generate method in parallel, this class is not thread-safe.
 *
 * @author Vincent Stange
 */
public abstract class XQueryGenerator<T extends XQueryGenerator> {

    public static final String DEFAULT_NAMESPACE = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";";
    public static final String DEFAULT_PATHTOROOT = "db2-fn:xmlcolumn(\"math.math_mathml\")";

    /**
     * current main node of the document thats the generator is working on
     */
    private Node mainElement = null;

    private String relativeXPath = "";

    private String exactMatchXQuery = "";

    private String lengthConstraint = "";

    private List<String> headers = new ArrayList<>();

    private String pathToRoot = DEFAULT_PATHTOROOT;

    private String returnFormat = "data($m/*[1]/@alttext)";

    private List<String> footers = new ArrayList<>();

    private boolean restrictLength = true;

    /**
     *
     */
    abstract void generateConstraints();

    /**
     * @param document
     * @return
     */
    public abstract String generateQuery(Document document);

    /**
     * @param documentString
     * @return
     */
    public String generateQuery(String documentString) {
        return generateQuery(XMLHelper.string2Doc(documentString, true));
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

        output
                .append("for $m in ").append(getPathToRoot()).append(" return\n")
                .append("for $x in $m//*:").append(NonWhitespaceNodeList.getFirstChild(getMainElement()).getLocalName())
                .append("\n")
                .append(getExactMatchXQuery());

        if (!getLengthConstraint().isEmpty()) {
            output.append("\n").append("where").append("\n").append(getLengthConstraint());
        }

        // append return format
        output.append("\n\n")
                .append("return").append("\n").append(getReturnFormat());

        // append footers e.g. closing fences from headers
        output.append(String.join("\n", getFooters()));
        return output.toString();
    }


    Node getMainElement() {
        return mainElement;
    }

    String getRelativeXPath() {
        return relativeXPath;
    }

    String getExactMatchXQuery() {
        return exactMatchXQuery;
    }

    void setExactMatchXQuery(String exactMatchXQuery) {
        this.exactMatchXQuery = exactMatchXQuery;
    }

    String getLengthConstraint() {
        return lengthConstraint;
    }

    void setLengthConstraint(String lengthConstraint) {
        this.lengthConstraint = lengthConstraint;
    }

    List<String> getHeaders() {
        return headers;
    }

    T addHeader(String header) {
        this.headers.add(header);
        return (T) this;
    }

    List<String> getFooters() {
        return footers;
    }

    T addFooter(String footers) {
        this.footers.add(footers);
        return (T) this;
    }

    String getPathToRoot() {
        return pathToRoot;
    }

    public String getReturnFormat() {
        return returnFormat;
    }

    public boolean isRestrictLength() {
        return restrictLength;
    }

    /**
     * Resets the current xQuery expression and sets a new main element.
     *
     * @param mainElement main node of a new document
     */
    void setMainElement(Node mainElement) {
        this.mainElement = mainElement;
        exactMatchXQuery = "";
        relativeXPath = "";
        lengthConstraint = "";
    }

    void setRelativeXPath(String relativeXPath) {
        this.relativeXPath = relativeXPath;
    }

    public T setPathToRoot(String pathToRoot) {
        this.pathToRoot = pathToRoot;
        return (T) this;
    }

    public T setReturnFormat(String returnFormat) {
        this.returnFormat = returnFormat;
        return (T) this;
    }

    /**
     * If set to true a query like $x+y$ does not match $x+y+z$.
     *
     * @param restrictLength
     */
    public T setRestrictLength(boolean restrictLength) {
        this.restrictLength = restrictLength;
        this.lengthConstraint = "";
        this.relativeXPath = "";
        return (T) this;
    }
}