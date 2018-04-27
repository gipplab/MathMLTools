package com.formulasearchengine.mathmltools.converters.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.formulasearchengine.mathmltools.nativetools.NativeResponse;

/**
 * Object representation of the JSON response
 * from the LaTeXML service.
 *
 * @author Vincent Stange
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaTeXMLServiceResponse extends NativeResponse {

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

    public LaTeXMLServiceResponse(NativeResponse nr) {
        this.statusCode = nr.getStatusCode();
        this.status = nr.getMessage();
        this.result = nr.getResult();
        if (nr.getThrowedException() != null) {
            log += "Exception: " + nr.getThrowedException();
        }
    }

    public LaTeXMLServiceResponse(NativeResponse nr, String result, String log) {
        this(nr);
        this.result = result;
        this.log = log + this.log;
    }

    @Override
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

    @Override
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

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        return msg == null ? this.status + " - LOG: " + this.log : msg;
    }
}
