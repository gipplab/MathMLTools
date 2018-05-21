package com.formulasearchengine.mathmltools.exceptions;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

/**
 * @author Andre Greiner-Petter
 */
class ErrorHandlerTest {
    private static final SAXParseException WARN = new SAXParseException("Warning Exception Test", new LocatorImpl());
    private static final SAXParseException FATAL = new SAXParseException("Fatal Exception Test", new LocatorImpl());

    @Test
    void silenceErrors() {
        ParsingErrorHandler handler = new ParsingErrorHandler(Severity.SILENT);
        try {
            handler.fatalError(FATAL);
        } catch ( Exception e ){
            fail("ParsingErrorHandler on Silent-Mode should not throw an exception!");
        }
    }

    @Test
    void notifyErrors() {
        ParsingErrorHandler handler = new ParsingErrorHandler(Severity.NOTIFY);
        try {
            handler.fatalError(FATAL);
        } catch ( Exception e ){
            fail("ParsingErrorHandler on Notification-Mode should not throw an exception!");
        }
    }

    @Test
    void severeErrors() {
        ParsingErrorHandler handler = new ParsingErrorHandler(Severity.SEVERE);
        try {
            handler.warning(WARN);
        } catch ( Exception e ){
            fail("ParsingErrorHandler on Sever-Mode should not throw an exception for warnings!");
        }
        assertThrows(
                SAXParseException.class,
                () -> handler.error(FATAL),
                "ParsingErrorHandler on Sever-Mode should throw an exception for errors!"
        );
        assertThrows(
                SAXParseException.class,
                () -> handler.fatalError(FATAL),
                "ParsingErrorHandler on Sever-Mode should throw an exception for fatal errors!"
        );
    }

    @Test
    void throwAllErrors() {
        ParsingErrorHandler handler = new ParsingErrorHandler(Severity.THROW_ALL);
        assertThrows(
                SAXParseException.class,
                () -> handler.warning(WARN),
                "ParsingErrorHandler on ThrowAll-Mode should throw an exception for warnings!"
        );
        assertThrows(
                SAXParseException.class,
                () -> handler.error(FATAL),
                "ParsingErrorHandler on ThrowAll-Mode should throw an exception for errors!"
        );
        assertThrows(
                SAXParseException.class,
                () -> handler.fatalError(FATAL),
                "ParsingErrorHandler on ThrowAll-Mode should throw an exception for fatal errors!"
        );
    }
}