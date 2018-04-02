package com.formulasearchengine.mathmltools.gold.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulasearchengine.mathmltools.gold.pojo.JsonGouldiBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubFileResponse {
    private static final Logger LOG = LogManager.getLogger(GitHubFileResponse.class.getName());

    private String type;
    private String encoding;
    private String size;
    private String name;
    private String content;

    public GitHubFileResponse() {
    }

    public static String cleanUpEncodedString(String encoded) {
        return encoded.replaceAll("\\n", "");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String encodedContent) {
        this.content = encodedContent;
    }

    public String getDecodedContent() {
        if (encoding != null && encoding.equals("base64")) {
            LOG.debug("Decode base64 encoded content.");
            String cleanedString = cleanUpEncodedString(content);
            byte[] strB = cleanedString.getBytes(StandardCharsets.UTF_8);
            return new String(Base64.getDecoder().decode(strB));
        } else if (encoding != null) {
            LOG.debug("Unknown encoding for content: " + encoding + ". Keep content encoded.");
        }
        return content;
    }

    public JsonGouldiBean getJsonBeanFromContent() throws IOException {
        String getEncodedContent = getDecodedContent();
        ObjectMapper mapper = new ObjectMapper();
        JsonGouldiBean bean = mapper.readValue(getEncodedContent, JsonGouldiBean.class);
        return bean;
    }
}
