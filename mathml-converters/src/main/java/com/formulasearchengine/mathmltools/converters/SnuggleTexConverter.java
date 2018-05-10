package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.converters.canonicalize.Canonicalizable;
import com.formulasearchengine.mathmltools.converters.exceptions.MathConverterException;
import com.formulasearchengine.mathmltools.utils.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import uk.ac.ed.ph.snuggletex.*;
import uk.ac.ed.ph.snuggletex.upconversion.UpConvertingPostProcessor;
import uk.ac.ed.ph.snuggletex.upconversion.internal.UpConversionPackageDefinitions;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SnuggleTexConverter implements IConverter, Canonicalizable {

    private static final Logger LOG = LogManager.getLogger(SnuggleTexConverter.class.getName());
    private static final String MATH_ENV_SOURROUNDER = "$";
    // do not multi-thread this object!
    private SnuggleSession session;
    private DOMOutputOptions options;
    private DocumentBuilder builder;

    public SnuggleTexConverter() {
    }

    @Override
    public void init() {
        LOG.debug("Instantiate Snuggle Session!");
        SessionConfiguration config = new SessionConfiguration();
        config.setExpansionLimit(-1); // deactivate safeguard -> we have monster heap!
        config.setFailingFast(false); // leave me here, keep going without me!

        SnuggleEngine snuggleEngine = new SnuggleEngine();
        snuggleEngine.addPackage(UpConversionPackageDefinitions.getPackage());

        session = snuggleEngine.createSession();

        UpConvertingPostProcessor upProcessor = new UpConvertingPostProcessor();
        options = new DOMOutputOptions();
        options.setAddingMathSourceAnnotations(true);
        options.addDOMPostProcessors(upProcessor);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setExpandEntityReferences(true);
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException pe) {
            LOG.error("Cannot create document builder because of invalid configurations. "
                    + "Builder will be null!", pe);
        }
    }

    /**
     * @param latex
     * @throws IOException
     */
    @Override
    public synchronized Document convertToDoc(String latex) {
        try {
            parseCurrentSession(latex);
            LOG.debug("Done. Return snuggletexs parsed document.");
            return createDocument();
        } catch (IOException ioe) {
            throw new MathConverterException("Cannot convert latex with SnuggleTeX: " + latex, ioe);
        } finally {
            session.reset();
        }
    }

    @Override
    public synchronized void convertToFile(String latex, Path outputFile) throws IOException {
        try {
            parseCurrentSession(latex);
            String str = createString();
            if (!Files.exists(outputFile)) {
                Files.createFile(outputFile);
            }
            Files.write(outputFile, str.getBytes());
            LOG.info("Writing file " + outputFile + " successful.");
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            session.reset();
        }
    }

    @Override
    public synchronized String convertToString(String latex) {
        try {
            parseCurrentSession(latex);
            return createString();
        } catch (IOException | SnuggleLogicException ioe) {
            throw new MathConverterException("Cannot convert latex with SnuggleTeX: " + latex, ioe);
        } finally {
            session.reset();
        }
    }

    private void parseCurrentSession(String latex) throws IOException {
        LOG.info("Parse latex with snuggle: " + latex);
        SnuggleInput input = new SnuggleInput(
                MATH_ENV_SOURROUNDER + latex + MATH_ENV_SOURROUNDER
        );
        boolean success = session.parseInput(input);
        if (!success) {
            LOG.warn("Snuggle couldn't parse latex...");
            handleErrors(session.getErrors());
        }

        LOG.debug("Snuggle parsed successfully. Start document export process...");
    }

    private String createString() {
        return session.buildXMLString();
    }

    private Document createDocument() throws SnuggleLogicException {
        Document doc = builder.newDocument();

//            NodeList nodeList = session.buildDOMSubtree(options);
        NodeList nodeList = session.buildDOMSubtree();

        doc.appendChild(doc.adoptNode(nodeList.item(0).cloneNode(true)));
        return doc;
    }

    private void handleErrors(List<InputError> errors) {
        String errorMsg = "";
        for (InputError err : errors) {
            errorMsg += err.toString() + Utility.NL;
        }
        LOG.error("Error occurred while parsing latex: " + errorMsg);
    }
}
