package io.avaje.validation;

import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a type for validation.
 */
@Retention(CLASS)
@Target(ElementType.TYPE)
public @interface ValidPojo {

  /**
   */
  @Retention(CLASS)
  @Target({ElementType.TYPE, ElementType.PACKAGE})
  @interface Import {

    /**
     * Specify types to generate ValidPojo Adapters for.
     */
    Class<?>[] value();
  }
}
