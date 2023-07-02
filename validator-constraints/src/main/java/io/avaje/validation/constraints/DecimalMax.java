package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.DecimalMax.List;

@Target({METHOD, FIELD, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface DecimalMax {

  String message() default "{avaje.DecimalMax.message}";

  Class<?>[] groups() default {};

  /**
   * The {@code String} representation of the max value according to the {@code BigDecimal} string
   * representation.
   *
   * @return value the element must be lower or equal to
   */
  String value();

  /**
   * Specifies whether the specified maximum is inclusive or exclusive. By default, it is inclusive.
   *
   * @return {@code true} if the value must be lower or equal to the specified maximum, {@code
   *     false} if the value must be lower
   * @since 1.1
   */
  boolean inclusive() default true;

  /**
   * Defines several {@link DecimalMax} annotations on the same element.
   *
   * @see DecimalMax
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @interface List {

    DecimalMax[] value();
  }
}
