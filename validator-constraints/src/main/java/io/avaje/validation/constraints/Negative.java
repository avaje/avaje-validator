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

import io.avaje.validation.constraints.Negative.List;

/**
 * The annotated element must be a strictly negative number (i.e. 0 is considered as an invalid
 * value).
 *
 * <p>Supported types are:
 *
 * <ul>
 *   <li>{@code BigDecimal}
 *   <li>{@code BigInteger}
 *   <li>{@code byte}, {@code short}, {@code int}, {@code long}, {@code float}, {@code double} and
 *       their respective wrappers
 * </ul>
 *
 * <p>{@code null} elements are considered valid.
 *
 * @author Gunnar Morling
 */
@Constraint(unboxPrimitives = true)
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
public @interface Negative {

  String message() default "{avaje.Negative.message}";

  Class<?>[] groups() default {};

  /**
   * Defines several {@link Negative} constraints on the same element.
   *
   * @see Negative
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  @interface List {

    Negative[] value();
  }
}
