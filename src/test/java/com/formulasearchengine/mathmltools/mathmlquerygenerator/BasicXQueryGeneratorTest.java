package com.formulasearchengine.mathmltools.mathmlquerygenerator;

import com.formulasearchengine.mathmlquerygenerator.BasicXQueryGenerator;
import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class BasicXQueryGeneratorTest {

    @Test
    public void testNoMath() throws Exception {
        BasicXQueryGenerator qg = new BasicXQueryGenerator();
        assertNull("Input without math should return null", qg.toString());
        assertNull("Input without math document should return null", qg.generateQuery((Document) null));
    }

    @Test
    public void generateTest_withoutConfiguration() throws Exception {
        String documentStr = IOUtils.toString(this.getClass().getResourceAsStream("mathml_document_1.xml"), "UTF-8");
        CMMLInfo cmmlInfo = new CMMLInfo(documentStr);

        String expectedQuery = "for $m in db2-fn:xmlcolumn(\"math.math_mathml\") return\n" +
                "for $x in $m//*:apply\n" +
                "[*[1]/name() = 'eq' and *[2]/name() = 'apply' and *[2][*[1]/name() = 'times' and *[2]/name() = 'ci' and *[2][./text() = 'F'] and *[3]/name() = 'ci' and *[3][./text() = 'R']] and *[3]/name() = 'apply' and *[3][*[1]/name() = 'times' and *[2]/name() = 'ci' and *[2][./text() = '?'] and *[3]/name() = 'ci' and *[3][./text() = 'S']]]\n" +
                "where\n" +
                "fn:count($x/*[2]/*[2]/*) = 0\n" +
                " and fn:count($x/*[2]/*[3]/*) = 0\n" +
                " and fn:count($x/*[2]/*) = 3\n" +
                " and fn:count($x/*[3]/*[2]/*) = 0\n" +
                " and fn:count($x/*[3]/*[3]/*) = 0\n" +
                " and fn:count($x/*[3]/*) = 3\n" +
                " and fn:count($x/*) = 3\n" +
                "\n" +
                "return\n" +
                "data($m/*[1]/@alttext)";

        BasicXQueryGenerator generator = new BasicXQueryGenerator();
        assertThat(generator.generateQuery(cmmlInfo), is(expectedQuery));
    }

    @Test
    public void generateTest_withConfiguration() throws Exception {
        String documentStr = IOUtils.toString(this.getClass().getResourceAsStream("mathml_document_1.xml"), "UTF-8");
        CMMLInfo cmmlInfo = new CMMLInfo(documentStr);

        String expectedQuery = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";\n" +
                "declare namespace functx = \"http://www.functx.com\";\n" +
                "declare function functx:path-to-node( $nodes as node()* ) as xs:string* {\n" +
                "$nodes/string-join(ancestor-or-self::*/name(.), '/')\n" +
                " };\n" +
                "<result> {for $m in . return\n" +
                "for $x in $m//*:apply\n" +
                "[*[1]/name() = 'eq' and *[2]/name() = 'apply' and *[2][*[1]/name() = 'times' and *[2]/name() = 'ci' and *[2][./text() = 'F'] and *[3]/name() = 'ci' and *[3][./text() = 'R']] and *[3]/name() = 'apply' and *[3][*[1]/name() = 'times' and *[2]/name() = 'ci' and *[2][./text() = '?'] and *[3]/name() = 'ci' and *[3][./text() = 'S']]]\n" +
                "where\n" +
                "fn:count($x/*[2]/*[2]/*) = 0\n" +
                " and fn:count($x/*[2]/*[3]/*) = 0\n" +
                " and fn:count($x/*[2]/*) = 3\n" +
                " and fn:count($x/*[3]/*[2]/*) = 0\n" +
                " and fn:count($x/*[3]/*[3]/*) = 0\n" +
                " and fn:count($x/*[3]/*) = 3\n" +
                " and fn:count($x/*) = 3\n" +
                "\n" +
                "return\n" +
                "<element><x>{$x}</x><p>{data(functx:path-to-node($x))}</p></element>}\n" +
                "</result>";

        BasicXQueryGenerator generator = BasicXQueryGenerator.getDefaultGenerator();
        assertThat(generator.generateQuery(cmmlInfo), is(expectedQuery));
    }

}