package com.formulasearchengine.mathmltools.gold.rest;

public class RESTPathBuilder {
    public static final String BIND = "/";
    private static final String GH_REPO = "repos";
    private static final String GH_CONTENT = "contents";

    private String url;
    private String ownerRepo;
    private String path;

    public RESTPathBuilder(String baseURL) {
        url = baseURL;
    }

    public RESTPathBuilder setGithubContent(String owner, String repoName) {
        this.ownerRepo = GH_REPO + BIND + owner + BIND + repoName + BIND;
        return this;
    }

    public RESTPathBuilder setInnerPath(String path) {
        this.path = GH_CONTENT + BIND + path + BIND;
        return this;
    }

    public String getURL() {
        String str = url;
        str += ownerRepo != null ? ownerRepo : "";
        str += path != null ? path : "";
        return str;
    }

    public String getFile(String fileName) {
        return getURL() + BIND + fileName;
    }
}
