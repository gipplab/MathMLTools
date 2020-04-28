package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.converters.canonicalize.Canonicalizable;
import com.formulasearchengine.mathmltools.converters.config.LaTeXMLConfig;
import com.formulasearchengine.mathmltools.converters.exceptions.MathConverterException;
import com.formulasearchengine.mathmltools.converters.services.LaTeXMLServiceResponse;
import com.formulasearchengine.mathmltools.io.XmlDocumentReader;
import com.formulasearchengine.mathmltools.nativetools.CommandExecutor;
import com.formulasearchengine.mathmltools.nativetools.NativeResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

    public NativeResponse parseToNativeResponse(List<String> arguments) {
        LOG.info("Start parsing process of installed latexml version. " + arguments);
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
    public String convertToString(List<String> arguments, String latex) {
        NativeResponse response = parseToNativeResponse(arguments, latex);
        if (handleResponseCode(response, NAME, LOG) != 0) {
            return null;
        }
        LOG.info(NAME + " conversion successful.");
        return response.getResult();
    }

    @Override
    public String convertToString(String latex) {
        return convertToString(buildArguments(latex), latex);
    }

    @Override
    public Document convertToDoc(String latex) {
        latex = preLatexmlFixes(latex);
        String result = convertToString(latex);
        if (result != null) {
            try {
                return XmlDocumentReader.parse(result);
            } catch (IOException | SAXException e) {
                throw new MathConverterException("Cannot convert LaTeXML output to document.", e);
            }
        } else {
            return null;
        }
    }

    @Override
    public void convertToFile(String latex, Path outputFile) {
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
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = config.buildServiceRequestParameters(true);
        parameters.add("tex", latex);
//        System.out.println(parameters);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, header);
        RestTemplate restTemplate = new RestTemplate();

//
//        try {
//            latex = UriComponentsBuilder.newInstance().queryParam("tex", latex).build().encode(StandardCharsets.UTF_8.toString()).getQuery();
//        } catch (UnsupportedEncodingException ignore) {
//            LOG.warn("encoding not supported", ignore);
//        }
//
//        String serviceArguments = config.buildServiceRequest();
//        String payload = serviceArguments + "&" + latex;
//        RestTemplate restTemplate = new RestTemplate();

        try {
//            LaTeXMLServiceResponse rep = restTemplate.getForObject(config.getUrl(), LaTeXMLServiceResponse.class, config.getDefaultParams());
            LaTeXMLServiceResponse rep = restTemplate.postForObject(config.getUrl(), request, LaTeXMLServiceResponse.class);
            LOG.debug(String.format("LaTeXMLServiceResponse:\n"
                            + "statusCode: %s\nstatus: %s\nlog: %s\nresult: %s",
                    rep.getStatusCode(), rep.getStatus(), rep.getLog(), rep.getResult()));
            return rep;
        } catch (HttpClientErrorException e) {
            LOG.error(e.getResponseBodyAsString());
            throw e;
        }
    }

    public static void main(String[] args) {
        LaTeXMLConfig lateXMLConfig = LaTeXMLConfig.getDefaultConfiguration().setUrl("https://drmf-latexml.wmflabs.org/convert");
        lateXMLConfig.getDefaultParams().remove("pmml");
        lateXMLConfig.getDefaultParams().remove("cmml");
        LaTeXMLConverter c = new LaTeXMLConverter(lateXMLConfig);
//        c.init();
        c.semanticMode();
        c.redirectLatex(Paths.get("/home/andre/data/DRMF"));

//        Map<String, String> params = c.config.getDefaultParams();
//        params.remove("cmml");
//        params.remove("format");
//        params.remove("linelength");

        Path dataset = Paths.get("/home/andre/data/Howard/together.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataset.getParent().resolve("together-lines.tex").toFile()))) {
            Pattern linePattern = Pattern.compile("^(.*?)[,.;]? \\\\url.*");
            Pattern resultPattern = Pattern.compile("<span.*?>(.*?)</span>", Pattern.DOTALL);

            int[] ln = new int[]{0};
            Stream<String> lines = Files.lines(dataset).sequential();
            lines
                    .peek(l -> ln[0]++)
                    .forEach(l -> {
                        Matcher m = linePattern.matcher(l);
                        if (m.matches()) {
                            String tex = m.group(1);
                            LOG.info("Convert " + ln[0] + ": " + tex);
                            try {
                                if (tex.isEmpty() || tex.matches("\\s+")) {
                                    tex = "ERROR";
                                }
                                writer.write("$" + tex + "$\n");
                            } catch (IOException e) {
                                LOG.error("CANNOT WRITE! STOP HERE");
                            }

//                            try {
////                                NativeResponse res = c.parseAsService(tex);
//                                NativeResponse res = c.parseToNativeResponse(tex);
//                                Matcher resM = resultPattern.matcher(res.getResult());
//                                if ( resM.find() ) {
//                                    String resStr = resM.group(1);
//                                    resStr = resStr.replaceAll("%\n", "");
//                                    resStr = resStr.replaceAll("\\\\\n", "\\\\ ");
//                                    LOG.info("Successfully converted " + ln[0]);
//                                    writer.write(ln[0] + ": " + resStr + "\n");
//                                } else {
//                                    LOG.warn("Unable to identify result " + ln[0]);
//                                    System.out.println(res.getResult());
//                                }
//                            } catch ( Exception | Error e ) {
//                                LOG.warn("Error in line " + ln[0]);
//                            }
                        } else {
                            LOG.warn("Cannot determine end of line in " + ln[0]);
                            try {
                                writer.write("$ERROR$\n");
                            } catch (IOException e) {
                                LOG.error("CANNOT WRITE! STOP HERE");
                            }
                        }
                    });
        } catch (IOException e) {
            LOG.error("Cannot read/write", e);
        }

//        LinkedList<String> myArgs = new LinkedList<>();
//        myArgs.addLast("latexmlc");
//        myArgs.addLast("--whatsin=math");
//        myArgs.addLast("--includes");
//        myArgs.addLast("--mathtex");
//        myArgs.addLast("--preload=amsmath.sty");
//        myArgs.addLast("--preload=DLMFmath.sty");
//        myArgs.addLast("--preload=DRMFfcns.sty");
//        myArgs.addLast("test.tex");

//        String tex = "\\JacobipolyP{a}{b}{c}@{x}";
//        String tex = "\\binom{n}{k}=\\frac{n!}{(n-k)!k!}=\\binom{n}{n-k}";
//        String tex = "\\phase@@{\\conj{z}}=-\\phase@@{z}";
//        NativeResponse res = c.parseToNativeResponse(tex);


//        NativeResponse res = c.parseToNativeResponse(myArgs);
//        System.out.println(res.getResult());


//        Pattern resultPattern = Pattern.compile("<span.*?>(.*?)</span>", Pattern.DOTALL);
//        Matcher m = resultPattern.matcher(res.getResult());
//        if ( m.find() ){
//            String r = m.group(1).replaceAll("\\\\\n", "\\\\ ");
//            System.out.println(r);
//        }

//        NativeResponse res = c.parseAsService(tex);
//        System.out.println(res.getResult());
//        System.out.println(res.getMessage());
//        System.out.println(res.getStatusCode());
    }
}