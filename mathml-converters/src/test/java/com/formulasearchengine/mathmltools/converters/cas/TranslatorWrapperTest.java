package com.formulasearchengine.mathmltools.converters.cas;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.apache.logging.log4j.ThreadContext.isEmpty;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andre Greiner-Petter
 */
@AssumeTranslatorAvailability(
        getJarPath = "/home/andre/Projects/LaCASt/bin/latex-to-cas-translator.jar"
)
public class TranslatorWrapperTest {
    private static SaveTranslatorWrapper translator;

    @BeforeAll
    public static void init(){
        translator = new SaveTranslatorWrapper("Maple");
        translator.init(
                new TranslatorConfig("/home/andre/Projects/LaCASt/bin/latex-to-cas-translator.jar")
        );
    }

    @Test
    public void simpleTranslationTest() throws IllegalAccessException {
        translator.translate("\\frac{a}{b}");
        TranslationResponse tr = translator.getTranslationResult();
        assertThat( tr.getResult(), is("(a)/(b)") );
    }

    @Test
    public void semanticMacroTranslationTest() throws IllegalAccessException {
        String translation = translator.translate("\\JacobipolyP{\\alpha}{\\beta}{n}@{a \\cos{\\theta}}");
        TranslationResponse tr = translator.getTranslationResult();
        assertEquals( translation, tr.getResult() );
        assertEquals( "JacobiP(n, alpha, beta, a*cos(theta))", translation );
    }

    @Test
    public void exceptionTranslationTest() throws IllegalAccessException {
        translator.translate("\\ctsHahn{n}@{x}{a}{b}{c}{d}{}");
        TranslationResponse tr = translator.getTranslationResult();
        assertThat( tr.getResult(), isEmpty() );
        assertThat( tr.getLog(), containsString("No translation possible") );
    }
}
