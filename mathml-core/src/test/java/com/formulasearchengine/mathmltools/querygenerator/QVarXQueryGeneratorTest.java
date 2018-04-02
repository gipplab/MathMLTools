package com.formulasearchengine.mathmltools.querygenerator;


import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import com.formulasearchengine.mathmltools.helper.XMLHelper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

public class QVarXQueryGeneratorTest {

    private static String QUERY_GEN_STRING = "com/formulasearchengine/mathmltools/querygenerator/";

    @SuppressWarnings("SameParameterValue")
    static public String getFileContents(String fname) throws IOException {
        try (InputStream is = QVarXQueryGeneratorTest.class.getClassLoader().getResourceAsStream(fname)) {
            final Scanner s = new Scanner(is, "UTF-8");
            //Stupid scanner tricks to read the entire file as one token
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

    private File getResources(String resourceName) {
        URL url = this.getClass().getClassLoader().getResource(resourceName);
        File dir = null;
        try {
            //IntelliJ would accept if (url != null) { ... } else { throw new NullPointerException(); }
            //but I don't like that
            //noinspection ConstantConditions
            dir = new File(url.toURI());
        } catch (Exception e) {
            fail("Cannot open test resource folder.");
        }
        return dir;
    }

    private void runTestCollection(String resourceName, boolean findRootApply) {
        runTestCollection(getResources(resourceName), findRootApply);
    }

    private void runTestCollection(File dir, boolean findRootApply) {
        String queryString = null;
        String reference = null;
        Document query = null;
        for (File nextFile : dir.listFiles()) {
            if (nextFile.getName().endsWith(".xml")) {
                File resultPath = new File(nextFile.getAbsolutePath().replace(".xml", ".xq"));
                try {
                    queryString = new String(Files.readAllBytes(nextFile.toPath()));
                    reference = new String(Files.readAllBytes(resultPath.toPath()));
                } catch (Exception e) {
                    fail("Cannot load test tuple (" + resultPath + ", ... " + nextFile.getName() + " )");
                }
                try {
                    query = XMLHelper.string2Doc(queryString, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Cannot parse reference document " + nextFile.getName());
                }
                QVarXQueryGenerator xQueryGenerator = new QVarXQueryGenerator(query);
                xQueryGenerator.addDefaultHeader();
                xQueryGenerator.setAddQvarMap(true);
                xQueryGenerator.setFindRootApply(findRootApply);
                assertEquals(reference, xQueryGenerator.toString(), "Example " + nextFile.getName() + " does not match reference.");
            }
        }
    }

    @Test
    public void testNoMath2() throws Exception {
        XQueryGenerator qg = new QVarXQueryGenerator();
        assertNull(qg.toString(), "Input without math should return null");
        assertNull(qg.generateQuery((Document) null), "Input without math document should return null");
    }

    @Test
    public void generateTest_withConfiguration() throws Exception {
        String documentStr = IOUtils.toString(this.getClass().getResourceAsStream("mathml_qvar_1.xml"), "UTF-8");
        CMMLInfo cmmlInfo = new CMMLInfo(documentStr);

        String expectedQuery = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";\n" +
                "declare namespace functx = \"http://www.functx.com\";\n" +
                "declare function functx:path-to-node( $nodes as node()* ) as xs:string* {\n" +
                "$nodes/string-join(ancestor-or-self::*/name(.), '/')\n" +
                " };\n" +
                "<result> {for $m in . return\n" +
                "for $x in $m//*:apply\n" +
                "[*[1]/name() = 'eq' and *[2]/name() = 'apply' and *[2][*[1]/name() = 'times'] and *[3]/name() = 'apply' and *[3][*[1]/name() = 'times' and *[2]/name() = 'ci' and *[2][./text() = 'i'] and *[3]/name() = 'ci' and *[3][./text() = 'd']]]\n" +
                "where\n" +
                "fn:count($x/*[2]/*) = 3\n" +
                " and fn:count($x/*[3]/*[2]/*) = 0\n" +
                " and fn:count($x/*[3]/*[3]/*) = 0\n" +
                " and fn:count($x/*[3]/*) = 3\n" +
                " and fn:count($x/*) = 3\n" +
                "\n" +
                "return\n" +
                "<element><x>{$x}</x><p>{data(functx:path-to-node($x))}</p></element>}\n" +
                "</result>";

        XQueryGenerator generator = QVarXQueryGenerator.getDefaultGenerator();
        assertThat(generator.generateQuery(cmmlInfo), is(expectedQuery));
    }


    @Test
    public void testMwsConversion() {
        runTestCollection(QUERY_GEN_STRING + "mws", false);
    }

    @Test
    public void testCmmlConversion() {
        runTestCollection(QUERY_GEN_STRING + "cmml", false);
    }

    @Test
    public void testFormatsConversion() {
        runTestCollection(QUERY_GEN_STRING + "formats", false);
    }

    @Test
    public void testRecurseConversion() {
        runTestCollection(QUERY_GEN_STRING + "recursive", true);
    }

    @Test
    public void testCustomization() throws Exception {
        final String testNamespace = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";";
        final String testPathToRoot = "//*:root";
        final String testResultFormat = "<hit>{$x}</hit>";
        final String testInput = getFileContents(QUERY_GEN_STRING + "cmml/q1.xml");
        final String expectedOutput = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";\n" +
                "for $m in //*:root return\n" +
                "for $x in $m//*:ci\n" +
                "[./text() = 'E']\n" +
                "where\n" +
                "fn:count($x/*) = 0\n" +
                "\n" +
                "return\n" +
                "<hit>{$x}</hit>";
        Document query = XMLHelper.string2Doc(testInput, true);
        QVarXQueryGenerator xQueryGenerator = new QVarXQueryGenerator(query);
        xQueryGenerator.setReturnFormat(testResultFormat).setNamespace(testNamespace)
                .setPathToRoot(testPathToRoot);
        assertEquals(expectedOutput, xQueryGenerator.toString());
        assertEquals(testResultFormat, xQueryGenerator.getReturnFormat());
        //assertEquals(testNamespace, xQueryGenerator.getNamespace());
    }

    @Test
    public void testNoRestriction() throws Exception {
        final String testInput = getFileContents(QUERY_GEN_STRING + "mws/qqx2x.xml");
        final String expectedOutput = "declare function local:qvarMap($x) {\n" +
                " map {\"x\" : (data($x/*[2]/*[2]/@xml:id),data($x/*[3]/@xml-id))}\n" + "};\n" +
                "for $m in //*:root return\n" + "for $x in $m//*:apply\n" +
                "[*[1]/name() = 'plus' and *[2]/name() = 'apply' and *[2][*[1]/name() = 'csymbol' and *[1][./text() = 'superscript'] and *[3]/name() = 'cn' and *[3][./text() = '2']]]\n" +
                "where\n" +
                "$x/*[2]/*[2] = $x/*[3]\n\n" +
                "return\n";
        Document query = XMLHelper.string2Doc(testInput, true);
        QVarXQueryGenerator xQueryGenerator = new QVarXQueryGenerator(query);
        xQueryGenerator.setAddQvarMap(true).setReturnFormat("").setPathToRoot("//*:root").setRestrictLength(false);
        assertEquals(expectedOutput, xQueryGenerator.toString());
        assertFalse(xQueryGenerator.isRestrictLength());
    }

    @Test
    public void testNoMath() throws Exception {
        final String input = "<?xml version=\"1.0\"?>\n<noMath />";
        QVarXQueryGenerator qg = new QVarXQueryGenerator();
        assertNull(qg.toString(), "Input without math should return null");
    }

    @Test
    public void testqVarGetter() throws Exception {
        final String expectedVariableName = "x";
        final String firstExpectedLocation = "/*[2]/*[2]";
        final String testInput = getFileContents(QUERY_GEN_STRING + "mws/qqx2x.xml");
        Document query = XMLHelper.string2Doc(testInput, false);
        QVarXQueryGenerator xQueryGenerator = new QVarXQueryGenerator(query);
        xQueryGenerator.setAddQvarMap(true);
        Map<String, ArrayList<String>> qVars = xQueryGenerator.getQvar();
        assertEquals(1, qVars.entrySet().size());
        Map.Entry<String, ArrayList<String>> firstEntry = qVars.entrySet().iterator().next();
        assertEquals(expectedVariableName, firstEntry.getKey());
        ArrayList<String> xPaths = firstEntry.getValue();
        assertEquals(2, xPaths.size());
        assertEquals(firstExpectedLocation, xPaths.get(0));
        assertEquals(true, xQueryGenerator.isAddQvarMap());
    }

}