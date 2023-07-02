package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Marks an annotation class as a Constraint. */
@Retention(CLASS)
@Target({ANNOTATION_TYPE})
public @interface Constraint {}
