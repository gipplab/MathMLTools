package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.converters.canonicalize.Canonicalizable;
import com.formulasearchengine.mathmltools.converters.config.LaTeXMLConfig;
import com.formulasearchengine.mathmltools.converters.services.LaTeXMLServiceResponse;
import com.formulasearchengine.mathmltools.io.XmlDocumentReader;
import com.formulasearchengine.mathmltools.nativetools.CommandExecutor;
import com.formulasearchengine.mathmltools.nativetools.NativeResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Conversion from a latex formula to a MathML representation via LaTeXML.
 */
public class LaTeXMLConverter implements IConverter, Canonicalizable {
    private static final Logger LOG = LogManager.getLogger(LaTeXMLConverter.class);

    private static final Pattern LTXML_PATTERN = Pattern.compile("^\\\\math.+");

    private static final String NAME = "LaTeXML";
    private static final String ARG_PREFIX = "literal:";
    private static final String OUT_PREFIX = "--dest=";

    private Path redirect;

    private LaTeXMLConfig config;

    private boolean semanticMode = false;

    public LaTeXMLConverter() {
    }

    public LaTeXMLConverter(LaTeXMLConfig config) {
        this.config = config;
    }

    /**
     * Test whether latexmlc is available on the current system.
     *
     * @return true if
     */
    public static boolean isLaTeXMLPresent() {
        return CommandExecutor.commandCheck(LaTeXMLConfig.NATIVE_CMD);
    }

    /**
     * LaTeXML Bug if there is "\math" command at the beginning of the
     * tex expression, it needs to be wrapped in curly brackets.
     *
     * @param rawTex
     * @return
     */
    private static String preLatexmlFixes(String rawTex) {
        Matcher matcher = LTXML_PATTERN.matcher(rawTex);
        if (matcher.find()) {
            rawTex = "{" + rawTex + "}";
        }
        return rawTex;
    }

    /**
     * Switch to semantic mode (recognizes semantic macros)
     */
    public void semanticMode() {
        this.semanticMode = true;
    }

    /**
     * Switch to normal mode without semantic macros
     */
    public void nonSemanticMode() {
        this.semanticMode = false;
    }

    /**
     * Reset semantic mode to default, equivalent to nonSemanticMode()
     */
    public void resetSemanticMode() {
        nonSemanticMode();
    }

    @Override
    public void init() {
        redirect = null;
        if (config == null) {
            config = LaTeXMLConfig.getDefaultConfiguration();
        }
    }

    /**
     * Redirects the local path to invoke LaTeXML in the directory with
     * semantic macros.
     *
     * @param path path to a local directory, where you want to invoke LaTeXML.
     */
    public void redirectLatex(Path path) {
        redirect = path;
    }

    public LinkedList<String> buildArguments(String latex) {
        LinkedList<String> args =
                semanticMode ? config.getContentArguments() : config.getDefaultArguments();
        args.add("literal:" + latex);
        return args;
    }

    public List<String> buildArguments(String latex, Path outputFile) {
        LinkedList<String> args =
                semanticMode ? config.getContentArguments() : config.getDefaultArguments();
        args.add(OUT_PREFIX + outputFile.toAbsolutePath().toString());
        args.add(ARG_PREFIX + latex);
        return args;
    }

    public NativeResponse parseToNativeResponse(List<String> arguments, String latex) {
        latex = preLatexmlFixes(latex);

        LOG.info("Start parsing process of installed latexml version. " + latex);
        CommandExecutor executor = new CommandExecutor(NAME, arguments);
        if (redirect != null) {
            executor.setWorkingDirectoryForProcess(redirect);
        }
        return executor.exec(CommandExecutor.DEFAULT_TIMEOUT, Level.TRACE);
    }

    /**
     * Converts a latex formula string into mathml and includes
     * pmml, cmml and tex semantics. If no url in the config is given,
     * the local installation is used.
     *
     * @param latex Latex Formula
     * @return MathML output in the result of LaTeXMLServiceResponse
     */
    public NativeResponse parseToNativeResponse(String latex) {
        return parseToNativeResponse(buildArguments(latex), latex);
    }

    /**
     * Parses latex to MathML string via LaTeXML (locally)
     *
     * @param arguments
     * @param latex
     * @return
     */
    public String parseToString(List<String> arguments, String latex) {
        NativeResponse response = parseToNativeResponse(arguments, latex);
        if (handleResponseCode(response, NAME, LOG) != 0) {
            return null;
        }
        LOG.info(NAME + " conversion successful.");
        return response.getResult();
    }

    public String parseToString(String latex) {
        return parseToString(buildArguments(latex), latex);
    }

    @Override
    public Document parse(String latex) {
        latex = preLatexmlFixes(latex);
        String result = parseToString(latex);
        if (result != null) {
            return XmlDocumentReader.getDocumentFromXMLString(result);
        } else {
            return null;
        }
    }

    @Override
    public void parseToFile(String latex, Path outputFile) {
        latex = preLatexmlFixes(latex);
        LOG.info("Call native latexmlc for " + latex);
        CommandExecutor executor = new CommandExecutor(NAME, buildArguments(latex, outputFile));
        if (redirect != null) {
            executor.setWorkingDirectoryForProcess(redirect);
        }
        NativeResponse response = executor.exec(CommandExecutor.DEFAULT_TIMEOUT, Level.TRACE);
        if (handleResponseCode(response, NAME, LOG) == 0) {
            LOG.info("Successfully write parsed expression to " + outputFile);
        }
    }

    @Override
    public String getNativeCommand() {
        return LaTeXMLConfig.NATIVE_CMD;
    }

    /**
     * Call a LaTeXML service.
     *
     * @param latex LaTeX formula
     * @return MathML String
     */
    public LaTeXMLServiceResponse parseAsService(String latex) {
        try {
            latex = UriComponentsBuilder.newInstance().queryParam("tex", latex).build().encode(StandardCharsets.UTF_8.toString()).getQuery();
        } catch (UnsupportedEncodingException ignore) {
            LOG.warn("encoding not supported", ignore);
        }

        String serviceArguments = config.buildServiceRequest();
        String payload = serviceArguments + "&" + latex;
        RestTemplate restTemplate = new RestTemplate();

        try {
            LaTeXMLServiceResponse rep = restTemplate.postForObject(config.getUrl(), payload, LaTeXMLServiceResponse.class);
            LOG.debug(String.format("LaTeXMLServiceResponse:\n"
                            + "statusCode: %s\nstatus: %s\nlog: %s\nresult: %s",
                    rep.getStatusCode(), rep.getStatus(), rep.getLog(), rep.getResult()));
            return rep;
        } catch (HttpClientErrorException e) {
            LOG.error(e.getResponseBodyAsString());
            throw e;
        }
    }
}