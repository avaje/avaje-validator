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
 * The string has to be a well-formed email address. Exact semantics of what makes up a valid email
 * address are left to the provided Email Annotation ValidationAdapter providers.
 * <p>
 * Accepts {@code CharSequence}.
 * {@code null} elements are considered valid.
 */
@Constraint
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
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

}
