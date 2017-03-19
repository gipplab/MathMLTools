package com.formulasearchengine.mathmltools.xmlhelper;

import org.w3c.dom.*;

import java.util.*;

/**
 * from http://stackoverflow.com/questions/1492428/javadom-how-do-i-set-the-base-namespace-of-an-already-created-document
 */

public class XmlNamespaceTranslator {

  private Map<Key<String>, Value<String>> translations = new HashMap<Key<String>, Value<String>>();
  private Set<String> unwantedAttributes = new HashSet<>();
  private String defaultNamespace = null;
  private Boolean preserveWhiteSpace = false;

  public Map<Key<String>, Value<String>> getTranslations() {
    return translations;
  }

  public XmlNamespaceTranslator setTranslations(Map<Key<String>, Value<String>> translations) {
    this.translations = translations;
    return this;
  }

  public String getDefaultNamespace() {
    return defaultNamespace;
  }

  public XmlNamespaceTranslator setDefaultNamespace(String defaultNamespace) {
    this.defaultNamespace = defaultNamespace;
    return this;
  }

  public XmlNamespaceTranslator addTranslation(String fromNamespaceURI, String toNamespaceURI) {
    Key<String> key = new Key<String>(fromNamespaceURI);
    Value<String> value = new Value<String>(toNamespaceURI);

    this.translations.put(key, value);

    return this;
  }

  public XmlNamespaceTranslator addUnwantedAttribute(String Name) {
    unwantedAttributes.add(Name);
    return this;
  }

  public void translateNamespaces(Document xmlDoc) {
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
            node.setPrefix("");
          }
          break;
        case Node.TEXT_NODE:
          if (node.getTextContent().trim().length() == 0) {
            node.getParentNode().removeChild(node);
          }
      }

      // for attributes of this node
      NamedNodeMap attributes = node.getAttributes();
      if (!(attributes == null || attributes.getLength() == 0)) {
        for (int i = 0, count = attributes.getLength(); i < count; ++i) {
          Node attribute = attributes.item(i);
          if (attribute != null) {
            nodes.push(attribute);
          }
        }
      }

      // for child nodes of this node
      NodeList childNodes = node.getChildNodes();
      if (!(childNodes == null || childNodes.getLength() == 0)) {
        for (int i = 0, count = childNodes.getLength(); i < count; ++i) {
          Node childNode = childNodes.item(i);
          if (childNode != null) {
            nodes.push(childNode);
          }
        }
      }
    }
  }

  public Boolean getPreserveWhiteSpace() {
    return preserveWhiteSpace;
  }

  public XmlNamespaceTranslator setPreserveWhiteSpace(Boolean preserveWhiteSpace) {
    this.preserveWhiteSpace = preserveWhiteSpace;
    return this;
  }

  // these will allow null values to be stored on a map so that we can distinguish
  // from values being on the map or not. map implementation returns null if the there
  // is no map element with a given key. If the value is null there is no way to
  // distinguish from value not being on the map or value being null. these classes
  // remove ambiguity.
  private static class Holder<T> {

    protected final T value;

    public Holder(T value) {
      this.value = value;
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

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Holder<?> other = (Holder<?>) obj;
      if (value == null) {
        if (other.value != null)
          return false;
      } else if (!value.equals(other.value))
        return false;
      return true;
    }

  }

  private static class Key<T> extends Holder<T> {

    public Key(T value) {
      super(value);
    }

  }

  private static class Value<T> extends Holder<T> {

    public Value(T value) {
      super(value);
    }

  }
}
