package io.avaje.validation;

import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specify external types for which to generate Valid Adapters. Use when you can't place a @Valid
 * annotation on an external type (such as a mvn/gradle dependency).
 */
@Retention(SOURCE)
@Target({TYPE, PACKAGE, MODULE})
public @interface ImportValidPojo {

  Class<?>[] value();
}
