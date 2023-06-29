package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Past.List;

@Documented
@Target({METHOD, FIELD, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface Past {

  String message() default "{avaje.Past.message}";

  Class<?>[] groups() default {};

  /**
   * Defines several {@code @Past} constraints on the same element.
   */
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  @interface List {
    Past[] value();
  }
}
