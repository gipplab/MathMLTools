package com.formulasearchengine.mathmltools.mml;

import javax.xml.parsers.ParserConfigurationException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;
import org.junit.Test;

public class TreeWriterTest {


    private CMMLInfo getArxivSample() throws IOException, ParserConfigurationException {
        final String sampleMML = CMMLInfoTest.getFileContents(CMMLInfoTest.MML_TEST_DIR + "arxivSample.mml");
        return new CMMLInfo(sampleMML);
    }

    @Test
    public void compactForm() throws Exception {
        CMMLInfo mml = getArxivSample();
        assertEquals("#document[math[semantics[mrow[mrow[mi;mi;mi];mi];[apply[relation1;apply[arith1;ci;ci;ci];ci]]]]]",
                TreeWriter.compactForm(mml.toStrictCmml().abstract2CDs()));
    }

    @Test
    public void getMMLLeaves() throws Exception {
        TreeWriter.compactForm(getArxivSample());
    }

}