package com.formulasearchengine.mathmltools.converters.latexml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object representation of the JSON response
 * from the LaTeXML service.
 *
 * @author Vincent Stange
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaTeXMLServiceResponse {

    @JsonProperty("status_code")
    private int statusCode = 0;

    @JsonProperty("status")
    private String status = "";

    private String result = "";

    private String log = "";

    public LaTeXMLServiceResponse() {
        // emtpy constructor need for spring
    }

    public LaTeXMLServiceResponse(String result, String log) {
        this.result = result;
        this.log = log;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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