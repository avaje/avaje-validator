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
import io.avaje.validation.constraints.NotNull.List;

@Constraint
@Documented
@Target({METHOD, FIELD, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface NotNull {

  String message() default "{avaje.NotNull.message}";

  Class<?>[] groups() default {};

  /**
   * Defines several {@code @NotEmpty} constraints on the same element.
   *
   * @see NotNull
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  public @interface List {
    NotNull[] value();
  }
}
