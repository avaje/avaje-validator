package io.avaje.validation.constraints;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * The annotated string length must be between the specified boundaries (included).
 * <p>
 * Supported types are:
 * <ul>
 *   <li>{@code CharSequence} (length of character sequence is evaluated)
 *   <li>{@code String} (length of character sequence is evaluated)
 * </ul>
 */
@Constraint
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Length.Lengths.class)
public @interface Length {

  String message() default "{avaje.Length.message}";

  Class<?>[] groups() default {};

  /** @return size the string must be higher or equal to */
  int min() default 0;

	/** @return size the string must be lower or equal to */
  int max() default Integer.MAX_VALUE;

  /**
	 * Defines several {@link Length} annotations on the same element.
	 *
	 * @see Length
	 */
  @Target({ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @interface Lengths {
    Length[] value();
  }
}
