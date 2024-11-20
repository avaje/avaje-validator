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
 * The annotated element size must be between the specified boundaries (included).
 *
 * <p>Supported types are:
 *
 * <ul>
 *   <li>{@code CharSequence} (length of character sequence is evaluated)
 *   <li>{@code Collection} (collection size is evaluated)
 *   <li>{@code Map} (map size is evaluated)
 *   <li>Array (array length is evaluated)
 * </ul>
 *
 * <p>{@code null} elements are considered valid.
 *
 * @author Emmanuel Bernard
 */
@Constraint
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Size.Sizes.class)
public @interface Size {
  String message() default "{avaje.Size.message}";

  Class<?>[] groups() default {};

  int min() default 0;

  int max() default Integer.MAX_VALUE;

  @Target({ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @interface Sizes {
    Size[] value();
  }
}
