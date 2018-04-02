package com.formulasearchengine.mathmltools.converters.mathoid;

/**
 * Configuration container for the Mathoid service.
 *
 * @author Vincent Stange
 */
public class MathoidConfig {

    private boolean active = true;

    private String url = "";

    public MathoidConfig() {
        // empty constructor
    }

    public boolean isActive() {
        return active;
    }

    public MathoidConfig setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public MathoidConfig setUrl(String url) {
        this.url = url;
        return this;
    }
}
