package com.formulasearchengine.mathmltools.converters.mathoid;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Andre Greiner-Petter
 */
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(AssumeMathoidAvailabilityCondition.class)
public @interface AssumeMathoidAvailability {
    String url();
}
