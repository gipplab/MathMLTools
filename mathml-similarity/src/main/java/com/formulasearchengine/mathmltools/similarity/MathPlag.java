package com.formulasearchengine.mathmltools.similarity;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import com.formulasearchengine.mathmltools.similarity.node.MathNode;
import com.formulasearchengine.mathmltools.similarity.node.MathNodeGenerator;
import com.formulasearchengine.mathmltools.similarity.result.Match;
import com.formulasearchengine.mathmltools.similarity.result.SimilarityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides methods to compare mathematical expression in
 * MathML format. The input can either be as a string or {@link CMMLInfo}
 * object. In the first case the string will converters into the latter.
 * <br/>
 * At the moment this is purely a utility class and therefore
 * has no public constructor. All methods are static.
 *
 * @author Vincent Stange
 */
public class MathPlag {

    private static Logger logger = LogManager.getLogger(MathPlag.class);

    private MathPlag() {
        // not visible, utility class only
    }

    /**
     * Compare two MathML formulas against each other. The MathML string will be transformed into a {@link CMMLInfo}
     * document and then to a {@link MathNode} tree.
     * <br/>
     * The formulas are compared to find identical structures.
     *
     * @param refMathML  Reference MathML string (must contain pMML and cMML)
     * @param compMathML Comparison MathML string (must contain pMML and cMML)
     * @return list of matches / similarities, list can be empty.
     */
    public static List<Match> compareIdenticalMathML(String refMathML, String compMathML) throws IOException, ParserConfigurationException {
        // switch from a string > CMMLInfo document > MathNode tree
        MathNode refMathNode = MathNodeGenerator.generateMathNode(new CMMLInfo(refMathML));
        MathNode compMathNode = MathNodeGenerator.generateMathNode(new CMMLInfo(compMathML));
        return new SubTreeComparison(SimilarityType.identical).getSimilarities(refMathNode, compMathNode, true);
    }

    /**
     * Compare two MathML formulas against each other. The MathML string will be transformed into a {@link CMMLInfo}
     * document and then to a {@link MathNode} tree.
     * <br/>
     * The formulas are compared to find similar structures.
     *
     * @param refMathML  Reference MathML string (must contain pMML and cMML)
     * @param compMathML Comparison MathML string (must contain pMML and cMML)
     * @return list of matches / similarities, list can be empty.
     */
    public static List<Match> compareSimilarMathML(String refMathML, String compMathML) throws IOException, ParserConfigurationException {
        // switch from a string > CMMLInfo document > abstract MathNode tree
        MathNode refMathNode = MathNodeGenerator.generateMathNode(new CMMLInfo(refMathML)).toAbstract();
        MathNode compMathNode = MathNodeGenerator.generateMathNode(new CMMLInfo(compMathML)).toAbstract();
        return new SubTreeComparison(SimilarityType.similar).getSimilarities(refMathNode, compMathNode, true);
    }

    /**
     * Compare two MathML formulas. The return value is a map of similarity factors like matching depth,
     * element coverage, indicator for structural or data match and if the comparison formula holds an
     * equation.
     *
     * @param refMathML  Reference MathML string (must contain pMML and cMML)
     * @param compMathML Comparison MathML string (must contain pMML and cMML)
     * @return map of all found factors (depth, coverage, structureMatch, dataMatch, isEquation)
     * @throws ParserConfigurationException malformed mathml or even xml in most cases
     * @throws XPathExpressionException     could hint towards a bug
     * @throws IOException                  transformation exception between document and string
     */
    public static Map<String, Object> compareOriginalFactors(String refMathML, String compMathML) throws ParserConfigurationException, XPathExpressionException, IOException {
        try {
            CMMLInfo refDoc = new CMMLInfo(refMathML);
            CMMLInfo compDoc = new CMMLInfo(compMathML);
            // compute factors
            final Integer depth = compDoc.getDepth(refDoc.getXQuery());
            final Double coverage = compDoc.getCoverage(refDoc.getElements());
            Boolean formula = compDoc.isEquation(true);
            Boolean structMatch = compDoc.toStrictCmml().abstract2CDs()
                    .isMatch(refDoc.toStrictCmml().abstract2CDs().getXQuery());
            Boolean dataMatch = new CMMLInfo(compMathML).toStrictCmml().abstract2DTs()
                    .isMatch(new CMMLInfo(refMathML).toStrictCmml().abstract2DTs().getXQuery());

            HashMap<String, Object> result = new HashMap<>();
            result.put("depth", depth);
            result.put("coverage", coverage);
            result.put("structureMatch", structMatch);
            result.put("dataMatch", dataMatch);
            result.put("isEquation", formula);
            return result;
        } catch (Exception e) {
            // log and throw in this case
            logger.error(String.format("mathml comparison failed (refMathML: %s) (compMathML: %s)", refMathML, compMathML), e);
            throw e;
        }
    }
}
