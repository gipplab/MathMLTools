package com.formulasearchengine.mathmltools.mml;


import com.formulasearchengine.mathmltools.helper.XMLHelper;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

public class CIdentifier implements Comparable<CIdentifier> {
    private Element n;

    public Integer getOrdinal() {
        return ordinal;
    }

    private int ordinal = -1;

    public CIdentifier(Element n, int i) {
        this.n = n;
        this.ordinal = i;
    }

    public String getName() {
        return n.getTextContent();
    }

    public Element getPresentation() throws XPathExpressionException {
        return (Element) XMLHelper.getElementById(n.getOwnerDocument(), getXref());
    }

    public String getXref() {
        return n.getAttribute("xref");
    }


    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(CIdentifier o) {
        return getOrdinal().compareTo(o.getOrdinal());

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CIdentifier) {
            return getName().equals(((CIdentifier) o).getName());
        }
        return super.equals(o);
    }
}
