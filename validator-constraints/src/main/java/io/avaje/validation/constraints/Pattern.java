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

import io.avaje.validation.constraints.Pattern.List;

/**
 * The annotated {@code CharSequence} must match the specified regular expression. The regular
 * expression follows the Java regular expression conventions see {@link java.util.regex.Pattern}.
 *
 * <p>Accepts {@code CharSequence}. {@code null} elements are considered valid.
 */
@Constraint
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
public @interface Pattern {

  /** the regular expression to match */
  String regexp();

  /** array of {@code RegexFlag}s considered when resolving the regular expression */
  RegexFlag[] flags() default {};

  /** the error message template */
  String message() default "{avaje.Pattern.message}";

  /** the groups the constraint belongs to */
  Class<?>[] groups() default {};

  /**
   * Defines several {@link Pattern} annotations on the same element.
   *
   * @see Pattern
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  @interface List {

    Pattern[] value();
  }
}
