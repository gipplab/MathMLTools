package com.formulasearchengine.mathmlquerygenerator;

/**
 * Created by Moritz on 08.11.2014.
 */
public class NtcirPattern {
	private final String num;
	private final String formulaID;
	private final String xQueryExpression;

	public NtcirPattern (String num, String formulaID, String xQueryExpression) {
		this.num = num;
		this.formulaID = formulaID;
		this.xQueryExpression = xQueryExpression;
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
}
