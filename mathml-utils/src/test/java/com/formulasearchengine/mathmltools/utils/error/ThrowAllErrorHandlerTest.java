package com.formulasearchengine.mathmltools.utils.error;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.formulasearchengine.mathmltools.utils.error.ThrowAllErrorHandler;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

class ThrowAllErrorHandlerTest {
    @Test
    void warning() {
        final ThrowAllErrorHandler throwAllErrorHandler = new ThrowAllErrorHandler();
        assertThrows(Exception.class, () -> {
            throwAllErrorHandler.warning(new SAXParseException("asdf", new LocatorImpl()));
        });
    }

    @Test
    void error() {
    }

    @Test
    void fatalError() {
    }

}