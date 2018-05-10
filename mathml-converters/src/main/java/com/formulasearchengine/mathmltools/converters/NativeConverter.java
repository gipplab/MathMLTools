package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.io.XmlDocumentReader;
import com.formulasearchengine.mathmltools.nativetools.CommandExecutor;
import com.formulasearchengine.mathmltools.nativetools.NativeResponse;
import com.formulasearchengine.mathmltools.utils.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

/**
 * An abstract class for converters that call native programs, such as LaTeXML.
 *
 * @author Andre Greiner-Petter
 */
public abstract class NativeConverter implements IConverter {

    private static final Logger LOG = LogManager.getLogger(NativeConverter.class.getName());

    private String name;
    private LinkedList<String> arguments;

    protected void internalInit(LinkedList<String> arguments, String name) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public Document convertToDoc(String latex) {
        return parseInternal(
                arguments,
                latex,
                name
        );
    }

    @Override
    public void convertToFile(String latex, Path outputFile) throws IOException {
        String str = parseInternalToString(arguments, latex, name);
        Files.write(outputFile, str.getBytes());
        LOG.debug("Writing file " + outputFile + " successful.");
    }

    @Override
    public String convertToString(String latex) {
        return parseInternalToString(arguments, latex, name);
    }

    protected String parseInternalToString(LinkedList<String> args, String latex, String name) {
        args.addLast(latex);
        LOG.debug("Create command executor for " + name + ".");
        CommandExecutor executor = new CommandExecutor(name, args);
        NativeResponse response = executor.exec(CommandExecutor.DEFAULT_TIMEOUT);
        if (handleResponseCode(response, name, LOG) != 0) {
            args.removeLast();
            return null;
        }

        args.removeLast();

        // post-processing &alpha; HTML unescape
        String res = response.getResult();
        return Utility.safeUnescape(res);
    }

    protected Document parseInternal(LinkedList<String> args, String latex, String name) {
        try {
            return XmlDocumentReader.loadAndRepair(parseInternalToString(args, latex, name), null);
        } catch (Exception e) {
            LOG.warn("Cannot convertToDoc with loadAndRepair method! " + name);
            return XmlDocumentReader.getDocumentFromXMLString(parseInternalToString(args, latex, name));
        }
    }
}
