package com.formulasearchengine.mathmltools.gold.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Andre Greiner-Petter
 */
@JsonPropertyOrder({"tree", "qid"})
public class JsonGouldiCheckBean {
    @JsonProperty("tree")
    private Boolean tree;

    @JsonProperty("qid")
    private Boolean qid;

    public Boolean isTree() {
        return tree;
    }

    public void setTree(Boolean tree) {
        this.tree = tree;
    }

    public Boolean isQid() {
        return qid;
    }

    public void setQid(Boolean qid) {
        this.qid = qid;
    }
}
