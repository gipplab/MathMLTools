package com.formulasearchengine.mathmltools.gold.pojo;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "definitions",
        "constraints",
        "math_inputtex",
        "math_inputtex_semantic",
        "correct_tex",
        "correct_mml",
        "uri",
        "title",
        "comment",
        "type",
        "check"
})
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonGouldiBean {
    @JsonProperty("math_inputtex")
    private String mathTex;

    @JsonProperty("math_inputtex_semantic")
    private String mathTexSemantic;

    @JsonProperty("title")
    private String title;

    @JsonProperty("correct_tex")
    private String correctTex;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("correct_mml")
    private String mml;

    @JsonProperty("constraints")
    private List<String> constraints;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("type")
    private String type;

    @JsonProperty("check")
    private JsonGouldiCheckBean check;

    // stores definitions -> ignore because the annotated functions should be used
    // for serialization and deserialization
    @JsonIgnore
    private JsonGouldiDefinitionsBean definitionsBean;

    // stores unknown properties
    @JsonIgnore
    private Map<String, Object> other = new HashMap<>();

    // Just a default constructor
    public JsonGouldiBean() {
    }

    /**
     * Provide a custom deserialization for definitions
     *
     * @param defs the generic object for definitions field in gouldi
     */
    @SuppressWarnings("unchecked") // bla bla, we know what we are doing here...
    @JsonProperty("definitions")
    private void deserializeDefinitionsField(Map<String, Object> defs) {
        definitionsBean = new JsonGouldiDefinitionsBean();
        LinkedList<JsonGouldiIdentifierDefinienBean> list = new LinkedList<>();
        definitionsBean.setIdentifierDefiniens(list);

        for (String key : defs.keySet()) {
            JsonGouldiIdentifierDefinienBean bean = new JsonGouldiIdentifierDefinienBean();
            ArrayList<JsonGouldiWikidataDefinienBean> arrList = new ArrayList<>();
            bean.setName(key);

            ArrayList<Object> identifierList = (ArrayList<Object>) defs.get(key);
            for (Object obj : identifierList) {
                if (obj instanceof String) {
                    JsonGouldiTextDefinienBean textBean = new JsonGouldiTextDefinienBean();
                    textBean.setDiscription((String) obj);
                    arrList.add(textBean);
                } else {
                    Map<String, String> qidMappings = (Map<String, String>) obj;
                    for (String qID : qidMappings.keySet()) {
                        JsonGouldiWikidataDefinienBean wikidefbean = new JsonGouldiWikidataDefinienBean();
                        wikidefbean.setWikiID(qID);
                        wikidefbean.setDiscription(qidMappings.get(qID));
                        arrList.add(wikidefbean);
                    }
                }
            }

            JsonGouldiWikidataDefinienBean[] arr = new JsonGouldiWikidataDefinienBean[arrList.size()];
            arr = arrList.toArray(arr);
            bean.setDefiniens(arr);
            list.add(bean);
        }
    }

    @JsonGetter("definitions")
    @JsonSerialize(using = JsonGouldiDefinitionSerializer.class)
    public JsonGouldiDefinitionsBean getDefinitions() {
        return definitionsBean;
    }

    @JsonAnyGetter
    public Map<String, Object> getUnknownProperties() {
        return other;
    }

    @JsonAnySetter
    public void unknownPropertySet(String name, Object value) {
        other.put(name, value);
    }

    /**
     * Debugger function to find out if the current bean object has stored unknown properties.
     *
     * @return true if there are unknown properties in this object.
     */
    public boolean hasUnknowProperties() {
        return !other.isEmpty();
    }

    public String getOriginalTex() {
        return mathTex;
    }

    public void setOriginalTex(String mathTex) {
        this.mathTex = mathTex;
    }

    public String getSemanticTex() {
        return mathTexSemantic;
    }

    public void setSemanticTex(String mathTexSemantic) {
        this.mathTexSemantic = mathTexSemantic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCorrectedTex() {
        return correctTex;
    }

    public void setCorrectedTex(String correctTex) {
        this.correctTex = correctTex;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMml() {
        return mml;
    }

    public void setMml(String mml) {
        this.mml = mml;
    }

    public List<String> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<String> constraints) {
        this.constraints = constraints;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonGouldiCheckBean getCheck() {
        return check;
    }

    public void setCheck(JsonGouldiCheckBean check) {
        this.check = check;
    }

    public JsonGouldiDefinitionsBean getDefinitionsBean() {
        return definitionsBean;
    }

    public void setDefinitionsBean(JsonGouldiDefinitionsBean definitionsBean) {
        this.definitionsBean = definitionsBean;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
