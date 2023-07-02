package example.avaje.composable;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Constraint;
import io.avaje.validation.constraints.Digits;
import io.avaje.validation.constraints.Negative;
import io.avaje.validation.constraints.Positive;

@Digits(integer = 2)
@Constraint
@Retention(SOURCE)
@Target(TYPE)
public @interface DigitsContraint {

  String message() default "";

  Class<?>[] groups() default {};
}
