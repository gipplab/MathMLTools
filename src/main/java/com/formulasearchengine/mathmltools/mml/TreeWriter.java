package com.formulasearchengine.mathmltools.mml;

import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;

import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mas9 on 3/9/15.
 */
@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
public class TreeWriter {
  private TreeWriter() {
  }

  /**
   * The Class Mynode.
   */
  private static class MyNode {

    /**
     * The node.
     */
    private Node node;

    /**
     * The q var.
     */
    private Map<String, Integer> qVar;

    /**
     * The out.
     */
    private String out;

    /**
     * Instantiates a new mynode.
     *
     * @param node the node
     * @param qVar the q var
     */
    private MyNode(Node node, Map<String, Integer> qVar) {
      this.node = node;
      this.qVar = qVar;
    }
  }

  /**
   * Compact form.
   *
   * @param node the node
   * @return the string
   */
  public static String CompactForm(Node node) {
    MyNode n = new MyNode(node, null);
    return CompactForm(n).out;
  }

  // <xPath,Name,Value>
  private static ArrayList<SimpleEntry<String, String>> traverseNode(Node n, String p) {
    ArrayList<SimpleEntry<String, String>> output = new ArrayList<>();
    String nName;
    if (n.getNodeType() != Node.TEXT_NODE) {
      nName = n.getNodeName();
      if (nName.startsWith("m:")) {
        nName = nName.substring(2);
      }
      if ("mws:qvar".equals(nName)) {
        return new ArrayList<>();
      }
      p += "/" + nName;
    }
    String nValue = n.getNodeValue();
    if (nValue != null) {
      nValue = nValue.trim();
      if (nValue.isEmpty()) {
        return new ArrayList<>();
      }
    } else {
      nValue = "";
    }

    if (n.hasChildNodes()) {
      for (int i = 0; i < n.getChildNodes().getLength(); i++) {
        output.addAll(traverseNode(n.getChildNodes().item(i), p));
      }
    } else {
      output.add(new SimpleEntry<>(p, nValue));
    }
    return output;
  }

  public static ArrayList<SimpleEntry<String, String>> getMMLLeaves(Node n) throws XPathExpressionException {
    Node cmmlRoot = XMLHelper.getElementB(n, "./semantics/*[1]");
    return traverseNode(cmmlRoot, "");
  }

  /**
   * Compact form.
   *
   * @param n the n
   * @return the mynode
   */
  private static MyNode CompactForm(MyNode n) {
    if (n.node.getNodeType() == Node.TEXT_NODE) {
      n.out = n.node.getNodeValue().trim();
    } else {
      n.out = n.node.getNodeName();
      if (n.out.startsWith("m:")) {
        n.out = n.out.substring(2);
      }
      if ("annotation-xml".equals(n.out)) {
        n.out = "";
      }
      if ("pquery".equals(n.out) || "cquery".equals(n.out)) {
        n.out = "";
      }
      if ("mws:qvar".equals(n.out)) {
        final String qname = n.node.getAttributes().getNamedItem("name").toString();
        if (n.qVar == null) {
          n.out = "\\qvar{x_0}";
          n.qVar = new HashMap<>();
          n.qVar.put(qname, 0);
        } else {
          Integer qInt = n.qVar.get(qname);
          if (qInt == null) {
            n.out = "\\qvar{x_" + n.qVar.size() + "}";
            n.qVar.put(qname, n.qVar.size());
          } else {
            n.out = "\\qqvar{x_" + qInt + "}";
          }
        }
      }
      if (n.node.hasChildNodes()) {
        String sChild = "";
        for (Node childNode = n.node.getFirstChild();
             childNode != null; childNode = childNode.getNextSibling()) {
          MyNode ret = CompactForm(new MyNode(childNode, n.qVar));
          String cn = ret.out;
          n.qVar = ret.qVar;
          if (!cn.isEmpty()) {
            sChild += cn + ";";
          }
        }
        if (sChild.endsWith(";")) {
          sChild = sChild.substring(0, sChild.length() - 1).trim();
        }
        if (!sChild.isEmpty() && sChild.codePointAt(0) != 8290 && sChild.codePointAt(0) != 8289) {
          if ("m:annotation-xml".equals(n.node.getNodeName())) {
            n.out += sChild;
          } else {
            n.out += '[' + sChild + ']';
          }

        }
      }
    }
    if ("mo".equals(n.out)) {
      n.out = ""; //Remove empty mo elements
    }
    return n;

  }
}
