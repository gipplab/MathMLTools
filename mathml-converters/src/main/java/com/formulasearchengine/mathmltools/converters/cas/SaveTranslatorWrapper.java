package com.formulasearchengine.mathmltools.converters.cas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
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

    private static final Logger LOG = LogManager.getLogger(SaveTranslatorWrapper.class.getName());

    private static final String PACKAGE_COMMON = "gov.nist.drmf.interpreter.common.";
    private static final String PACKAGE_TRANSLATOR = "gov.nist.drmf.interpreter.cas.translation.";

    private Object translatorObj;

    private Method translateMethod;
    private Method getInfoMethod;
    private Method getTranslatedExpressionMethod;

    public SaveTranslatorWrapper() {
    }

    public void init(String forwardJARPath, String cas, String mlpReferenceDir) throws RuntimeException {
        Path jar = Paths.get(forwardJARPath);
        Path referenceDirPath = Paths.get(mlpReferenceDir);

        LOG.debug("Check existence of given JAR.");
        if (!Files.exists(jar) || !Files.exists(referenceDirPath)) {
            throw new IllegalArgumentException("Given jar for the forward translation cannot be found!");
        }

        try {
            Files.createSymbolicLink(Paths.get("libs"), referenceDirPath.getParent());
        } catch (IOException e) {
            //e.printStackTrace();
        }

        try {
            File jarF = jar.toFile();
            URLClassLoader urlCL = new URLClassLoader(new URL[] {jarF.toURI().toURL()}, System.class.getClassLoader());

            Class globalConstantsClass = urlCL.loadClass(PACKAGE_COMMON + "GlobalConstants");
            Class translator = urlCL.loadClass(PACKAGE_TRANSLATOR + "SemanticLatexTranslator");
            Class globalPathsClass = urlCL.loadClass(PACKAGE_COMMON + "GlobalPaths");
            LOG.debug("Successfully loaded classes at runtime. Start to load objects at runtime.");

            // setting global cas
            Field casKeyField = globalConstantsClass.getDeclaredField("CAS_KEY"); // public static string
            casKeyField.set(null, cas); // set global variable

            Field f = globalPathsClass.getDeclaredField("PATH_LEXICONS");
            Object obj = f.get(null);
            System.out.println(((Path) obj).toAbsolutePath().toString());

            translatorObj = translator.getDeclaredConstructor(String.class, String.class).newInstance("LaTeX", cas);

            Method initMethod = translator.getMethod("init", Path.class);
            translateMethod = translator.getMethod("translate", String.class);
            getInfoMethod = translator.getMethod("getInfoLog");
            getTranslatedExpressionMethod = translator.getMethod("getTranslatedExpression");

            LOG.debug("Successfully loaded all objects. Start to init translator.");
            initMethod.invoke(translatorObj, referenceDirPath);
        } catch (MalformedURLException mue) {
            throw new IllegalArgumentException("The given jar was corrupted. Cannot build URL.", mue);
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException cfe) {
            throw new IllegalArgumentException("The given jar was corrupted (maybe out of date). Cannot load classes.", cfe);
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("The given jar was corrupted. Cannot set values of public static variable.", iae);
        } catch (InvocationTargetException | InstantiationException ie) {
            throw new RuntimeException("Cannot run translator.", ie);
        }
    }

    public void translate(String latexString) throws InvocationTargetException, IllegalAccessException {
        translateMethod.invoke(translatorObj, latexString);
    }

    public TranslationResponse getTranslationResult() throws InvocationTargetException, IllegalAccessException {
        Object result = getTranslatedExpressionMethod.invoke(translatorObj);
        Object log = getInfoMethod.invoke(translatorObj);
        TranslationResponse res = new TranslationResponse();
        res.setResult((String) result);
        res.setLog(log.toString());
        return res;
    }
}
