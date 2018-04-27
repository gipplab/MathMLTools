package com.formulasearchengine.mathmltools.similarity;

import com.formulasearchengine.mathmltools.similarity.result.Match;
import com.formulasearchengine.mathmltools.similarity.util.MathNodeException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Vincent Stange
 */
public class MathPlagTest {

    @Test
    public void complexRun_all() throws IOException, XPathExpressionException, MathNodeException {
        // prepare two mathml files, cmml is in the annotate element
        String refMathML = IOUtils.toString(this.getClass().getResourceAsStream("mathml_complex_1.xml"), "UTF-8");
        String compMathML = IOUtils.toString(this.getClass().getResourceAsStream("mathml_complex_2.xml"), "UTF-8");

        // test the old comparison services
        Map<String, Object> result = MathPlag.compareOriginalFactors(refMathML, compMathML);
        assertThat(result, notNullValue());
        assertThat(result.get("coverage"), is(1.0));
        assertThat(result.get("depth"), nullValue());
        assertThat(result.get("structureMatch"), is(true));
        assertThat(result.get("dataMatch"), is(true));
        assertThat(result.get("isEquation"), is(true));

        // test the identical mathplag services
        List<Match> identMatch = MathPlag.compareIdenticalMathML(refMathML, compMathML);
        assertThat(identMatch, notNullValue());
        assertThat(identMatch.size(), is(3));

        // test the similar mathplag services
        List<Match> simMatch = MathPlag.compareSimilarMathML(refMathML, compMathML);
        assertThat(simMatch, notNullValue());
        assertThat(simMatch.size(), is(1));
        assertThat(simMatch.get(0).getId(), is("p1.1.m1.1.4.cmml"));
        // The query should be completely found inside the comparisonMathML, depth is therefor = 0
        assertThat(simMatch.get(0).getDepth(), is(0)); // depth in ref tree
        assertThat(simMatch.get(0).getMatches().get(0).getDepth(), is(0)); // depth in comp tree
    }

    @Test
    public void simpleRun_identical_1() throws IOException, XPathExpressionException, MathNodeException {
        // prepare two mathml files, cmml is in the annotate element
        String refMathML = IOUtils.toString(this.getClass().getResourceAsStream("mathml_annotation_1.xml"), "UTF-8");
        String compMathML = IOUtils.toString(this.getClass().getResourceAsStream("mathml_annotation_2.xml"), "UTF-8");

        // test the old comparison services
        Map<String, Object> result = MathPlag.compareOriginalFactors(refMathML, compMathML);
        assertThat(result, notNullValue());
        assertThat(result.get("coverage"), is(1.0));
        assertThat(result.get("depth"), is(5));
        assertThat(result.get("structureMatch"), is(true));
        assertThat(result.get("dataMatch"), is(true));
        assertThat(result.get("isEquation"), is(false));

        // test the mathplag services
        List<Match> matches = MathPlag.compareIdenticalMathML(refMathML, compMathML);
        assertThat(matches, notNullValue());
        assertThat(matches.size(), is(1));
        assertThat(matches.get(0).getId(), is("p1.1.m1.1.4.cmml"));
        assertThat(matches.get(0).getCoverage(), is(1.0));
    }

    @Test
    public void simpleRun_similar_1() throws IOException, XPathExpressionException, MathNodeException {
        // prepare two mathml files, cmml is in the annotate element
        String refMathML = IOUtils.toString(this.getClass().getResourceAsStream("mathml_query_pure_1.xml"), "UTF-8");
        String compMathML = IOUtils.toString(this.getClass().getResourceAsStream("mathml_annotation_3.xml"), "UTF-8");
        // test the old comparison services
        Map<String, Object> result = MathPlag.compareOriginalFactors(refMathML, compMathML);
        assertThat(result, notNullValue());
        assertThat(result.get("coverage"), is(0.0));
        assertThat(result.get("depth"), nullValue());
        assertThat(result.get("structureMatch"), is(true));
        assertThat(result.get("dataMatch"), is(true));
        assertThat(result.get("isEquation"), is(true));

        // test the identical mathplag services
        List<Match> identMatch = MathPlag.compareIdenticalMathML(refMathML, compMathML);
        assertThat(identMatch, notNullValue());
        assertThat(identMatch.size(), is(0));

        // test the similar mathplag services
        List<Match> simMatch = MathPlag.compareSimilarMathML(refMathML, compMathML);
        assertThat(simMatch, notNullValue());
        assertThat(simMatch.size(), is(1));
        // the query has no id attribute for the qvar element
        assertThat(simMatch.get(0).getId(), nullValue());
        // The query should be completely found inside the comparisonMathML, depth is therefor = 1
        assertThat(simMatch.get(0).getDepth(), is(1));
        assertThat(simMatch.get(0).getMatches().get(0).getDepth(), is(1));
    }
}