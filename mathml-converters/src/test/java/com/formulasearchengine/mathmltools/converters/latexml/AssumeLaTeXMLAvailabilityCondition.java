package com.formulasearchengine.mathmltools.converters.latexml;

import com.formulasearchengine.mathmltools.converters.LaTeXMLConverter;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

/**
 * @author Andre Greiner-Petter
 */
public class AssumeLaTeXMLAvailabilityCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        Optional<AssumeLaTeXMLAvailability> annotation =
                findAnnotation(extensionContext.getElement(), AssumeLaTeXMLAvailability.class);

        if ( annotation.isPresent() ){
            if (LaTeXMLConverter.isLaTeXMLPresent()) {
                return ConditionEvaluationResult.enabled("LaTeXML is available. Continuing tests.");
            } else {
                return ConditionEvaluationResult.disabled("LaTeXML is not available, skip related tests.");
            }
        } else {
            return ConditionEvaluationResult.enabled("No check LaTeXML annotation. Continuing tests without conditions.");
        }
    }
}
