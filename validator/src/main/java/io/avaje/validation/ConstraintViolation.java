package io.avaje.validation;

/**
 * Describes a constraint violation. This object exposes the constraint violation context as well as
 * the message describing the violation.
 */
public record ConstraintViolation(String path, String field, String message) {}
