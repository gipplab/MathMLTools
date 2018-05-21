package com.formulasearchengine.mathmltools.exceptions;

/**
 * Control the severity of the {@link ParsingErrorHandler}.
 *
 * @author Andre Greiner-Petter
 */
public enum Severity {
    /**
     * Ignores all errors and problems.
     * Note that errors and fatal errors will be printed to the log on the debug level!
     * Otherwise it could make developers crazy to find problems.
     * However, there will be no exceptions thrown from the {@link ParsingErrorHandler}.
     */
    SILENT,

    /**
     * Notifies about errors and problems via logging but do not throw exceptions.
     */
    NOTIFY,

    /**
     * Notifies about every event via logging and throw critical exceptions.
     * This option should be used by default.
     */
    SEVERE,

    /**
     * Notifies about all events and throw exceptions all the time (also for warnings!).
     */
    THROW_ALL
}
