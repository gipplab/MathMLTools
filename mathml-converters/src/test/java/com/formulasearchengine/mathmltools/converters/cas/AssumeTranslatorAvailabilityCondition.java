package com.formulasearchengine.mathmltools.converters.cas;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

/**
 * @author Andre Greiner-Petter
 */
public class AssumeTranslatorAvailabilityCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        Optional<AssumeTranslatorAvailability> annotation =
                findAnnotation(extensionContext.getElement(), AssumeTranslatorAvailability.class);

        if (annotation.isPresent()){
            String jarPath = annotation.get().getJarPath();

            if (StringUtils.isEmpty(jarPath)){
                return ConditionEvaluationResult.disabled("Missing translator arguments. Skipping tests!");
            }

            Path jarP = Paths.get(jarPath);

            if (Files.notExists(jarP)){
                return ConditionEvaluationResult.disabled("Sources for translator are not available. Skipping tests!");
            } else {
                return ConditionEvaluationResult.enabled("Translator sources available. Continue translator tests!");
            }
        } else {
            return ConditionEvaluationResult.enabled("No check annotation. Continuing tests without conditions.");
        }
    }
}
