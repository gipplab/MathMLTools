package com.formulasearchengine.mathmltools.converters.mathoid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Alternative approach for conversion from a latex formula to
 * a MathML representation via Mathoid. Mathoid currently produces only
 * an enriched presentation MathML. Which must be futher transformed to
 * be usable for a math similarity comparison.
 *
 * @author Vincent Stange
 */
public class MathoidConverter {

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

    /**
     * Request against Mathoid to receive an enriched MathML.
     * Input format can be chosen.
     *
     * @param input LaTeX formula to be converted
     * @param type  input format
     * @return Enrichted MathML String from mathoid
     */
    String convert(String input, String type) {
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
}
