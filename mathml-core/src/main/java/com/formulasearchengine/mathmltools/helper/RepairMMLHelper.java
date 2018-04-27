package com.formulasearchengine.mathmltools.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.formulasearchengine.mathmltools.helper.XMLHelper.NS_MATHML;

/**
 * @author Andre Greiner-Petter
 */
public class RepairMMLHelper {

    private static final Logger LOG = LogManager.getLogger(RepairMMLHelper.class.getName());

    private RepairMMLHelper() {
    }

    public static Document forceResetNamespaces(Document doc) throws Exception {
        doc.getDocumentElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", NS_MATHML);
        doc.getDocumentElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");
        return doc;
    }

    public static Document forceResetNamespaces(Document doc, String prefix) throws Exception {
        String ns = "xmlns";
        if (prefix != null && !prefix.isEmpty()) {
            ns += ":" + prefix;
        }
        doc.getDocumentElement().setAttributeNS("http://www.w3.org/2000/xmlns/", ns, NS_MATHML);
        doc.getDocumentElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");


        setPrefixRecursive(doc.getDocumentElement(), prefix);

        return doc;
    }

    public static void setPrefixRecursive(Node node, String prefix) {
        if (node.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == Node.ATTRIBUTE_NODE) {
            LOG.debug("Change prefix for: " + node.getClass() + " / " + node.getNodeName());
            node.setPrefix(prefix);
        }

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            setPrefixRecursive(list.item(i), prefix);
        }
    }

    public static void renameNamespaceRecursive(Document doc, Node node,
                                                String namespace, String prefix) {
        if (node.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == Node.ATTRIBUTE_NODE) {
            LOG.debug("Change prefix for: " + node.getClass() + " / " + node.getNodeName());
            //System.out.println("renaming type: " + node.getClass()
            //        + ", name: " + node.getNodeName());
            node.setPrefix(prefix);
            //doc.renameNode(node, namespace, prefix + node.getNodeName());
        }

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            renameNamespaceRecursive(doc, list.item(i), namespace, prefix);
        }
    }
}
