package com.formulasearchengine.mathmltools.converters.canonicalize;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;


import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;

/**
 * @author Vincent Stange
 */
public class MathMLCanUtilTest {

    @Test
    public void canonicalize_real1() throws Exception {
        // prepare and execute
        String actualMathML = MathMLCanUtil.canonicalize(getResourceContent("mathml_real_1_test.xml"));
        String expectedMathML = getResourceContent("mathml_real_1_expected.xml");
        // validate
        assertThat(actualMathML, equalToIgnoringWhiteSpace(expectedMathML));
    }

    @Test
    public void canonicalize_artificial_1() throws Exception {
        // prepare and execute
        String actualMathML = MathMLCanUtil.canonicalize(getResourceContent("mathml_can_1_test.xml"));
        String expectedMathML = getResourceContent("mathml_can_1_expected.xml");
        // validate
        assertThat(actualMathML, equalToIgnoringWhiteSpace(expectedMathML));
    }

    /**
     * This is a check for a common misbehavior from the canonicalizer.
     * Check for UnaryOperatorRemover - this module should not be applied!
     */
    @Test
    public void canonicalize_error_1() throws Exception {
        // prepare and execute
        String actualMathML = MathMLCanUtil.canonicalize(getResourceContent("mathml_error_example_test.xml"));
        String expected = getResourceContent("mathml_error_example_expected.xml");
        // this should be equal - if not, the UnaryOperatorRemover is active and this should not be!
        assertThat(actualMathML, equalToIgnoringWhiteSpace(expected));
    }

    /**
     * This is a check for a common misbehavior from the canonicalizer.
     * Check to remove type annotation from constants.
     */
    @Test
    public void canonicalize_error_2() throws Exception {
        // prepare and execute
        String actualMathML = MathMLCanUtil.canonicalize(getResourceContent("mathml_error_example_test_2.xml"));
        String expected = getResourceContent("mathml_error_example_expected_2.xml");
        // this should be equal - if not, the ElementMinimizer should be changed
        assertThat(actualMathML, equalToIgnoringWhiteSpace(expected));
    }

    private String getResourceContent(String resourceFilename) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(resourceFilename), "UTF-8");
    }

}