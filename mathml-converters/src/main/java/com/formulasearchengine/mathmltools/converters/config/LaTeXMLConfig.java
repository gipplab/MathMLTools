package com.formulasearchengine.mathmltools.converters.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Configuration for the LaTeXML service.
 *
 * @author Andre Greiner-Petter
 */
public class LaTeXMLConfig {
    public static final String NATIVE_CMD = "latexmlc";

    private static final String CMD_PREFIX = "--";
    private static final String PRELOAD_CMD = CMD_PREFIX + "preload";

    @JsonProperty("url")
    private String url = "";

    @JsonProperty("params-default")
    private Map<String, String> defaultParams = new LinkedHashMap<>();

    @JsonProperty("extra-params-content")
    private Map<String, String> extraContentParams = new LinkedHashMap<>();

    @JsonProperty("preload-default")
    private String[] defaultPreloads;

    @JsonProperty("preload-content")
    private String[] contentPreloads;

    public LaTeXMLConfig() {
        // empty constructor
    }

    @JsonIgnore
    public static LaTeXMLConfig getDefaultConfiguration() {
        try {
            String config = IOUtils.toString(LaTeXMLConfig.class.getResourceAsStream("default-service-config.json"), "UTF-8");
            return new ObjectMapper().readValue(config, LaTeXMLConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("could not load default configuration from resource package");
        }
    }

    private static void addParams(Map<String, String> args, LinkedList<String> list) {
        if (args == null) {
            return;
        }
        for (String key : args.keySet()) {
            list.add(CMD_PREFIX + key);
            String arg = args.get(key);
            if (arg != null && !arg.isEmpty()) {
                list.add(arg);
            }
        }
    }

    private static void addParamsForService(Map<String, String> args, LinkedList<String> list) {
        if (args == null) {
            return;
        }
        for (String key : args.keySet()) {
            String cmd = key;
            String arg = args.get(key);
            if (arg != null && !arg.isEmpty()) {
                cmd += "=" + arg;
            }
            list.add(cmd);
        }
    }

    public String getUrl() {
        return url;
    }

    public LaTeXMLConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<String, String> getDefaultParams() {
        return defaultParams;
    }

    public LaTeXMLConfig setDefaultParams(Map<String, String> defaultParams) {
        this.defaultParams = defaultParams;
        return this;
    }

    public Map<String, String> getExtraContentParams() {
        return extraContentParams;
    }

    public LaTeXMLConfig setExtraContentParams(Map<String, String> extraContentParams) {
        this.extraContentParams = extraContentParams;
        return this;
    }

    public String[] getDefaultPreloads() {
        return defaultPreloads;
    }

    public LaTeXMLConfig setDefaultPreloads(String[] defaultPreloads) {
        this.defaultPreloads = defaultPreloads;
        return this;
    }

    public String[] getContentPreloads() {
        return contentPreloads;
    }

    public LaTeXMLConfig setContentPreloads(String[] contentPreloads) {
        this.contentPreloads = contentPreloads;
        return this;
    }

    @JsonIgnore
    private LinkedList<String> buildBasicArguments() {
        LinkedList<String> args = new LinkedList<>();
        args.add(NATIVE_CMD);
        addParams(defaultParams, args);
        return args;
    }

    @JsonIgnore
    public LinkedList<String> getDefaultArguments() {
        LinkedList<String> args = buildBasicArguments();
        if (defaultPreloads == null) {
            return args;
        }
        for (int i = 0; i < defaultPreloads.length; i++) {
            args.add(PRELOAD_CMD);
            args.add(defaultPreloads[i]);
        }
        return args;
    }

    @JsonIgnore
    public LinkedList<String> getContentArguments() {
        LinkedList<String> args = buildBasicArguments();
        addParams(extraContentParams, args);
        if (contentPreloads == null) {
            return args;
        }
        for (int i = 0; i < contentPreloads.length; i++) {
            args.add(PRELOAD_CMD);
            args.add(contentPreloads[i]);
        }
        return args;
    }

    @JsonIgnore
    public String buildServiceRequest() {
        LinkedList<String> args = new LinkedList<>();
        addParamsForService(defaultParams, args);
        addParamsForService(extraContentParams, args);
        if (contentPreloads == null) {
            return String.join("&", args);
        }
        for (int i = 0; i < contentPreloads.length; i++) {
            String cmd = "preload=" + contentPreloads[i];
            args.add(cmd);
        }
        return String.join("&", args);
    }
}
