package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.converters.config.ConfigLoader;
import com.formulasearchengine.mathmltools.converters.exceptions.MathConverterException;
import com.formulasearchengine.mathmltools.converters.exceptions.UnavailableConverterException;
import com.formulasearchengine.mathmltools.io.XmlDocumentReader;
import com.formulasearchengine.mathmltools.utils.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Andre Greiner-Petter
 */
public class MathToWebConverter implements IConverter, Cloneable {

    private static final Logger LOG = LogManager.getLogger(MathToWebConverter.class.getName());

    private static final String NAME = "MathToWeb";

    private static final String PACKAGE_PATH = "mathtoweb.engine.";

    private static final String CONVERSION_MODE = "conversion_utility_thread";
    private static final String OPTION_LIST = "-unicode -line -ie UTF8 -rep";
    private final Path mathToWebJar;

    private String[] response;


    private Class mathToWebClass;
    private Constructor mtwConstructor;
    private Method mtwConvertMethod;
    private Method mtwGetResultsMethod;

    public MathToWebConverter() {
        this.mathToWebJar = Paths.get(ConfigLoader.CONFIG.getProperty(ConfigLoader.MATH_TO_WEB));
    }

    private static String extractStringResult(String[] response) {
        if (!response[0].equals("success")) {
            LOG.info("Successfully parsed expression via MathToWeb.");
            debugOutput(response[1]);
            // success
            return response[2];
        } else {
            LOG.error("Cannot convertToDoc latex with MathToWeb. " + response[1]);
            return null;
        }
    }

    private static void debugOutput(String log) {
        String[] logLines = log.split(System.lineSeparator());
        for (int i = 0; i < logLines.length; i++) {
            if (logLines[i].endsWith(":")) {
                LOG.trace(NAME + " - " + logLines[i] + logLines[++i]);
            } else if (!logLines[i].isEmpty()) {
                LOG.trace(NAME + " - " + logLines[i]);
            }
        }
    }

    public static void main(String[] args) {
//        MathToWebConverter converter = new MathToWebConverter();
//        converter.init();
//        String mml = converter.convertToString("a+b");
//        System.out.println(mml);
    }

    @Override
    public void init() throws UnavailableConverterException {
        if (!Files.exists(mathToWebJar)) {
            LOG.error("Cannot find MathToWebJar at " + mathToWebJar.toString());
            throw new UnavailableConverterException(NAME);
        }

        File mathToWebJarFile = mathToWebJar.toFile();
        URL jarUrl;
        try {
            jarUrl = mathToWebJarFile.toURI().toURL();
        } catch (MalformedURLException mue) {
            throw new UnavailableConverterException(NAME, "Wrong path syntax.", mue);
        }

        URLClassLoader urlCL = new URLClassLoader(new URL[] {jarUrl}, System.class.getClassLoader());
        try {
            mathToWebClass = urlCL.loadClass(PACKAGE_PATH + NAME);
            mtwConstructor = mathToWebClass.getConstructor(String.class, String.class, String.class);
            mtwConvertMethod = mathToWebClass.getMethod("convertLatexToMathML");
            mtwGetResultsMethod = mathToWebClass.getMethod("getResults", String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new UnavailableConverterException(NAME, "Cannot load classes or objects. We expected version 4.0.0 of MathToWeb!", e);
        }
    }

    @Override
    public Document convertToDoc(String latex) throws MathConverterException {
        return innerParser(latex);
    }

    @Override
    public void convertToFile(String latex, Path outputFile) throws MathConverterException, IOException {
        String result = convertToString(latex);
        if (result != null) {
            Files.write(outputFile, result.getBytes());
            LOG.info("Successfully wrote " + outputFile + ".");
        }
    }

    @Override
    public String convertToString(String latex) throws MathConverterException {
        latex = "$" + latex + "$";
        LOG.debug("MathToWeb convertToDoc: " + latex);

        try {
            Object mtwObj = mtwConstructor.newInstance(CONVERSION_MODE, OPTION_LIST, latex);
            mtwConvertMethod.invoke(mtwObj);
            Object respObj = mtwGetResultsMethod.invoke(mtwObj, CONVERSION_MODE);

            if (respObj == null) {
                LOG.error("Translation process with MathToWeb failed for " + latex);
                return null;
            }

            String[] response = (String[]) respObj;
            return extractStringResult(response);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new UnavailableConverterException(NAME, "Cannot invoke methods of MathToWeb. We expected version 4.0.0!", e);
        } catch (InvocationTargetException e) {
            throw new MathConverterException("Cannot convert LaTeX via MathToWeb.", e.getCause());
        }
    }

    private Document innerParser(String latex) {
        String mml = convertToString(latex);
        if (mml == null) {
            return null;
        }
        String unescaped = Utility.safeUnescape(mml);
        try {
            return XmlDocumentReader.parse(unescaped);
        } catch (IOException | SAXException e) {
            throw new MathConverterException("Cannot convert MathToWeb output to document.", e);
        }
    }
}
