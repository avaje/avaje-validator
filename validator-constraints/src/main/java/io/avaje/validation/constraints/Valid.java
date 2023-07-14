package io.avaje.validation.constraints;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Marks a type for validation adapter generation
 *
 * <p>Additionally Marks a property, method parameter or method return type for validation
 * cascading.
 *
 * <p>Constraints defined on the object and its properties are validated when the property, method
 * parameter or method return type is validated.
 *
 * <p>This behavior is applied recursively.
 */
@Retention(CLASS)
@Target({TYPE, TYPE_USE, FIELD})
public @interface Valid {}
