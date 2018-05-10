package com.formulasearchengine.mathmltools.converters;


import com.formulasearchengine.mathmltools.converters.exceptions.MathConverterException;
import com.formulasearchengine.mathmltools.converters.exceptions.UnavailableConverterException;
import com.formulasearchengine.mathmltools.io.XmlDocumentWriter;
import com.formulasearchengine.mathmltools.nativetools.NativeResponse;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This interface indicates a converter from LaTeX to MathML (not necessarily content MathML).
 * Each converter is capable to convertToDoc LaTeX to MathML as
 * 1) String format with {@link #convertToString(String)},
 * 2) W3C Document format with {@link #convertToDoc(String)},
 * 3) or directly to file with {@link #convertToFile(String, Path)}.
 * <p>
 * All methods can throw an {@link UnavailableConverterException} if the sources of the converter
 * is not available.
 *
 * @author Andre Greiner-Petter
 */
public interface IConverter {
    /**
     * A converter needs to be initiated before it can be used.
     * The reason for this extra method is that most converters
     * need to pre load packages, jars or initiate the original
     * converter that will be called.
     * <p>
     * If your converter do not have anything to initiated before
     * it can be used, just leave this method blank.
     *
     * @throws Exception any kind of exception during the initiation
     */
    void init() throws Exception;

    /**
     * Converts the given latex string to MathML as Document object
     *
     * @param latex raw latex string of mathematical formula
     * @return a document object of the generated MathML
     * @throws MathConverterException        if there occur an error in translation
     * @throws UnavailableConverterException if the converter is not available because the sources are not loaded
     */
    default Document convertToDoc(String latex) throws MathConverterException, UnavailableConverterException {
        //String rawMML = convertToString(latex);
        return null; // TODO
    }

    /**
     * Converts the given latex string to MathML string.
     * Note that this method may return the raw string output from the original converter.
     * That means the MathML may be formatted (line breaks, indents, etc.) or not.
     *
     * @param latex raw latex string of mathematical formula
     * @return the generated MathML string (raw string output of the converter)
     * @throws MathConverterException        if there occur an error in translation
     * @throws UnavailableConverterException if the converter is not available because the sources are not loaded
     */
    String convertToString(String latex) throws MathConverterException, UnavailableConverterException;

    /**
     * Converts the given latex string directly to a file.
     * By default it will use the {@link #convertToDoc(String)} method
     * to allow a formatted output.
     *
     * @param latex      raw latex string of mathematical formula
     * @param outputFile the file with the formatted MathML. If the file already existed, it will be overwritten!
     * @throws IOException                   if an error in the writing process or formatting process occurs
     * @throws MathConverterException        if there occur an error in translation
     * @throws UnavailableConverterException if the converter is not available because the sources are not loaded
     */
    default void convertToFile(String latex, Path outputFile) throws IOException, MathConverterException, UnavailableConverterException {
        Document doc = convertToDoc(latex);
        XmlDocumentWriter.writeToFile(doc, outputFile);
    }

    /**
     * @param latex
     * @param outputFile
     * @param formatted
     * @throws IOException
     * @throws MathConverterException
     * @throws UnavailableConverterException
     */
    default void convertToFile(String latex, Path outputFile, boolean formatted) throws IOException, MathConverterException, UnavailableConverterException {
        if (formatted) {
            convertToFile(latex, outputFile);
            return;
        }

        if (Files.notExists(outputFile)) {
            Files.createFile(outputFile);
        }

        String raw = convertToString(latex);
        Files.write(outputFile, raw.getBytes());
    }

    default int handleResponseCode(NativeResponse response, String name, Logger logger) {
        if (response.getStatusCode() != 0) {
            logger.warn(name
                    + " finished with exit "
                    + response.getStatusCode()
                    + ": "
                    + response.getMessage());
        }
        return response.getStatusCode();
    }

    default String getNativeCommand() {
        return null;
    }
}
