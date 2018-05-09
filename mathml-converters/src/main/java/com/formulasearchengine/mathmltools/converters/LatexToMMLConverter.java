package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.converters.canonicalize.Canonicalizable;
import com.formulasearchengine.mathmltools.converters.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

/**
 * @author Andre Greiner-Petter
 */
public class LatexToMMLConverter extends NativeConverter implements Canonicalizable {

    private static final Logger LOG = LogManager.getLogger(LatexToMMLConverter.class.getName());

    private static final String NAME = "Latex2MathML";
    private static final String CMD = "python";

    private LinkedList<String> arguments;

    public LatexToMMLConverter() {
        arguments = new LinkedList<>();
    }

    @Override
    public void init() {
        arguments.clear();
        arguments.add(CMD);
        String script = ConfigLoader.CONFIG.getProperty(ConfigLoader.LATEX2MML);
        arguments.add(script);
        internalInit(arguments, NAME);
    }

    @Override
    public String getNativeCommand() {
        return CMD;
    }
}
