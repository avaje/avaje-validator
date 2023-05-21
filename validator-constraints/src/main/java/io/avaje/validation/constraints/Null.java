package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Null.List;

@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface Null {

  String message() default "{avaje.Null.message}";

  Class<?>[] groups() default {};

  /**
   * Defines several {@code @NotEmpty} constraints on the same element.
   *
   * @see Null
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  public @interface List {
    Null[] value();
  }
}
