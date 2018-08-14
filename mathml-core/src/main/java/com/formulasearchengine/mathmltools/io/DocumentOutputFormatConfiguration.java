package com.formulasearchengine.mathmltools.io;

/**
 * @author Andre Greiner-Petter
 */
public class DocumentOutputFormatConfiguration {

    private final String method = "xml";
    private int indent = 2;
    private boolean omitXMLDeclare = true;
    private String encoding = "UTF-8";

    private String mediaType = "application/mathml+xml";

    public int getIndention() {
        return indent;
    }

    public DocumentOutputFormatConfiguration setIndention(int indent) {
        this.indent = indent;
        return this;
    }

    public boolean omitXMLDeclaration() {
        return omitXMLDeclare;
    }

    public DocumentOutputFormatConfiguration setOmitXMLDeclaration(boolean omitXMLDeclare) {
        this.omitXMLDeclare = omitXMLDeclare;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public String getEncoding() {
        return encoding;
    }

    public DocumentOutputFormatConfiguration setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public String getMediaType() {
        return mediaType;
    }

    public DocumentOutputFormatConfiguration setMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }
}
