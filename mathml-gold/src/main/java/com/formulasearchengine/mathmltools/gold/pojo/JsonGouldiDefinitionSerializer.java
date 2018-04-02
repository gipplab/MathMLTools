package com.formulasearchengine.mathmltools.gold.pojo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.LinkedList;

/**
 * @author Andre Greiner-Petter
 */
public class JsonGouldiDefinitionSerializer extends JsonSerializer<JsonGouldiDefinitionsBean> {
    @Override
    public void serialize(JsonGouldiDefinitionsBean value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
        jgen.writeStartObject();
        for (JsonGouldiIdentifierDefinienBean bean : value.getIdentifierDefiniens()) {
            writeSingleDefinition(bean, jgen);
        }
        jgen.writeEndObject();
    }

    private void writeSingleDefinition(JsonGouldiIdentifierDefinienBean value, JsonGenerator jgen) throws IOException {
        LinkedList<String> pureTextList = new LinkedList<>();
        LinkedList<JsonGouldiWikidataDefinienBean> restList = new LinkedList<>();

        for (JsonGouldiWikidataDefinienBean definienBean : value.getDefiniens()) {
            if (definienBean instanceof JsonGouldiTextDefinienBean) {
                pureTextList.add(definienBean.getDiscription());
            } else {
                restList.add(definienBean);
            }
        }

        jgen.writeArrayFieldStart(value.getName());
        for (String s : pureTextList) {
            jgen.writeString(s);
        }

        jgen.writeStartObject();
        for (JsonGouldiWikidataDefinienBean b : restList) {
            jgen.writeStringField(b.getWikiID(), b.getDiscription());
        }
        jgen.writeEndObject();
        jgen.writeEndArray();

    }
}
