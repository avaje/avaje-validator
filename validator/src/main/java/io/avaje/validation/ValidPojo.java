package io.avaje.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
