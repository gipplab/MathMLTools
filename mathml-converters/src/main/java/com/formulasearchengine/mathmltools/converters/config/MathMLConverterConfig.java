package com.formulasearchengine.mathmltools.converters.config;

/**
 * General settings for math related transformations.
 *
 * @author Vincent Stange
 */
public class MathMLConverterConfig {

    /**
     * Configuration of Encoplot algorithm
     */
    private LaTeXMLConfig latexml;

    /**
     * Configuration of Sherlock algorithm
     */
    private MathoidConfig mathoid;

    public LaTeXMLConfig getLatexml() {
        return latexml;
    }

    public MathMLConverterConfig setLatexml(LaTeXMLConfig latexml) {
        this.latexml = latexml;
        return this;
    }

    public MathoidConfig getMathoid() {
        return mathoid;
    }

    public MathMLConverterConfig setMathoid(MathoidConfig mathoid) {
        this.mathoid = mathoid;
        return this;
    }
}
