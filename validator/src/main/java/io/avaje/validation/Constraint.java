package io.avaje.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Marks a type for validation. */
@Retention(SOURCE)
@Target({ANNOTATION_TYPE})
public @interface Constraint {}
