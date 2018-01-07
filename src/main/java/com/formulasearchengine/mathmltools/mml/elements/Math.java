package com.formulasearchengine.mathmltools.mml.elements;

import static com.formulasearchengine.mathmltools.xmlhelper.PartialLocalEntityResolver.getMmlDtd;
import static org.xmlunit.matchers.ValidationMatcher.valid;

import java.io.IOException;

import org.xmlunit.builder.Input;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import javax.xml.parsers.ParserConfigurationException;

public class Math {
    private final String s;
    private CMMLInfo cDom;
    private static Validator v;

    public Math(String s) throws IOException, ParserConfigurationException {
        this.cDom = new CMMLInfo(s,true);
        this.s=s;
    }

    CMMLInfo getInfoObject() {
        return cDom;
    }

    public boolean isValid(){
        return valid(getMathMLSchema()).matches(cDom);
    }

    public Iterable<ValidationProblem> getValdationProblems(){
        Validator v = getValidator();
        ValidationResult result = v.validateInstance(Input.fromString(s).build());
        //boolean valid = result.isValid();
        return result.getProblems();
    }

    private static Validator getValidator() {
        if (v==null){
            v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
            v.setSchemaSources(Input.fromURI("https://www.w3.org/Math/XMLSchema/mathml3/mathml3.xsd").build());
        }
        return v;
    }

    private Input.Builder getMathMLSchema() {
        return Input.fromStream(getMmlDtd());
    }

    @Override

    public String toString() {
        return cDom.toString();
    }
}
