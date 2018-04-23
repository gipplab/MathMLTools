package com.formulasearchengine.mathmltools.converters.mathoid;

/**
 * @author Andre Greiner-Petter
 */
public enum MathoidTypes {
    TEX("tex"),
    INLINE_TEX("inline-tex"),
    MML("mml"),
    ASCII("ascii");

    private String value;

    MathoidTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
