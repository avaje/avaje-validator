package io.avaje.validation.constraints;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Marks a type for validation.
 */
@Retention(CLASS)
@Target({TYPE,TYPE_USE, FIELD})
public @interface Valid {

  /**
   */

}
