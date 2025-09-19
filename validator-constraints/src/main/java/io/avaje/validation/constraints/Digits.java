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
 * The annotated element must be a number within accepted range.
 * <p>
 * Supported types are:
 * <ul>
 *   <li>{@code BigDecimal}
 *   <li>{@code BigInteger}
 *   <li>{@code CharSequence}
 *   <li>{@code byte}, {@code short}, {@code int}, {@code long}, and their respective wrapper types
 * </ul>
 * <p>
 * {@code null} elements are considered valid.
 *
 * @author Emmanuel Bernard
 */
@Constraint
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Digits.Digitses.class)
public @interface Digits {
  String message() default "{avaje.Digits.message}";

  Class<?>[] groups() default {};

  /** Return maximum number of integral digits accepted for this number */
  int integer();

  /** Return maximum number of fractional digits accepted for this number */
  int fraction() default 0;

	/**
	 * Defines several {@link Digits} annotations on the same element.
	 *
	 * @see Digits
	 */
  @Target({ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @interface Digitses {
    Digits[] value();
  }
}
