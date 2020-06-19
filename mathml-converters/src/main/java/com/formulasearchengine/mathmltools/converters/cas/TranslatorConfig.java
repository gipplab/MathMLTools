package com.formulasearchengine.mathmltools.converters.cas;

/**
 * @author Andre Greiner-Petter
 */
public class TranslatorConfig {
    private String jarPath;

    public TranslatorConfig() {
    }

    public TranslatorConfig(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }
}
