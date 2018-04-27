package com.formulasearchengine.mathmltools.converters.latexml;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Andre Greiner-Petter
 */
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(AssumeLaTeXMLAvailabilityCondition.class)
public @interface AssumeLaTeXMLAvailability {
}
