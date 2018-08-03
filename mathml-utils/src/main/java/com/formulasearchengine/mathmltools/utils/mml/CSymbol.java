package com.formulasearchengine.mathmltools.utils.mml;


import com.formulasearchengine.mathmltools.mml.MathDoc;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;

public class CSymbol implements Comparable<CSymbol> {
    private Element n;
    private static final String SERIALIZATION_SEPARATOR = ":";

    public CSymbol(Element n) {
        this.n = n;
    }

    public String getCName() {
        return n.getTextContent();
    }

    public String getCd() {
        return n.getAttribute("cd");
    }

    public void setCd(String cd) {
        n.setAttribute("cd", cd);
    }

    @Override
    public String toString() {
        return getCd() + SERIALIZATION_SEPARATOR + getCName();
    }

    @Override
    public int compareTo(CSymbol o) {
        final int cdComp = getCd().compareTo(o.getCd());
        return cdComp != 0 ? cdComp : getCName().compareTo(o.getCName());
    }
}
