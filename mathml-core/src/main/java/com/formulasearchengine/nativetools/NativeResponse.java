package com.formulasearchengine.nativetools;

/**
 * A simple response wrapper class for native function calls.
 *
 * @author Andre Greiner-Petter
 */
public class NativeResponse {
    private int responseCode;
    private String result;
    private String message;
    private Throwable exception;

    protected NativeResponse(String result) {
        this.responseCode = 0;
        this.result = result;
    }

    protected NativeResponse(int responseCode, String errorMessage, Throwable exception) {
        this.responseCode = responseCode;
        this.message = errorMessage;
        this.exception = exception;
    }

    /**
     * Gets the exit code of the ran native command.
     *
     * @return exit code of native command
     */
    public int getStatusCode() {
        return responseCode;
    }

    /**
     * Gets the output from the console (System.out stream) as a string
     *
     * @return string from the output stream
     */
    public String getResult() {
        return result;
    }

    /**
     * Additional messages
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    public Throwable getThrowedException() {
        return exception;
    }
}
