package com.formulasearchengine.mathmltools.mml.elements;

import java.io.IOException;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import javax.xml.parsers.ParserConfigurationException;

public class Math {
    private CMMLInfo cDom;

    public Math(String cDom) throws IOException, ParserConfigurationException {
        this.cDom = new CMMLInfo(cDom);
    }

    public CMMLInfo getInfoObject() {
        return cDom;
    }

}
