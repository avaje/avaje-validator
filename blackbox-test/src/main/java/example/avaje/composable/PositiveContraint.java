package example.avaje.composable;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Constraint;
import io.avaje.validation.constraints.Positive;

@Positive
@Constraint
@Target(TYPE)
@Retention(SOURCE)
@DigitsContraint
public @interface PositiveContraint {

  String message() default "";

  Class<?>[] groups() default {};
}
