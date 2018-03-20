package com.formulasearchengine.mathmltools.querygenerator;

import org.w3c.dom.Node;

/**
 * Created by Moritz Schubotz on 08.11.2014.
 */
public class NtcirPattern {
    private final String num;
    private final String formulaID;
    private final String xQueryExpression;
    private final Node mathMLNode;

    /**
     * @param num
     * @param formulaID
     * @param xQueryExpression
     * @param mathMLNode
     */
    public NtcirPattern(String num, String formulaID, String xQueryExpression, Node mathMLNode) {
        this.num = num;
        this.formulaID = formulaID;
        this.xQueryExpression = xQueryExpression;
        this.mathMLNode = mathMLNode;
    }

    public final String getNum() {
        return num;
    }

    public final String getFormulaID() {
        return formulaID;
    }

    public final String getxQueryExpression() {
        return xQueryExpression;
    }

    public final Node getMathMLNode() {
        return mathMLNode;
    }
}
