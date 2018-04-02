package com.formulasearchengine.mathmltools.converters.error;

/**
 * Exception for warnings regarding problems in the math converter process.
 *
 * @author Vincent Stange
 */
public class MathConverterException extends Throwable {
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message detail message.
     */
    public MathConverterException(String message) {
        super(message);
    }
}
