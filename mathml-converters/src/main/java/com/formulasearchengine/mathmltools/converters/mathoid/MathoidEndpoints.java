package com.formulasearchengine.mathmltools.converters.mathoid;

/**
 * @author Andre Greiner-Petter
 */
public enum MathoidEndpoints {
    SVG_ENDPOINT("svg", "image/svg+xml"),
    PNG_ENDPOINT("png", "image/png"),
    MML_ENDPOINT("mml", "application/mathml+xml"),
    INFO_ENDPOINT("texvcinfo", "application/json");

    private final String endpoint;
    private final String responseMediaType;

    MathoidEndpoints(String endpoint, String responseMediaType) {
        this.endpoint = endpoint;
        this.responseMediaType = responseMediaType;
    }

    public static void main(String[] args) {
        System.out.println(MML_ENDPOINT.getResponseMediaType().toString());
    }

    public String getEndpoint(String host) {
        return host + "/" + endpoint;
    }

    public String getResponseMediaType() {
        return responseMediaType;
    }
}
