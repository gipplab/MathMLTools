package com.formulasearchengine.mathmltools.utils.mml;


import org.w3c.dom.Element;

public class CSymbol {
    private boolean strict;
    private Element n;

    public CSymbol(Element n, boolean strict) {
        this.strict = strict;
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
        return getCd() + ":" + getCName();
    }
}
