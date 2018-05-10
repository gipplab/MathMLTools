package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.converters.cas.POMLoader;
import com.formulasearchengine.mathmltools.converters.cas.PomXmlWriter;
import com.formulasearchengine.mathmltools.converters.exceptions.MathConverterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

/**
 * This class is a wrapper of the MLP (POM-project) class.
 * It can convertToDoc mathematical expressions and creates
 * a tagged convertToDoc tree. Furthermore, it uses the PomXmlWriter
 * to convertToDoc it to XML trees.
 */
public class POMConverter implements IConverter {

    private static final Logger LOG = LogManager.getLogger(POMConverter.class.getName());

    private Path referenceDir;
    private POMLoader pom;

    public POMConverter() {
    }

    @Override
    public void init() {
        try {
            pom = new POMLoader();
            pom.init();
        } catch (Exception e) {
            LOG.error("Cannot instantiate POMLoader.", e);
        }
    }

    private String parseLatexMathToStringXML(String latex) throws InvocationTargetException, IllegalAccessException, XMLStreamException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        LOG.debug("Parse latex expression by POM-Tagger.");
        pom.parse(latex);

        LOG.debug("Write XML to output stream");
        PomXmlWriter.writeStraightXML(pom, outputStream);

        LOG.debug("Convert output stream to string and close output stream.");
        String out = outputStream.toString();
        outputStream.close();
        return out;
    }

    private synchronized Document parseLatexMathToDOM(String latex)
            throws
            IOException,
            ParserConfigurationException,
            InvocationTargetException,
            IllegalAccessException,
            XMLStreamException,
            ExecutionException,
            InterruptedException {
        LOG.info("Parse latex string to document...");
        // we using a trick and use the PomXmlWriter to directly
        // write the output the DocumentBuilder input stream.

        // Piped in and output streams (connect the output to the input stream)
        LOG.trace("Init linked piped in and output streams");
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);

        // create a builder to convertToDoc input stream to a document
        LOG.trace("Create document builder factory");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setExpandEntityReferences(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        // First step, convertToDoc the mathematical expression
        LOG.debug("Parse latex expression by POM-Tagger");
        pom.parse(latex);

        // Run the Document building parser in a separate thread
        // it needs work until the writing process of the PomXmlWriter is finished
        LOG.debug("Create and start parallel thread to listen on piped input stream");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Callable<Document> callable = () -> builder.parse(inputStream);

        // Run the separate thread and get the future object
        Future<Document> future = executorService.submit(callable);

        // Write to the output stream in XML format
        LOG.debug("Starting writing process to connected piped output stream...");
        PomXmlWriter.writeStraightXML(pom, outputStream);

        // flush and finally close the stream => the second thread finished here
        LOG.trace("Done writing to output stream. Close stream");
        outputStream.flush();
        outputStream.close();

        // get the document from the future object and close the input stream
        LOG.debug("Get result from parallel thread (the document object)");
        Document document = future.get();
        inputStream.close();

        // shutdown all services (if its still running)
        LOG.trace("Shutdown parallel service.");
        executorService.shutdown();

        // return final document
        return document;
    }

    @Override
    public Document convertToDoc(String latex) {
        try {
            return parseLatexMathToDOM(latex);
        } catch (Exception e) {
            throw new MathConverterException("Cannot convert " + latex, e);
        }
    }

    @Override
    public String convertToString(String latex) {
        try {
            return parseLatexMathToStringXML(latex);
        } catch (Exception e) {
            throw new MathConverterException("Cannot convert " + latex, e);
        }
    }

    @Override
    public void convertToFile(String latex, Path outputFile) throws IOException {
        if (!Files.exists(outputFile)) {
            LOG.info("Create output file: " + outputFile.toString());
            Files.createFile(outputFile);
        }
        LOG.info("Parse LaTeX via POM.");

        try {
            pom.parse(latex);
            LOG.info("Write parsed POM tree to file.");
            PomXmlWriter.writeStraightXML(pom, new FileOutputStream(outputFile.toFile()));
        } catch (InvocationTargetException | IllegalAccessException | XMLStreamException e) {
            throw new MathConverterException("Cannot convert " + latex, e);
        }
    }

}
