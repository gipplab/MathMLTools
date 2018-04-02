package com.formulasearchengine.mathmltools.converters.cas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Andre Greiner-Petter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslationResponse {
    @JsonProperty("result")
    private String result;

    @JsonProperty("log")
    private String log;

    public TranslationResponse() {
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
