package com.formulasearchengine.mathmltools.mml.elements;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;
import static org.junit.jupiter.api.Assertions.assertThrows;

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