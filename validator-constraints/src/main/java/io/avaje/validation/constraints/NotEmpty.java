package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.NotEmpty.List;

@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface NotEmpty {

  String message() default "{avaje.validation.constraints.NotEmpty.message}";

  Class<?>[] groups() default {};

  /**
   * Defines several {@code @NotEmpty} constraints on the same element.
   *
   * @see NotEmpty
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  public @interface List {
    NotEmpty[] value();
  }
}
