package io.avaje.validation.constraints;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * The annotated element must be a String validated to be a valid URI.
 *
 * <p>Supported types are:
 *
 * <ul>
 *   <li>{@code String}
 *   <li>{@code CharSequence}
 * </ul>
 */
@Constraint
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface URI {

  /** Set a scheme to match. Per default any scheme is allowed. */
  String scheme() default "";

  /** Set the host to match, e.g. localhost. Per default any host is allowed. */
  String host() default "";

  /** Set the port to match, e.g. 80. Per default any port is allowed. */
  int port() default -1;

  /** Set a regular expression to match against. Per default anything is allowed. */
  String regexp() default "";

  /** Used in combination with {@link #regexp()} in order to specify a regular expression option */
  RegexFlag[] flags() default {};

  /** Set the message to use. Per default uses the built-in message key. */
  String message() default "{avaje.URI.message}";

  Class<?>[] groups() default {};
}
