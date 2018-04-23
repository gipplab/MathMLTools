package com.formulasearchengine.mathmltools.converters.mathoid;

import com.google.common.net.MediaType;

/**
 * @author Andre Greiner-Petter
 */
public enum MathoidEndpoints {
    SVG_ENDPOINT("svg", MediaType.SVG_UTF_8),
    PNG_ENDPOINT("png", MediaType.PNG),
    MML_ENDPOINT("mml", MediaType.create("application", "mathml+xml")),
    INFO_ENDPOINT("texvcinfo", MediaType.JSON_UTF_8);

    private final String endpoint;
    private final MediaType responseMediaType;

    MathoidEndpoints(String endpoint, MediaType responseMediaType) {
        this.endpoint = endpoint;
        this.responseMediaType = responseMediaType;
    }

    public static void main(String[] args) {
        System.out.println(MML_ENDPOINT.getResponseMediaType().toString());
    }

    public String getEndpoint(String host) {
        return host + "/" + endpoint;
    }

    public MediaType getResponseMediaType() {
        return responseMediaType;
    }
}
