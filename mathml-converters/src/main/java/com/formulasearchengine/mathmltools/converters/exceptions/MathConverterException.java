package com.formulasearchengine.mathmltools.converters.exceptions;

/**
 * Exception for warnings regarding problems in the math converter process.
 *
 * @author Vincent Stange
 */
public class MathConverterException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message detail message.
     */
    public MathConverterException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message with
     * a throwable object that causes this exception.
     *
     * @param message detail message
     * @param throwable the cause of this exception
     */
    public MathConverterException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
