package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The annotated element must not be {@code null} and must contain at least one
 * non-whitespace character. Accepts {@code CharSequence}.
 *
 * @author Hardy Ferentschik
 * @see Character#isWhitespace(char)
 */
@Constraint(targets = CharSequence.class)
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface NotBlank {

  /**
   * Set the maximum length. By default, this is 0 (meaning unlimited).
   * This mimics the functionality of {@link Size} & {@link Length}, for a verbosity reduction.
   */
  int max() default 0;

  String message() default "{avaje.NotBlank.message}";

  Class<?>[] groups() default {};

}
