package io.avaje.validation;

/**
 * Specify types to generate Valid Adapters for. Use if you can't place a @Valid annotation on an
 * external type.
 */
public @interface ImportValidPojo {

  Class<?>[] value();
}
