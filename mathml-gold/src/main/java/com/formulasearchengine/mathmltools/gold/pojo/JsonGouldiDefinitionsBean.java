package com.formulasearchengine.mathmltools.gold.pojo;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.LinkedList;

/**
 * @author Andre Greiner-Petter
 */
@JsonRootName(value = "definitions")
public class JsonGouldiDefinitionsBean {
    private LinkedList<JsonGouldiIdentifierDefinienBean> list;

    public LinkedList<JsonGouldiIdentifierDefinienBean> getIdentifierDefiniens() {
        return list;
    }

    public void setIdentifierDefiniens(LinkedList<JsonGouldiIdentifierDefinienBean> list) {
        this.list = list;
    }
}
