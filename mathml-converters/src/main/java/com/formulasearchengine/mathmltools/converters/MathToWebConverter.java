package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.io.XmlDocumentReader;
import com.formulasearchengine.mathmltools.utils.Utility;
import mathtoweb.engine.MathToWeb;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Andre Greiner-Petter
 */
public class MathToWebConverter implements IConverter, Cloneable {

    private static final Logger LOG = LogManager.getLogger(MathToWebConverter.class.getName());

    private static final String NAME = "MathToWeb";

    private static final String CONVERSION_MODE = "conversion_utility_thread";
    private static final String OPTION_LIST = "-unicode -line -ie UTF8 -rep";

    private MathToWeb converter;
    private String[] response;

    public MathToWebConverter() {
    }

    @Override
    public void init() {
    }

    @Override
    public Document parse(String latex) throws InterruptedException {
        return innerParser(latex);
    }

    @Override
    public void parseToFile(String latex, Path outputFile) throws InterruptedException, IOException {
        String result = innerStringParser(latex);
        if (result != null) {
            Files.write(outputFile, result.getBytes());
            LOG.info("Successfully wrote " + outputFile + ".");
        }
    }

    private String innerStringParser(String latex) throws InterruptedException {
        latex = "$" + latex + "$";
        LOG.debug("MathToWeb parse: " + latex);
        converter = new MathToWeb(CONVERSION_MODE, OPTION_LIST, latex);
        converter.convertLatexToMathML();
        response = converter.getResults(CONVERSION_MODE);
        if (response == null) {
            LOG.error("Something strange happened, MathToWeb cannot parse this expression. " + latex);
            return null;
        }
        return extractStringResult();
    }

    private Document innerParser(String latex) throws InterruptedException {
        String mml = innerStringParser(latex);
        if (mml == null) {
            return null;
        }
        String unescaped = Utility.safeUnescape(mml);
        return XmlDocumentReader.getDocumentFromXMLString(unescaped);
    }

    private String extractStringResult() {
        if (!response[0].equals("success")) {
            LOG.info("Successfully parsed expression via MathToWeb.");
            debugOutput(response[1]);
            // success
            return response[2];
        } else {
            LOG.error("Cannot parse latex with MathToWeb. " + response[1]);
            return null;
        }
    }

    private void debugOutput(String log) {
        String[] logLines = log.split(System.lineSeparator());
        for (int i = 0; i < logLines.length; i++) {
            if (logLines[i].endsWith(":")) {
                LOG.trace(NAME + " - " + logLines[i] + logLines[++i]);
            } else if (!logLines[i].isEmpty()) {
                LOG.trace(NAME + " - " + logLines[i]);
            }
        }
    }
}
