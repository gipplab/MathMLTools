package com.formulasearchengine.mathmltools.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * from http://stackoverflow.com/questions/1492428/javadom-how-do-i-set-the-base-namespace-of-an-already-created-document
 */

public class XmlNamespaceTranslator {

    private Map<Key<String>, Value<String>> translations = new HashMap<Key<String>, Value<String>>();
    private Set<String> unwantedAttributes = new HashSet<>();
    private String defaultNamespace = null;
    private Boolean preserveWhiteSpace = false;

    private void addToNodes(java.util.Stack<Node> nodes, ItemList attributes) {
        if (attributes.getLength() != 0) {
            for (int i = 0, count = attributes.getLength(); i < count; ++i) {
                Node attribute = attributes.item(i);
                if (attribute != null) {
                    nodes.push(attribute);
                }
            }
        }
    }

    public XmlNamespaceTranslator addTranslation(String fromNamespaceURI, String toNamespaceURI) {
        Key<String> key = new Key<String>(fromNamespaceURI);
        Value<String> value = new Value<String>(toNamespaceURI);

        this.translations.put(key, value);

        return this;
    }

    public XmlNamespaceTranslator addUnwantedAttribute(String name) {
        unwantedAttributes.add(name);
        return this;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public XmlNamespaceTranslator setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
        return this;
    }

    public Boolean getPreserveWhiteSpace() {
        return preserveWhiteSpace;
    }

    public XmlNamespaceTranslator setPreserveWhiteSpace(Boolean preserveWhiteSpace) {
        this.preserveWhiteSpace = preserveWhiteSpace;
        return this;
    }

    public Map<Key<String>, Value<String>> getTranslations() {
        return translations;
    }

    public XmlNamespaceTranslator setTranslations(Map<Key<String>, Value<String>> translations) {
        this.translations = translations;
        return this;
    }

    public void     translateNamespaces(Document xmlDoc, String prefix) {
        Stack<Node> nodes = new Stack<Node>();
        nodes.push(xmlDoc.getDocumentElement());

        while (!nodes.isEmpty()) {
            Node node = nodes.pop();
            switch (node.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    if (unwantedAttributes.contains(node.getNodeName())) {
                        Node parent = ((Attr) node).getOwnerElement();
                        parent.getAttributes().removeNamedItem(node.getNodeName());
                        // parentAttributes.getAttributes().removeNamedItem(node.getNodeName());
                    }
                case Node.ELEMENT_NODE:
                    Value<String> value = this.translations.get(new Key<String>(node.getNamespaceURI()));
                    if (value != null) {
                        // the reassignment to node is very important. as per javadoc renameNode will
                        // try to modify node (first parameter) in place. If that is not possible it
                        // will replace that node for a new created one and return it to the caller.
                        // if we did not reassign node we will get no childs in the loop below.
                        node = xmlDoc.renameNode(node, value.getValue(), node.getNodeName());
                    }
                    if (node.getPrefix() != null && node.getNamespaceURI().equals(defaultNamespace)) {
                        node.setPrefix(prefix);
                    }
                    break;
                case Node.TEXT_NODE:
                    if (node.getTextContent().trim().length() == 0) {
                        node.getParentNode().removeChild(node);
                    }
                    break;
                default:
            }

            // for attributes of this node
            NamedNodeMap attributes = node.getAttributes();
            addToNodes(nodes, new ItemList(attributes));

            // for child nodes of this node
            NodeList childNodes = node.getChildNodes();
            addToNodes(nodes, new ItemList(childNodes));
//            if (!(childNodes == null || childNodes.getLength() == 0)) {
//                for (int i = 0, count = childNodes.getLength(); i < count; ++i) {
//                    Node childNode = childNodes.item(i);
//                    if (childNode != null) {
//                        nodes.push(childNode);
//                    }
//                }
//            }
        }
    }

    // these will allow null values to be stored on a map so that we can distinguish
    // from values being on the map or not. map implementation returns null if the there
    // is no map element with a given key. If the value is null there is no way to
    // distinguish from value not being on the map or value being null. these classes
    // remove ambiguity.
    private static class Holder<T> {

        private final T value;

        private Holder(T value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Holder<?> other = (Holder<?>) obj;
            if (value == null) {
                if (other.value != null) {
                    return false;
                }
            } else if (!value.equals(other.value)) {
                return false;
            }
            return true;
        }

        public T getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }
    }

    private static class Key<T> extends Holder<T> {

        Key(T value) {
            super(value);
        }
    }

    private static class Value<T> extends Holder<T> {

        Value(T value) {
            super(value);
        }
    }

    class ItemList {
        private NodeList l = null;
        private NamedNodeMap a = null;

        ItemList(NodeList list) {
            l = list;
        }

        ItemList(NamedNodeMap list) {
            a = list;
        }

        public int getLength() {
            if (l != null) {
                return l.getLength();
            } else if (a != null) {
                return a.getLength();
            } else {
                return 0;
            }
        }

        public Node item(int i) {
            if (l != null) {
                return l.item(i);
            } else {
                return a.item(i);
            }
        }
    }
}
