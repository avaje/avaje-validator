package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.NegativeOrZero.List;

@Constraint
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
public @interface NegativeOrZero {

  String message() default "{avaje.NegativeOrZero.message}";

  Class<?>[] groups() default {};

  /**
   * Defines several {@link NegativeOrZero} constraints on the same element.
   *
   * @see NegativeOrZero
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  @interface List {

    NegativeOrZero[] value();
  }
}
