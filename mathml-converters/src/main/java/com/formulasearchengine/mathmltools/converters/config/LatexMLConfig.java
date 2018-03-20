package com.formulasearchengine.mathmltools.converters.config;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * As long as there is no centralized version this config, we need here our
 * own config.
 * @author Andre Greiner-Petter
 */
public class LatexMLConfig {
    private LatexMLConfig() { }

    public static final String NATIVE_COMMAND = "latexmlc";

    public static final String[] GENERIC_CONFIG = new String[]{
            NATIVE_COMMAND,
            "--includestyles",
            "--format=xhtml",
            "--whatsin=math",
            "--whatsout=math",
            "--pmml",
            "--cmml",
            "--quiet",
            "--nodefaultresources",
            "--linelength=90",
            "--preload", "LaTeX.pool",
            "--preload", "article.cls",
            "--preload", "amsmath.sty",
            "--preload", "amsthm.sty",
            "--preload", "amstext.sty",
            "--preload", "amssymb.sty",
            "--preload", "eucal.sty",
            "--preload", "[dvipsnames]xcolor.sty",
            "--preload", "url.sty",
            "--preload", "hyperref.sty",
            "--preload", "[ids]latexml.sty",
            "--preload", "texvc"
    };

    public static final String[] SEMANTIC_CONFIG = new String[]{
            NATIVE_COMMAND,
            "--whatsin=math",
            "--whatsout=math",
            "--includestyles",
            "--format=xhtml",
            "--pmml",
            "--cmml",
            "--mathtex",
            "--nodefaultresources",
            "--linelength=90",
            "--quiet",
            "--stylesheet", "DRMF.xsl",
            "--preload", "LaTeX.pool",
            "--preload", "article.cls",
            "--preload", "amsmath.sty",
            "--preload", "amsthm.sty",
            "--preload", "amstext.sty",
            "--preload", "amssymb.sty",
            "--preload", "eucal.sty",
            "--preload", "mleftright.sty", // new macros uses \mleft( and \mright
            "--preload", "[dvipsnames]xcolor.sty",
            "--preload", "url.sty",
            "--preload", "hyperref.sty",
            "--preload", "DLMFmath.sty",
            "--preload", "[ids]latexml.sty",
            "--preload", "texvc",
            "--preload", "wikidata.sty"
    };

    public static ArrayList<String> asList(String[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }
}
