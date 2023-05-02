package io.avaje.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Marks a type for validation.
 */
@Retention(CLASS)
@Target({TYPE, FIELD})
public @interface ValidPojo {

  /**
   */
  @Retention(CLASS)
  @Target({TYPE, PACKAGE})
  @interface Import {

    /**
     * Specify types to generate ValidPojo Adapters for.
     */
    Class<?>[] value();
  }
}
