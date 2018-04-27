package com.formulasearchengine.mathmltools.converters.mathoid;

import com.formulasearchengine.mathmltools.converters.MathoidConverter;
import com.formulasearchengine.mathmltools.converters.config.MathoidConfig;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Vincent Stange
 */
@AssumeMathoidAvailability(url = "http://localhost:10044")
public class MathoidConverterTest {

    public static final String HTTP_MATHOID_TEXT = "http://localhost:10044/mml";

    @Test
    public void convertMathML() throws Exception {
        MathoidConverter converter = new MathoidConverter(createTestConfig());
        String actual = converter.convertMathML(getResourceContent("mathoid_1_test.txt"));
        String expected = getResourceContent("mathoid_1_expected.txt");
        assertThat(actual, is(expected));
    }

    @Test
    public void convertLatex() throws Exception {
        MathoidConverter converter = new MathoidConverter(createTestConfig());
        String actual = converter.convertLatex("\\sqrt{3}+\\frac{a+1}{b-2}");
        String expected = getResourceContent("mathoid_2_expected.txt");
        assertThat(actual, is(expected));
    }

    @Test
    public void testConfig() {
        // simple object check
        MathoidConfig config = createTestConfig();
        assertThat(config.isActive(), is(true));
        assertThat(config.getUrl(), is(HTTP_MATHOID_TEXT));
    }

    private MathoidConfig createTestConfig() {
        return new MathoidConfig().setActive(true).setUrl(HTTP_MATHOID_TEXT);
    }

    private String getResourceContent(String resourceFilename) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(resourceFilename), "UTF-8");
    }

}