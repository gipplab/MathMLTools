package com.formulasearchengine.mathmltools.similarity.similarity.util;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

/**
 * The class contains static methods for direct access
 * to the content semantics of a MathML string or CMMLInfo document.
 *
 * @author Vincent Stange
 */
public class CMMLHelper {

    private static Logger logger = LogManager.getLogger(CMMLHelper.class);

    private CMMLHelper() {
        // not visible, utility class only
    }

    /**
     * Get the first node of the MathML-Content annotations within a MathML document.
     *
     * @param mathml full MathML document
     * @return first node of the MathML-Content annotations within a MathML document
     */
    public static Node getFirstApplyNode(String mathml) {
        try {
            // get the apply node of the ContentMathML root
            return getFirstApplyNode(new CMMLInfo(mathml));
        } catch (Exception e) {
            logger.error("failed to get apply node", e);
            return null;
        }
    }

    /**
     * Get the first node of the MathML-Content annotations within a MathML document.
     * Before, the MathML document was converted to strict CMML and subsequently also converted
     * to display only the abstract variation of the content dictionary.
     *
     * @param mathml full MathML document
     * @return first node of the MathML-Content annotations within a MathML document
     */
    public static Node getStrictCmml(String mathml) {
        try {
            // get ContentMathML to Strict ContentMathML and finally the abstract CD
            CMMLInfo cmmlInfo = new CMMLInfo(mathml).toStrictCmml();

            /*
              Don't use:
              Abstract2CD nicht benutzen! Sobald ein Knoten tatsächlich umbenannt
              wird, verliert dieser Knoten alle Kinder! Stattdessen kann auf dem
              späteren MathNode ein separater toAbstract aufruf erfolgen.
             */

            // and finally only get the first apply node of the ContentMathML
            return getFirstApplyNode(cmmlInfo);
        } catch (Exception e) {
            logger.error("failed to get apply node", e);
            return null;
        }
    }

    /**
     * Get the root apply node from the content mathml.
     * It will search for the first node of the MathML-Content annotations
     * or the semantics/apply node within a CMMLInfo document.
     *
     * @param cmmlInfo CMMLInfo document
     * @return first node of the MathML-Content annotations within a MathML document
     * @throws XPathExpressionException parser exception
     */
    public static Node getFirstApplyNode(CMMLInfo cmmlInfo) throws XPathExpressionException {
        // 1. search for a separate cmml semantic
        XPath xpath = XMLHelper.namespaceAwareXpath("m", CMMLInfo.NS_MATHML);
        Node applyRoot = getElement(cmmlInfo, "m:math/m:semantics/m:annotation-xml[@encoding='MathML-Content']/m:apply", xpath);
        if (applyRoot == null) {
            // 2. search for a main cmml semantic
            applyRoot = getElement(cmmlInfo, "*//m:semantics/m:apply", xpath);
            if (applyRoot == null) {
                // 3. try to take the apply right beneath the math elements
                applyRoot = getElement(cmmlInfo, "m:math/m:apply", xpath);
            }
        }
        return applyRoot;
    }

    /**
     * Extracts a single node for the specified XPath expression.
     *
     * @param node  the node
     * @param xExpr expression
     * @param xPath the x path
     * @return Node
     * @throws XPathExpressionException the xpath expression exception
     */
    public static Node getElement(Node node, String xExpr, XPath xPath) throws XPathExpressionException {
        return (Node) xPath.compile(xExpr).evaluate(node, XPathConstants.NODE);
    }
}
