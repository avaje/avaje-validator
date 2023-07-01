package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.Constraint;

@Constraint
@Documented
@Target({METHOD, FIELD, TYPE_USE})
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
