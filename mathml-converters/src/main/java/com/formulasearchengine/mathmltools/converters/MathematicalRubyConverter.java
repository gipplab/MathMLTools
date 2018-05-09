package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.converters.canonicalize.Canonicalizable;
import com.formulasearchengine.mathmltools.converters.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import java.util.LinkedList;

/**
 * @author Andre Greiner-Petter
 */
public class MathematicalRubyConverter extends NativeConverter implements Canonicalizable {

    private static final Logger LOG = LogManager.getLogger(MathematicalRubyConverter.class.getName());

    private static final String NAME = "Ruby-Mathematical";
    private static final String CMD = "ruby";

    private LinkedList<String> arguments;

    public MathematicalRubyConverter() {
        arguments = new LinkedList<>();
    }

    @Override
    public void init() {
        arguments.clear();
        arguments.add(CMD);
        String script = ConfigLoader.CONFIG.getProperty(ConfigLoader.MATHEMATICAL);
        arguments.add(script);
        internalInit(arguments, NAME);
    }

    @Override
    protected Document parseInternal(LinkedList<String> args, String latex, String name) {
        return super.parseInternal(
                args,
                "$" + latex + "$",
                name
        );
    }

    @Override
    public String getNativeCommand() {
        return CMD;
    }
}
