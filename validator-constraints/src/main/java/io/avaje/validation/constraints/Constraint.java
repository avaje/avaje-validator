package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks an annotation as a constraint class.
 * Only annotations marked with {@code @Constraint} are composable.
 */
@Retention(CLASS)
@Target({ANNOTATION_TYPE})
public @interface Constraint {

  /** Determines if the constraint can validate primitives without boxing */
  boolean unboxPrimitives() default false;


  /**
   * The assignable types the constraint can be placed on. When the constraint
   * is placed on a type that cannot be assigned a compiler error will be thrown.
   */
  Class<?>[] targets() default {};

}
