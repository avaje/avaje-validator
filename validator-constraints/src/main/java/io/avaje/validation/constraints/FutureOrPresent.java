package io.avaje.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Repeatable(FutureOrPresent.List.class)
public @interface FutureOrPresent {

  String message() default "{avaje.FutureOrPresent.message}";

  Class<?>[] groups() default {};

  /**
   * Defines several {@code @FutureOrPresent} constraints on the same element.
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  @interface List {
    FutureOrPresent[] value();
  }
}
