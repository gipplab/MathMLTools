package com.formulasearchengine.mathmltools.converters.mathoid;

import com.formulasearchengine.mathmltools.converters.MathoidConverter;
import com.formulasearchengine.mathmltools.converters.config.MathoidConfig;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

/**
 * @author Andre Greiner-Petter
 */

public class AssumeMathoidAvailabilityCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        Optional<AssumeMathoidAvailability> annotation =
                findAnnotation(extensionContext.getElement(), AssumeMathoidAvailability.class);

        if (annotation.isPresent()) {
            MathoidConverter c = new MathoidConverter(new MathoidConfig().setUrl(annotation.get().url()));

            if (c.isReachable()) {
                return ConditionEvaluationResult.enabled("Mathoid service is available. Continuing tests.");
            } else {
                return ConditionEvaluationResult.disabled("Mathoid service is not reachable, skip related tests.");
            }
        } else {
            return ConditionEvaluationResult.enabled("No check annotation. Continuing tests without conditions.");
        }

    }
}
