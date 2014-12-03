package com.formulasearchengine.mathmlquerygenerator;

import org.w3c.dom.Node;

/**
 * Created by Moritz Schubotz on 08.11.2014.
 */
public class NtcirPattern {
	private final String num;
	private final String formulaID;
	private final String xQueryExpression;
	private final Node mathMLNode;

	public NtcirPattern (String num, String formulaID, String xQueryExpression, Node mathMLNode) {
		this.num = num;
		this.formulaID = formulaID;
		this.xQueryExpression = xQueryExpression;
		this.mathMLNode = mathMLNode;
	}

	public String getNum () {
		return num;
	}

	public String getFormulaID () {
		return formulaID;
	}

	public String getxQueryExpression () {
		return xQueryExpression;
	}

	public Node getMathMLNode () { return mathMLNode; }
}
