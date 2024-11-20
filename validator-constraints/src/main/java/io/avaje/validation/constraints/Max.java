package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be a number whose value must be lower or equal to the specified
 * maximum.
 *
 * <p>Supported types are:
 *
 * <ul>
 *   <li>{@code BigDecimal}
 *   <li>{@code BigInteger}
 *   <li>{@code byte}, {@code short}, {@code int}, {@code long}, and their respective wrappers
 * </ul>
 *
 * Note that {@code double} and {@code float} are not supported due to rounding errors (some
 * providers might provide some approximative support).
 *
 * <p>{@code null} elements are considered valid.
 *
 * @author Emmanuel Bernard
 */
@Constraint(unboxPrimitives = true)
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Max.Maxs.class)
public @interface Max {
  String message() default "{avaje.Max.message}";

  Class<?>[] groups() default {};

  long value();

  @Target({ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @interface Maxs {
    Max[] value();
  }
}
