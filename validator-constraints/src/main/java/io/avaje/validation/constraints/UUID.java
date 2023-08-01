package io.avaje.validation.constraints;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * The annotated element must be a String validated to be a valid UUID.
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
@Repeatable(UUID.List.class)
public @interface UUID {
  String message() default "{avaje.UUID.message}";

  Class<?>[] groups() default {};

  @Target({ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface List {
    UUID[] value();
  }
}
