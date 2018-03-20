package com.formulasearchengine.mathmltools.converters.latexml;

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

import com.formulasearchengine.mathmltools.nativetools.NativeResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

/**
 * @author Vincent Stange
 */
public class LaTeXMLConverterTest {

    public static final String HTTP_LATEXML_TEST = "https://drmf-latexml.wmflabs.org";

    /**
     * This test needs a local LaTeXML installation. If you don't have
     * one, just @Ignore this test.
     */
    @Test
    public void runLatexmlc() throws Exception {
        assumeTrue(LaTeXMLConverter.latexmlcPresent(), "latexmlc not present. skipping");
        // prepare the converter with a local configuration (no url set)
        LaTeXMLConverter converter = new LaTeXMLConverter(new LateXMLConfig().setActive(true).setUrl(""));

        // test local installation
        String latex = "\\sqrt{3}+\\frac{a+1}{b-2}";
        NativeResponse serviceResponse = converter.runLatexmlc(latex);

        // validate
        String expected = getResourceContent("latexmlc_result1_expected.txt");
        assertThat(serviceResponse.getStatusCode(), equalTo(0));
        assertThat(serviceResponse.getResult(), equalTo(expected));
    }

    @Test
    public void convertLatexmlService() throws Exception {
        // default configuration for the test in json (with DRMF stylesheet)
        LateXMLConfig lateXMLConfig = LateXMLConfig.getDefaultConfiguration().setUrl(HTTP_LATEXML_TEST);
        LaTeXMLConverter converter = new LaTeXMLConverter(lateXMLConfig);

        // test online service
        String latex = "\\frac{1}{(1-2^{1-s})}";
        NativeResponse serviceResponse = converter.convertLatexmlService(latex);

        // validate
        String expected = getResourceContent("latexml_service_expected.txt");
        assertThat(serviceResponse.getStatusCode(), equalTo(0));
        assertThat(serviceResponse.getResult(), equalTo(expected));
    }

    @Test
    public void convertURIEncoding() throws Exception {
        // default configuration for the test in json (with DRMF stylesheet)
        LateXMLConfig lateXMLConfig = LateXMLConfig.getDefaultConfiguration().setUrl(HTTP_LATEXML_TEST);
        LaTeXMLConverter converter = new LaTeXMLConverter(lateXMLConfig);

        // test online service
        String latex = "a+2 b";
        NativeResponse serviceResponse = converter.convertLatexmlService(latex);

        // validate
        String expected = getResourceContent("latexml_service_3_expected.txt");
        assertThat(serviceResponse.getStatusCode(), equalTo(0));
        assertThat(serviceResponse.getResult(), equalTo(expected));
    }

    @Test
    public void convertLatexmlService2() throws Exception {
        // default configuration for the test in json (with DRMF stylesheet)
        LateXMLConfig lateXMLConfig = LateXMLConfig.getDefaultConfiguration().setUrl(HTTP_LATEXML_TEST);
        LaTeXMLConverter converter = new LaTeXMLConverter(lateXMLConfig);

        // test online service
        String latex = getResourceContent("latexml_service_2_test.txt");
        NativeResponse serviceResponse = converter.convertLatexmlService(latex);

        // validate
        String expected = getResourceContent("latexml_service_2_expected.txt");
        assertThat(serviceResponse.getStatusCode(), equalTo(0));
        assertThat(serviceResponse.getResult(), equalToIgnoringWhiteSpace(expected));
    }

    @Test
    public void testConfig() {
        // simple object check
        LateXMLConfig config = new LateXMLConfig().setActive(false).setUrl(HTTP_LATEXML_TEST);
        assertThat(config.isActive(), is(false));
        assertThat(config.getUrl(), is(HTTP_LATEXML_TEST));
        assertThat(config.getParams(), notNullValue());
        assertEquals(config.getParams().size(), 0);
    }

    @Test
    public void configToUrlString() throws Exception {
        // prepare
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("A", "1");
        map.put("B", "");
        map.put("C", Arrays.asList("2", "3", "4", "5"));

        // test it
        LaTeXMLConverter laTeXMLConverter = new LaTeXMLConverter(null);
        String result = laTeXMLConverter.configToUrlString(map);

        // verify
        assertThat(result, equalTo("&A=1&B&C=2&C=3&C=4&C=5"));
    }

    /**
     * The VMEXT demo converts arrays to k-v-pairs in with integer numbers as keys.
     * For example a,b would become 0=a, 1=b
     *
     * @throws Exception
     */
    @Test
    public void configToUrlStringKeys() throws Exception {
        // prepare
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> innerMap = new LinkedHashMap<>();
        innerMap.put("0", "2");
        innerMap.put("1", "3");
        innerMap.put("2", "4");
        innerMap.put("3", "5");
        map.put("A", "1");
        map.put("B", "");
        map.put("C", innerMap);

        // test it
        LaTeXMLConverter laTeXMLConverter = new LaTeXMLConverter(null);
        String result = laTeXMLConverter.configToUrlString(map);

        // verify
        assertThat(result, equalTo("&A=1&B&C=2&C=3&C=4&C=5"));
    }

    @Test
    public void configToUrlStringWithCnfg() throws Exception {
        LateXMLConfig config = LateXMLConfig.getDefaultConfiguration().setUrl(HTTP_LATEXML_TEST);
        assertThat(config.getUrl(), is(HTTP_LATEXML_TEST));
        assertEquals(config.getParams().size(), 11);
        LaTeXMLConverter laTeXMLConverter = new LaTeXMLConverter(config);
        String result = laTeXMLConverter.configToUrlString(config.getParams());

        // verify
        assertThat(result, equalTo("&whatsin=math&whatsout=math&includestyles&format=xhtml&pmml&cmml&nodefaultresources&linelength=90&quiet&preload=LaTeX.pool&preload=article.cls&preload=amsmath.sty&preload=amsthm.sty&preload=amstext.sty&preload=amssymb.sty&preload=eucal.sty&preload=DLMFmath.sty&preload=DRMFfcns.sty&preload=[dvipsnames]xcolor.sty&preload=url.sty&preload=hyperref.sty&preload=[ids]latexml.sty&preload=texvc&stylesheet=DRMF.xsl"));
    }

    private String getResourceContent(String resourceFilename) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(resourceFilename), "UTF-8");
    }

}