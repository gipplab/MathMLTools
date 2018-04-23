package com.formulasearchengine.mathmltools.converters.mathoid;

/**
 * @author Andre Greiner-Petter
 */
public enum MathoidEndpoints {
    SVG_ENDPOINT("svg"),
    PNG_ENDPOINT("png"),
    MML_ENDPOINT("mml"),
    INFO_ENDPOINT("texvcinfo");

    private final String endpoint;

    MathoidEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint(String host) {
        return host + "/" + endpoint;
    }
}
