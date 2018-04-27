package com.formulasearchengine.mathmltools.converters;

import com.formulasearchengine.mathmltools.converters.canonicalize.Canonicalizable;
import com.formulasearchengine.mathmltools.converters.config.MathoidConfig;
import com.formulasearchengine.mathmltools.converters.mathoid.MathoidEndpoints;
import com.formulasearchengine.mathmltools.converters.mathoid.MathoidInfoResponse;
import com.formulasearchengine.mathmltools.converters.mathoid.MathoidTypes;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import java.net.URL;
import java.nio.file.Path;

/**
 * Alternative approach for conversion from a latex formula to
 * a MathML representation via Mathoid. Mathoid currently produces only
 * an enriched presentation MathML. Which must be futher transformed to
 * be usable for a math similarity comparison.
 *
 * @author Vincent Stange
 */
public class MathoidConverter implements Parser, Canonicalizable {

    private static final String INFO_ENDPOINT = "texvcinfo";
    private static Logger logger = LogManager.getLogger(MathoidConverter.class);
    private final MathoidConfig mathoidConfig;

    public MathoidConverter(MathoidConfig mathoidConfig) {
        this.mathoidConfig = mathoidConfig;
    }

    /**
     * Request against Mathoid to receive an enriched MathML.
     * Input format is LaTeX.
     *
     * @param latex LaTeX formula to be converted
     * @return Enrichted MathML String from mathoid
     */
    public String convertLatex(String latex) throws HttpClientErrorException {
        return convert(latex, "tex");
    }

    /**
     * Request against mathoid to receive an enriched MathML.
     * Input format should be pMML.
     *
     * @param pmml LaTeX formula to be converted
     * @return Enrichted MathML String from mathoid
     */
    public String convertMathML(String pmml) throws HttpClientErrorException {
        return convert(pmml, "mathml");
    }

    public HttpEntity<MultiValueMap<String, String>> buildRequest(String input, MathoidTypes type) {
        // set necessary header: request per form
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("q", input);

        if (type != null) {
            map.add("type", type.getValue());
        }

        return new HttpEntity<>(map, headers);
    }

    public MathoidInfoResponse check(String in, MathoidTypes type) throws HttpClientErrorException {
        HttpEntity<MultiValueMap<String, String>> request = buildRequest(in, type);
        String url = MathoidEndpoints.INFO_ENDPOINT.getEndpoint(mathoidConfig.getUrl());
        try {
            MathoidInfoResponse response =
                    new RestTemplate().postForObject(url, request, MathoidInfoResponse.class);
            logger.info("Successfully checked expression via Mathoid.");
            return response;
        } catch (HttpClientErrorException e) {
            logger.error(e.getResponseBodyAsString());
            throw e;
        }
    }

    public String conversion(MathoidEndpoints endpoint, String input) throws HttpClientErrorException {
        HttpEntity<MultiValueMap<String, String>> request = buildRequest(input, null);
        String url = endpoint.getEndpoint(mathoidConfig.getUrl());
        try {
            RestTemplate template = new RestTemplate();
            StringHttpMessageConverter converter = new StringHttpMessageConverter(Charsets.UTF_8);
            template.getMessageConverters().add(0, converter);
            String response = template.postForObject(url, request, String.class);
            logger.info("Successfully converted expression via Mathoid.");
            return response;
        } catch (HttpClientErrorException e) {
            logger.error(e.getResponseBodyAsString());
            throw e;
        }
    }

    /**
     * Request against Mathoid to receive an enriched MathML.
     * Input format can be chosen.
     *
     * @param input LaTeX formula to be converted
     * @param type  input format
     * @return Enrichted MathML String from mathoid
     */
    public String convert(String input, String type) {
        // set necessary header: request per form
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // pack the latex string as the parameter q (q for query ;) )
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("q", input);
        if (!type.isEmpty()) {
            map.add("type", type);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        try {
            String rep = new RestTemplate().postForObject(mathoidConfig.getUrl(), request, String.class);
            logger.info(rep);
            return rep;
        } catch (HttpClientErrorException e) {
            logger.error(e.getResponseBodyAsString());
            throw e;
        }
    }

    /**
     * Returns true if the Mathoid service is reachable, otherwise false.
     *
     * @return
     */
    public boolean isReachable() {
        try {
            URL url = new URL(mathoidConfig.getUrl() + "/mml");
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            ClientHttpRequest req = factory.createRequest(url.toURI(), HttpMethod.POST);
            req.execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void init() throws Exception {
        // load default config?
    }

    @Override
    public Document parse(String latex) throws Exception {
        // TODO
        return null;
    }

    @Override
    public void parseToFile(String latex, Path outputFile) throws Exception {
        // TODO
    }
}
