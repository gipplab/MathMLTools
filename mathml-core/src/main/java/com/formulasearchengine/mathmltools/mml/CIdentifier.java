package com.formulasearchengine.mathmltools.mml;


import com.formulasearchengine.mathmltools.helper.XMLHelper;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CIdentifier implements Comparable<CIdentifier> {
    private Element n;

    public CIdentifier(Element n) {
        this.n = n;
    }

    public String getName() {
        return n.getTextContent();
    }

    public Node getPresentation() throws XPathExpressionException {
        return XMLHelper.getElementById(n.getOwnerDocument(),getXref());
    }

    public String getXref() {
        return n.getAttribute("xref");
    }


    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() { return  getName();    }

    @Override
    public int compareTo(CIdentifier o) {
        return  getName().compareTo(o.getName());
    }
}
