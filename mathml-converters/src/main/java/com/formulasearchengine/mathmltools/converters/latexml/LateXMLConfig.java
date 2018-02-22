package com.formulasearchengine.mathmltools.converters.latexml;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration for the LaTeXML service.
 *
 * @author Vincent Stange
 */
public class LateXMLConfig {

    private boolean active = true;

    private String url = "";

    private Map<String, Object> params = new LinkedHashMap<>();

    public LateXMLConfig() {
        // empty constructor
    }

    public boolean isActive() {
        return active;
    }

    public LateXMLConfig setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public LateXMLConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public LateXMLConfig setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public static LateXMLConfig getDefaultConfiguration() {
        try {
            String config = IOUtils.toString(LateXMLConfig.class.getResourceAsStream("default-service-config.json"), "UTF-8");
            return new ObjectMapper().readValue(config, LateXMLConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("could not load default configuration from resource package");
        }
    }
}
