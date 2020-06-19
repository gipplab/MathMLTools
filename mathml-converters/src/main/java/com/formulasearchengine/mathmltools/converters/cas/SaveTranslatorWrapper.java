package com.formulasearchengine.mathmltools.converters.cas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This wrapper is designed to load the jar of the translator service
 * at runtime and calls the forward translation service. This workaround
 * is necessary since neither the MLP.jar nor the DLMF macros are publicly
 * available yet.
 * <p>
 * TODO update once the MLP and the DLMF macros are available
 *
 * @author Andre Greiner-Petter
 */
public class SaveTranslatorWrapper {
    private static final Logger LOG = LogManager.getLogger(SaveTranslatorWrapper.class);

    private static final String PACKAGE_COMMON = "gov.nist.drmf.interpreter.common.";
    private static final String PACKAGE_TRANSLATOR = "gov.nist.drmf.interpreter.cas.translation.";

    private Object translatorObj;
    private Object exceptionObj;
    private Object resultObj;

    private Method translateMethod;
    private Method translateLabelMethod;
    private Method getInfoMethod;

    private Method getExceptionMessageMethod;
    private Method getExceptionReasonMethod;

    private final String cas;

    /**
     * The new translator requires to provide the CAS to translate to.
     * You can handle two instances in parallel for different CAS if you want.
     *
     * @param cas the CAS (e.g., Maple or Mathematica)
     */
    public SaveTranslatorWrapper(String cas) {
        this.cas = cas;
    }

    /**
     * Loads and initiates LaCASt from the provided path to the jar. If you wondering which jar
     * path to use, you need the 'latex-to-cas-translator.jar' from LaCASt.
     * <p>
     * Since it is a non-public project, we load the class on the fly and call necessary entry
     * points via reflection. If the JVM doesn't allow it, the initiation will fail. If you
     * do not have LaCASt (or a wrong version) the program is also unable to initiate a connection.
     * <p>
     * In the future, we may allow remote access. Thus you do not need to communicate directly to
     * the jar but to the host that provides a LaCASt endpoint.
     *
     * @param forwardJARPath path to the latex-to-cas-translator.jar from the LaCASt project
     * @throws RuntimeException a generic runtime exception will be thrown if the instantiation fails.
     *                          The message in the exception will provide more details about what went wrong.
     */
    public void init(String forwardJARPath) throws RuntimeException {
        Path jar = Paths.get(forwardJARPath);

        try {
            File jarF = jar.toFile();
            URLClassLoader urlCL = new URLClassLoader(new URL[]{jarF.toURI().toURL()}, ClassLoader.getSystemClassLoader());

            // load necessary classes
            // the exception class (thrown if any error during the translation process occurred)
            Class<?> translationExceptionClass = urlCL.loadClass(PACKAGE_COMMON + "exceptions.TranslationException");
            // and the main translator class
            Class<?> translator = urlCL.loadClass(PACKAGE_TRANSLATOR + "SemanticLatexTranslator");
            LOG.debug("Successfully loaded classes at runtime. Start to connect to necessary methods via reflection.");

            // translation and meta information methods
            translateMethod = translator.getMethod("translate", String.class);
            translateLabelMethod = translator.getMethod("translate", String.class, String.class);
            getInfoMethod = translator.getMethod("getInfoLogger");

            getExceptionMessageMethod = translationExceptionClass.getMethod("getMessage");
            getExceptionReasonMethod = translationExceptionClass.getMethod("getReason");

            LOG.debug("Successfully established all connections. Initiating LaCASt...");
            translatorObj = translator.getConstructor(String.class).newInstance(cas);
            LOG.info("Established connection with the LaCASt engine.");
        } catch (MalformedURLException mue) {
            throw new IllegalArgumentException("The given jar was corrupted. Cannot build URL.", mue);
        } catch (ClassNotFoundException | NoSuchMethodException cfe) {
            throw new IllegalArgumentException("The given jar was corrupted (maybe out of date). Cannot load classes.", cfe);
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("The given jar was corrupted. Cannot create instances of the translator object.", iae);
        } catch (InvocationTargetException | InstantiationException ie) {
            throw new RuntimeException("Unable to initiate the translator.", ie);
        }
    }

    private void reset() {
        exceptionObj = null;
        resultObj = null;
    }

    public String translate(String latexString) throws IllegalAccessException {
        reset();
        try {
            resultObj = translateMethod.invoke(translatorObj, latexString);
            return resultObj.toString();
        } catch (InvocationTargetException ie) {
            exceptionObj = ie.getCause();
            LOG.error("Unable to translate " + latexString, ie.getCause());
            return null;
        }
    }

    public String translate(String latexString, String label) throws IllegalAccessException {
        reset();
        try {
            resultObj = translateLabelMethod.invoke(translatorObj, latexString, label);
            return resultObj.toString();
        } catch (InvocationTargetException ie) {
            exceptionObj = ie.getCause();
            LOG.error("Unable to translate " + latexString, ie.getCause());
            return null;
        }
    }

    public TranslationResponse getTranslationResult() throws IllegalAccessException {
        TranslationResponse res = new TranslationResponse();
        try {
            String result;
            Object log;
            if (exceptionObj != null) {
                result = "";
                String msg = (String) getExceptionMessageMethod.invoke(exceptionObj);
                msg += " - Reason: " + getExceptionReasonMethod.invoke(exceptionObj).toString();
                log = msg;
            } else {
                result = resultObj.toString();
                log = getInfoMethod.invoke(translatorObj);
            }
            res.setResult(result);
            res.setLog(log.toString());
            return res;
        } catch (InvocationTargetException ie) {
            res.setResult("");
            res.setLog("Error during examination results - " + ie.getCause());
            return res;
        }
    }
}
