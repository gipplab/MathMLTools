package com.formulasearchengine.mathmltools.converters.exceptions;

/**
 * This exception should be thrown when the needed sources of a converter
 * are not available. Further information for installing the translators
 * are available on https://github.com/ag-gipp/MathMLTools#setup-converters
 *
 * @author Andre Greiner-Petter
 */
public class UnavailableConverterException extends RuntimeException {

    private static final String MSG =
            "The requested converter '%s' is not available. "
            + "Check https://github.com/ag-gipp/MathMLTools#setup-converters for instructions.";

    public UnavailableConverterException(String converter) {
        super(String.format(MSG, converter));
    }

    public UnavailableConverterException(String converter, Throwable throwable) {
        super(String.format(MSG, converter), throwable);
    }

}
