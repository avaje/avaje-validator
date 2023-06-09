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

import io.avaje.validation.constraints.Email.List;

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
public @interface Email {

  String message() default "{avaje.Email.message}";

  Class<?>[] groups() default {};

  /**
   * An additional regular expression the annotated element must match. The default is any string
   * ('.*')
   */
  String regexp() default ".*";

  /** Used in combination with {@link #regexp()} in order to specify a regular expression option */
  RegexFlag[] flags() default {};

  /**
   * Defines several {@link Email} annotations on the same element.
   *
   * @see Email
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  @interface List {

    Email[] value();
  }
}
