package com.formulasearchengine.mathmltools.converters.cas;

/**
 * @author Andre Greiner-Petter
 */
public class TranslatorConfig {
    private String jarPath;

    private String referencesPath;

    public TranslatorConfig() {
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getReferencesPath() {
        return referencesPath;
    }

    public void setReferencesPath(String referencesPath) {
        this.referencesPath = referencesPath;
    }
}
