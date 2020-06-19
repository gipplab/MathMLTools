package com.formulasearchengine.mathmltools.converters.cas;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Andre Greiner-Petter
 */
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(AssumeTranslatorAvailabilityCondition.class)
public @interface AssumeTranslatorAvailability {
    /**
     * String path to translator jar file
     * @return path
     */
    String getJarPath();
}
