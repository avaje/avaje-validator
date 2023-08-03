package example.avaje.crossfield;

import io.avaje.validation.constraints.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Target(TYPE)
@Retention(SOURCE)
@Constraint
public @interface APassingSkill {
  String message() default "put these foolish ambitions to rest"; // default error message

  Class<?>[] groups() default {}; // groups
}
