package com.formulasearchengine.mathmltools.io;

import com.formulasearchengine.mathmltools.helper.CMMLHelper;
import com.formulasearchengine.mathmltools.helper.RepairMMLHelper;
import com.formulasearchengine.mathmltools.helper.XMLHelper;
import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Andre Greiner-Petter
 */
public class ReaderStressTest {

    private static final Logger LOG = LogManager.getLogger(ReaderStressTest.class.getName());

    private static final String TEST_DIR = "com/formulasearchengine/mathmltools/broken";

    @Test
    void testMissingAll() throws Exception {
        final URL url = ReaderStressTest.class.getClassLoader().getResource(TEST_DIR + "/mini.mml");
        String s = new String(Files.readAllBytes(Paths.get(url.getFile())));
        Document doc = XmlDocumentReader.loadAndRepair(s, null);
        doc = RepairMMLHelper.forceResetNamespaces(doc);

        Node r = (Node) doc.getDocumentElement();
        printAttrInfo(r);

        NodeList list = r.getChildNodes();
        for ( int i = 0; i < list.getLength(); i++ )
            printAttrInfo( list.item(i) );

        String outxml = XmlDocumentWriter.stringify(doc);
        LOG.debug(outxml);

//
        Document doc2 = XmlDocumentReader.strictLoader(outxml);
//
//        r = (Node) doc2.getDocumentElement();
//        printAttrInfo(r);
//
//        list = r.getChildNodes();
//        for ( int i = 0; i < list.getLength(); i++ )
//            printAttrInfo( list.item(i) );
//
//
//        String outxml2 = XmlDocumentWriter.stringify(doc2);
//        System.out.println(outxml2.toString());
    }

    @Test
    void testPerfect() throws Exception {
        final URL url = ReaderStressTest.class.getClassLoader().getResource(TEST_DIR + "/perfect.mml");
        String s = new String(Files.readAllBytes(Paths.get(url.getFile())));
        Document doc = XmlDocumentReader.loadAndRepair(s, null);
        doc = RepairMMLHelper.forceResetNamespaces(doc);

//
//        Node r = (Node) doc.getDocumentElement();
//        printAttrInfo(r);
//
//        NodeList list = r.getChildNodes();
//        for ( int i = 0; i < list.getLength(); i++ )
//            printAttrInfo( list.item(i) );

        CMMLInfo cmml = new CMMLInfo(doc);

        Node n = CMMLHelper.getFirstApplyNode(cmml);


        XPath xpath = XMLHelper.namespaceAwareXpath("m", CMMLInfo.NS_MATHML);
        Node attrN = CMMLHelper.getElement(cmml, "*//m:mo/@stretchy", xpath);
        LOG.debug("ATTR: " + attrN);


        printAttrInfo(n);
        LOG.debug(n.getNodeName());

//        String outxml = XmlDocumentWriter.stringify(doc);

        LOG.debug(XMLHelper.printDocument(n));
        //System.out.println(outxml.toString());
    }

    private static void printAttrInfo( Node root ){
        NamedNodeMap attributes = root.getAttributes();
        if (attributes != null)
        {
            for (int i = 0; i < attributes.getLength(); i++)
            {
                Node node = attributes.item(i);
                if (node.getNodeType() == Node.ATTRIBUTE_NODE)
                {
                    String name = node.getNodeName();
                    LOG.debug(name + " " + node.getNamespaceURI());
                }
            }
        }
    }

    /*
    @ParameterizedTest
    @MethodSource("mmlResources")
    void testLoadFromString(Path f) throws IOException {
        String s = new String(Files.readAllBytes(f));
        Document doc = XmlDocumentReader.getDocumentFromXMLString(s);
        assertNotNull(doc);
    }

    @ParameterizedTest
    @MethodSource("mmlResources")
    void testLoadFromFile(Path f) throws IOException {
        Document doc = XmlDocumentReader.getDocumentFromXML(f);
        assertNotNull(doc);
    }

    @ParameterizedTest
    @MethodSource("xmlResources")
    void testLoadFromXMLFile(Path f) throws IOException {
        Document doc = XmlDocumentReader.getDocumentFromXML(f);
        assertNotNull(doc);
    }
    */

    private static Stream<Path> mmlResources() throws IOException {
        return fileResources().filter( p -> p.getFileName().toString().endsWith("mml") );
    }

    private static Stream<Path> xmlResources() throws IOException {
        return fileResources().filter( p -> p.getFileName().toString().endsWith("xml") );
    }

    private static Stream<Path> fileResources() throws IOException {
        final URL url = ReaderStressTest.class.getClassLoader().getResource(TEST_DIR);
        return Files
                .walk(Paths.get(url.getFile()))
                .filter(Files::isRegularFile);
    }

}
