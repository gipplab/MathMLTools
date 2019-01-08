package com.formulasearchengine.mathmltools.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Andre Greiner-Petter
 */
public class ParsingErrorHandler implements ErrorHandler {

    private static final Logger LOG = LogManager.getLogger(ParsingErrorHandler.class.getName());

    private final Severity severity;

    /**
     * Default error handler uses the {@link Severity#SEVERE} option.
     */
    public ParsingErrorHandler() {
        this.severity = Severity.SEVERE;
    }

    public ParsingErrorHandler(Severity severity) {
        this.severity = severity;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        switch (severity) {
            case SILENT:
                return; // ignore
            case NOTIFY:
            case SEVERE:
                LOG.warn(exception.getMessage());
                break;
            case THROW_ALL:
                throw exception;
            default:
        }
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        switch (severity) {
            case SILENT:
                LOG.debug(exception.getMessage() + " Error handler is on level 'silent' that ignores this error.");
                return; // ignore
            case NOTIFY:
                LOG.warn(exception.getMessage() + " Error handler is on level 'notify', no exception was thrown.");
                break;
            case SEVERE:
            case THROW_ALL:
                throw exception;
            default:
        }
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        switch (severity) {
            case SILENT:
                LOG.debug(exception.getMessage() + " Error handler is on level 'silent' that ignores this error.");
                return; // ignore
            case NOTIFY:
                LOG.fatal(exception.getMessage() + " Error handler is on level 'notify', no exception was thrown.");
                break;
            case SEVERE:
            case THROW_ALL:
                throw exception;
            default:
        }
    }
}
