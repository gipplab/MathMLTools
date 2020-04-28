package com.formulasearchengine.mathmltools.converters.latexml;

import static org.apache.logging.log4j.ThreadContext.isEmpty;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.formulasearchengine.mathmltools.converters.LaTeXMLConverter;
import com.formulasearchengine.mathmltools.converters.config.LaTeXMLConfig;
import com.formulasearchengine.mathmltools.nativetools.NativeResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

/**
 * @author Vincent Stange
 */
@AssumeLaTeXMLAvailability
public class LaTeXMLConverterTest {

    public static final String HTTP_LATEXML_TEST = "https://drmf-latexml.wmflabs.org/convert";

    /**
     * This test needs a local LaTeXML installation. If you don't have
     * one, just @Ignore this test.
     */
    @Test
    public void runLatexmlc() throws Exception {
        assumeTrue(LaTeXMLConverter.isLaTeXMLPresent(), "latexmlc not present. skipping");
        // prepare the converter with a local configuration (no url set)
        LaTeXMLConverter converter = new LaTeXMLConverter();
        converter.init();

        // test local installation
        String latex = "\\sqrt{3}+\\frac{a+1}{b-2}";
        NativeResponse serviceResponse = converter.parseToNativeResponse(latex);

        // validate
        String expected = getResourceContent("latexmlc_result1_expected.txt");
        assertThat(serviceResponse.getStatusCode(), equalTo(0));
        assertThat(serviceResponse.getResult(), equalTo(expected));
    }

    @Test
    public void convertLatexmlService() throws Exception {
        // default configuration for the test in json (with DRMF stylesheet)
        LaTeXMLConfig lateXMLConfig = LaTeXMLConfig.getDefaultConfiguration().setUrl(HTTP_LATEXML_TEST);
        LaTeXMLConverter converter = new LaTeXMLConverter(lateXMLConfig);

        // test online service
        String latex = "\\frac{1}{(1-2^{1-s})}";
        NativeResponse serviceResponse = converter.parseAsService(latex);

        // validate
        String expected = getResourceContent("latexml_service_expected.xml");
        assertThat(serviceResponse.getStatusCode(), equalTo(0));
        assertThat(serviceResponse.getResult(), equalTo(expected));
    }

    @Test
    public void convertURIEncoding() throws Exception {
        // default configuration for the test in json (with DRMF stylesheet)
        LaTeXMLConfig lateXMLConfig = LaTeXMLConfig.getDefaultConfiguration().setUrl(HTTP_LATEXML_TEST);
        LaTeXMLConverter converter = new LaTeXMLConverter(lateXMLConfig);

        // test online service
        String latex = "a+2 b";
        NativeResponse serviceResponse = converter.parseAsService(latex);

        // validate
        String expected = getResourceContent("latexml_service_3_expected.txt");
        assertThat(serviceResponse.getStatusCode(), equalTo(0));
        assertThat(serviceResponse.getResult(), equalTo(expected));
    }

    @Test
    public void convertLatexmlService2() throws Exception {
        // default configuration for the test in json (with DRMF stylesheet)
        LaTeXMLConfig lateXMLConfig = LaTeXMLConfig.getDefaultConfiguration().setUrl(HTTP_LATEXML_TEST);
        LaTeXMLConverter converter = new LaTeXMLConverter(lateXMLConfig);

        // test online service
        String latex = getResourceContent("latexml_service_2_test.txt");
        NativeResponse serviceResponse = converter.parseAsService(latex);

        // validate
        String expected = getResourceContent("latexml_service_2_expected.xml");
        assertThat(serviceResponse.getStatusCode(), equalTo(0));
        assertThat(serviceResponse.getResult(), equalToIgnoringWhiteSpace(expected));
    }

    @Test
    public void testConfig() {
        // simple object check
        LaTeXMLConfig config = new LaTeXMLConfig().setUrl(HTTP_LATEXML_TEST);
        assertThat(config.getUrl(), is(HTTP_LATEXML_TEST));
        assertThat(config.getDefaultArguments(), notNullValue());
        assertThat(config.buildServiceRequest(), isEmpty());
    }

    @Test
    public void configToUrlString() throws Exception {
        // prepare
        Map<String, String> map = new LinkedHashMap<>();
        map.put("A", "1");
        map.put("B", "");
        map.put("C", "2");

        String[] arr = new String[]{"a", "b"};

        // test it
        LaTeXMLConfig config = new LaTeXMLConfig();
        config.setDefaultParams(map);
        config.setContentPreloads(arr);
        config.setDefaultPreloads(arr);

        // verify
        assertThat(config.buildServiceRequest(), equalTo("A=1&B&C=2&preload=a&preload=b"));
    }

    @Test
    public void configToUrlStringWithCnfg() throws Exception {
        LaTeXMLConfig config = LaTeXMLConfig.getDefaultConfiguration().setUrl(HTTP_LATEXML_TEST);
        assertThat(config.getUrl(), is(HTTP_LATEXML_TEST));
        String result = config.buildServiceRequest();

        // verify
        assertThat(result, equalTo("whatsin=math&whatsout=math&format=xhtml&includestyles&pmml&cmml&mathtex&quiet&nodefaultresources&linelength=90&stylesheet=DRMF.xsl&preload=LaTeX.pool&preload=article.cls&preload=amsmath.sty&preload=amsthm.sty&preload=amstext.sty&preload=amssymb.sty&preload=eucal.sty&preload=[dvipsnames]xcolor.sty&preload=url.sty&preload=hyperref.sty&preload=[ids]latexml.sty&preload=DLMFmath.sty&preload=DRMFfcns.sty&preload=wikidata.sty&preload=mleftright.sty&preload=texvc"));
    }

    private String getResourceContent(String resourceFilename) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(resourceFilename), "UTF-8");
    }

}