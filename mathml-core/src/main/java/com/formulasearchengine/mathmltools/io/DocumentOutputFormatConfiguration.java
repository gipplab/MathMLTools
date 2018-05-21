package com.formulasearchengine.mathmltools.io;

/**
 * @author Andre Greiner-Petter
 */
public class DocumentOutputFormatConfiguration {

    private int indent = 2;

    private boolean omitXMLDeclare = true;

    private boolean omitDoctype = true;

    private boolean omitComments = true;

    private int maxLineWidth = Integer.MAX_VALUE;

    private String lineSeperator = System.lineSeparator();

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

    public boolean omitDoctype() {
        return omitDoctype;
    }

    public DocumentOutputFormatConfiguration setOmitDoctype(boolean omitDoctype) {
        this.omitDoctype = omitDoctype;
        return this;
    }

    public boolean omitComments() {
        return omitComments;
    }

    public DocumentOutputFormatConfiguration setOmitComments(boolean omitComments) {
        this.omitComments = omitComments;
        return this;
    }

    public int getMaxLineWidth() {
        return maxLineWidth;
    }

    public DocumentOutputFormatConfiguration setMaxLineWidth(int maxLineWidth) {
        this.maxLineWidth = maxLineWidth;
        return this;
    }

    public String getLineSeperator() {
        return lineSeperator;
    }

    public DocumentOutputFormatConfiguration setLineSeperator(String lineSeperator) {
        this.lineSeperator = lineSeperator;
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
