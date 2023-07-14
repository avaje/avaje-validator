package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks an annotation as a Constraint class. Only annotations marked with Constraint are composable
 */
@Retention(CLASS)
@Target({ANNOTATION_TYPE})
public @interface Constraint {}
