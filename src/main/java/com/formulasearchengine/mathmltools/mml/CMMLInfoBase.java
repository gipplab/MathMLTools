package com.formulasearchengine.mathmltools.mml;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

class CMMLInfoBase {
    @SuppressWarnings("all")
    Document cmmlDoc;
    public final Node adoptNode(Node node) throws DOMException {
        return cmmlDoc.adoptNode(node);
    }

    public final Node appendChild(Node node) throws DOMException {
        return cmmlDoc.appendChild(node);
    }

    public final Node cloneNode(boolean b) {
        return cmmlDoc.cloneNode(b);
    }

    public final short compareDocumentPosition(Node node) throws DOMException {
        return cmmlDoc.compareDocumentPosition(node);
    }

    public final Attr createAttribute(String s) throws DOMException {
        return cmmlDoc.createAttribute(s);
    }

    public final Attr createAttributeNS(String s, String s1) throws DOMException {
        return cmmlDoc.createAttributeNS(s, s1);
    }

    public final CDATASection createCDATASection(String s) throws DOMException {
        return cmmlDoc.createCDATASection(s);
    }

    public final Comment createComment(String s) {
        return cmmlDoc.createComment(s);
    }

    public final DocumentFragment createDocumentFragment() {
        return cmmlDoc.createDocumentFragment();
    }

    public final Element createElement(String s) throws DOMException {
        return cmmlDoc.createElement(s);
    }

    public final Element createElementNS(String s, String s1) throws DOMException {
        return cmmlDoc.createElementNS(s, s1);
    }

    public final EntityReference createEntityReference(String s) throws DOMException {
        return cmmlDoc.createEntityReference(s);
    }

    public final ProcessingInstruction createProcessingInstruction(String s, String s1) throws DOMException {
        return cmmlDoc.createProcessingInstruction(s, s1);
    }

    public final Text createTextNode(String s) {
        return cmmlDoc.createTextNode(s);
    }

    public final NamedNodeMap getAttributes() {
        return cmmlDoc.getAttributes();
    }

    public final String getBaseURI() {
        return cmmlDoc.getBaseURI();
    }

    public final NodeList getChildNodes() {
        return cmmlDoc.getChildNodes();
    }

    public final DocumentType getDoctype() {
        return cmmlDoc.getDoctype();
    }

    public final Element getDocumentElement() {
        return cmmlDoc.getDocumentElement();
    }

    public final String getDocumentURI() {
        return cmmlDoc.getDocumentURI();
    }

    public final void setDocumentURI(String s) {
        cmmlDoc.setDocumentURI(s);
    }

    public final DOMConfiguration getDomConfig() {
        return cmmlDoc.getDomConfig();
    }

    public final Element getElementById(String s) {
        return cmmlDoc.getElementById(s);
    }

    public final NodeList getElementsByTagName(String s) {
        return cmmlDoc.getElementsByTagName(s);
    }

    public final NodeList getElementsByTagNameNS(String s, String s1) {
        return cmmlDoc.getElementsByTagNameNS(s, s1);
    }

    public final Object getFeature(String s, String s1) {
        return cmmlDoc.getFeature(s, s1);
    }

    public final Node getFirstChild() {
        return cmmlDoc.getFirstChild();
    }

    public final DOMImplementation getImplementation() {
        return cmmlDoc.getImplementation();
    }

    public final String getInputEncoding() {
        return cmmlDoc.getInputEncoding();
    }

    public final Node getLastChild() {
        return cmmlDoc.getLastChild();
    }

    public final String getLocalName() {
        return cmmlDoc.getLocalName();
    }

    public final String getNamespaceURI() {
        return cmmlDoc.getNamespaceURI();
    }

    public final Node getNextSibling() {
        return cmmlDoc.getNextSibling();
    }

    public final String getNodeName() {
        return cmmlDoc.getNodeName();
    }

    public final short getNodeType() {
        return cmmlDoc.getNodeType();
    }

    public final String getNodeValue() throws DOMException {
        return cmmlDoc.getNodeValue();
    }

    public final void setNodeValue(String s) throws DOMException {
        cmmlDoc.setNodeValue(s);
    }

    public final Document getOwnerDocument() {
        return cmmlDoc.getOwnerDocument();
    }

    public final Node getParentNode() {
        return cmmlDoc.getParentNode();
    }

    public final String getPrefix() {
        return cmmlDoc.getPrefix();
    }

    public final void setPrefix(String s) throws DOMException {
        cmmlDoc.setPrefix(s);
    }

    public final Node getPreviousSibling() {
        return cmmlDoc.getPreviousSibling();
    }

    public final boolean getStrictErrorChecking() {
        return cmmlDoc.getStrictErrorChecking();
    }

    public final void setStrictErrorChecking(boolean b) {
        cmmlDoc.setStrictErrorChecking(b);
    }

    public final String getTextContent() throws DOMException {
        return cmmlDoc.getTextContent();
    }

    public final void setTextContent(String s) throws DOMException {
        cmmlDoc.setTextContent(s);
    }

    public final Object getUserData(String s) {
        return cmmlDoc.getUserData(s);
    }

    public final String getXmlEncoding() {
        return cmmlDoc.getXmlEncoding();
    }

    public final boolean getXmlStandalone() {
        return cmmlDoc.getXmlStandalone();
    }

    public final void setXmlStandalone(boolean b) throws DOMException {
        cmmlDoc.setXmlStandalone(b);
    }

    public final String getXmlVersion() {
        return cmmlDoc.getXmlVersion();
    }

    public final void setXmlVersion(String s) throws DOMException {
        cmmlDoc.setXmlVersion(s);
    }

    public final boolean hasAttributes() {
        return cmmlDoc.hasAttributes();
    }

    public final boolean hasChildNodes() {
        return cmmlDoc.hasChildNodes();
    }

    public final Node importNode(Node node, boolean b) throws DOMException {
        return cmmlDoc.importNode(node, b);
    }

    public final Node insertBefore(Node node, Node node1) throws DOMException {
        return cmmlDoc.insertBefore(node, node1);
    }

    public final boolean isDefaultNamespace(String s) {
        return cmmlDoc.isDefaultNamespace(s);
    }

    public final boolean isEqualNode(Node node) {
        return cmmlDoc.isEqualNode(node);
    }

    public final boolean isSameNode(Node node) {
        return cmmlDoc.isSameNode(node);
    }

    public final boolean isSupported(String s, String s1) {
        return cmmlDoc.isSupported(s, s1);
    }

    public final String lookupNamespaceURI(String s) {
        return cmmlDoc.lookupNamespaceURI(s);
    }

    public final String lookupPrefix(String s) {
        return cmmlDoc.lookupPrefix(s);
    }

    public final void normalize() {
        cmmlDoc.normalize();
    }

    public final void normalizeDocument() {
        cmmlDoc.normalizeDocument();
    }

    public final Node removeChild(Node node) throws DOMException {
        return cmmlDoc.removeChild(node);
    }

    public final Node renameNode(Node node, String s, String s1) throws DOMException {
        return cmmlDoc.renameNode(node, s, s1);
    }

    public final Node replaceChild(Node node, Node node1) throws DOMException {
        return cmmlDoc.replaceChild(node, node1);
    }

    public final Object setUserData(String s, Object o, UserDataHandler userDataHandler) {
        return cmmlDoc.setUserData(s, o, userDataHandler);
    }
}
