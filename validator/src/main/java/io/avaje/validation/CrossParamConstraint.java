package io.avaje.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Marks an method annotation as a CrossParamConstraint. */
@Retention(CLASS)
@Target({ANNOTATION_TYPE})
public @interface CrossParamConstraint {}
