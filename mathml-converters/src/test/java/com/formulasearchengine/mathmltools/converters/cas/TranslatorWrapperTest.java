package com.formulasearchengine.mathmltools.converters.cas;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.apache.logging.log4j.ThreadContext.isEmpty;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Andre Greiner-Petter
 */
@AssumeTranslatorAvailability(
        getJarPath = "/home/andreg-p/Projects/latex-grammar/latex-to-cas-translator.jar",
        getReferenceDirectory = "/home/andreg-p/Projects/latex-grammar/libs/ReferenceData"
)
public class TranslatorWrapperTest {
    private static SaveTranslatorWrapper translator;

    @BeforeAll
    public static void init(){
        translator = new SaveTranslatorWrapper();
        translator.init(
                "/home/andreg-p/Projects/latex-grammar/latex-to-cas-translator.jar",
                "Maple",
                "/home/andreg-p/Projects/latex-grammar/libs/ReferenceData"
        );
    }

    @Test
    public void simpleTranslationTest() throws IllegalAccessException {
        translator.translate("\\frac{a}{b}");
        TranslationResponse tr = translator.getTranslationResult();
        assertThat( tr.getResult(), is("(a)/(b)") );
    }

    @Test
    public void exceptionTranslationTest() throws IllegalAccessException {
        translator.translate("\\ctsHahn{n}@{x}{a}{b}{c}{d}{}");
        TranslationResponse tr = translator.getTranslationResult();
        assertThat( tr.getResult(), isEmpty() );
        assertThat( tr.getLog(), containsString("Unknown DLMF/DRMF Macro") );
    }
}
