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

import io.avaje.validation.constraints.NotBlank.List;

/**
 * The annotated element must not be {@code null} and must contain at least one non-whitespace
 * character. Accepts {@code CharSequence}.
 *
 * @author Hardy Ferentschik
 * @see Character#isWhitespace(char)
 */
@Constraint
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface NotBlank {

  String message() default "{avaje.NotBlank.message}";

  Class<?>[] groups() default {};

  /**
   * Defines several {@code @NotEmpty} constraints on the same element.
   *
   * @see NotBlank
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  public @interface List {
    NotBlank[] value();
  }
}
