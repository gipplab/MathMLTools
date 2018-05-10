package com.formulasearchengine.mathmltools.converters;

import java.nio.file.Path;

public enum Converters {
    POM(0, "pom", ".xml", new POMConverter()),
    SnuggleTeX(1, "snuggletex", ".mml", new SnuggleTexConverter()),
    LatexML(2, "latexml", ".mml", new LaTeXMLConverter()),
    Mathematical(3, "mathematical", ".mml", new MathematicalRubyConverter()),
    MathToWeb(4, "mathtoweb", ".mml", new MathToWebConverter()),
    Latex2MML(5, "latex2mathml", ".mml", new LatexToMMLConverter()),
    TeXZilla(6, "texzilla", ".mml", null),
    Mathoid(7, "mathoid", ".mml", null),
    Mathematica(8, "mathematica", ".mml", null);

    // just the position of this element in this enum (it's easier that way...)
    private final int position;

    // name of the converter (used for the sub directories)
    private final String name;

    // the file extension (usually only mml or xml)
    private final String fileEnding;

    // the converter class
    private final IConverter converter;
    // is the generated file XML or MML?
    private final boolean xmlMode;
    // the sub path to the directory, should be initialized first to set a base dir
    private Path subPath;
    // skip this in the generation process
    private boolean skip = false;

    Converters(int pos, String name, String fileEnding, IConverter parser) {
        this.position = pos;
        this.name = name;
        this.fileEnding = fileEnding;
        this.converter = parser;
        this.xmlMode = fileEnding.contains("xml");
        this.skip = parser == null;
    }

    public Path initSubPath(Path baseDir) {
        subPath = baseDir.resolve(name);
        return subPath;
    }

    public int getPosition() {
        return position;
    }

    public Path getFile(int index) {
        return subPath.resolve(index + fileEnding);
    }

    public String fileEnding() {
        return fileEnding;
    }

    public IConverter getConverter() {
        return converter;
    }

    public Path getSubPath() {
        return subPath;
    }

    public boolean isMML() {
        return !xmlMode;
    }

    public boolean skip() {
        return this.skip;
    }

    public void setSkipMode(boolean skip) {
        if (converter != null) {
            this.skip = skip;
        }
    }
}
