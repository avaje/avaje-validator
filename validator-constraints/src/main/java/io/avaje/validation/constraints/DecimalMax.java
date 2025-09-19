package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The annotated element must be a number whose value must be lower or equal to the specified
 * maximum.
 * <p>
 * Supported types are:
 * <ul>
 *   <li>{@code BigDecimal}
 *   <li>{@code BigInteger}
 *   <li>{@code CharSequence}
 *   <li>{@code byte}, {@code short}, {@code int}, {@code long}, and their respective wrappers
 * </ul>
 * Note that {@code double} and {@code float} are not supported due to rounding errors (some
 * providers might provide some approximative support).
 * <p>
 * {@code null} elements are considered valid.
 *
 * @author Emmanuel Bernard
 */
@Constraint
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(DecimalMax.DecimalMaxs.class)
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
   * @return {@code true} if the value must be lower or equal to the specified maximum,
   *         {@code false} if the value must be lower
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
  @interface DecimalMaxs {

    DecimalMax[] value();
  }
}
