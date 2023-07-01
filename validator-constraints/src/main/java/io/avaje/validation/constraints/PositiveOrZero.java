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
import io.avaje.validation.constraints.PositiveOrZero.List;

@Constraint
@Target({METHOD, FIELD, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
public @interface PositiveOrZero {

  String message() default "{avaje.PositiveOrZero.message}";

  Class<?>[] groups() default {};

  /**
   * Defines several {@link PositiveOrZero} constraints on the same element.
   *
   * @see PositiveOrZero
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  @interface List {

    PositiveOrZero[] value();
  }
}
