package com.formulasearchengine.mathmlquerygenerator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * from http://stackoverflow.com/questions/229310/how-to-ignore-whitespace-while-reading-a-file-to-produce-an-xml-dom
 *
 * @deprecated this class is obsolete and only exists for testing
 */
@Deprecated
public class NdLst implements NodeList, Iterable<Node> {

    private final List<Node> nodes;

    public NdLst(NodeList list) {
        nodes = new ArrayList<>();
        for (int i = 0; i < list.getLength(); i++) {
            if (!isWhitespaceNode(list.item(i))) {
                nodes.add(list.item(i));
            }
        }
    }

    @Override
    public Node item(int index) {
        return nodes.get(index);
    }

    @Override
    public int getLength() {
        return nodes.size();
    }

    private static boolean isWhitespaceNode(Node n) {
        if (n.getNodeType() == Node.TEXT_NODE) {
            String val = n.getNodeValue();
            return val.trim().length() == 0;
        } else {
            return false;
        }
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }
}