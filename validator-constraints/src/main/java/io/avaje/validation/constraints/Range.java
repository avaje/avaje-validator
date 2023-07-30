package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Range.List;

/**
 * The annotated element has to be in the appropriate range. Apply on numeric values or string
 * representation of the numeric value.
 *
 * @author Hardy Ferentschik
 */
@Documented
@Retention(RUNTIME)
@Repeatable(List.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
public @interface Range {
  long min() default 0;

  long max() default Long.MAX_VALUE;

  String message() default "{avaje.Range.message}";

  Class<?>[] groups() default {};

  /** Defines several {@code @Range} annotations on the same element. */
  @Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
  @Retention(RUNTIME)
  @Documented
  public @interface List {
    Range[] value();
  }
}
