package io.avaje.validation;

/**
 * Specify external types for which to generate Valid Adapters. Use when you can't place a @Valid
 * annotation on an external type (such as a mvn/gradle dependency).
 */
public @interface ImportValidPojo {

  Class<?>[] value();
}
