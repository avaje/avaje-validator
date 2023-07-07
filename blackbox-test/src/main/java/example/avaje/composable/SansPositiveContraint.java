package example.avaje.composable;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;

@Retention(SOURCE)
@Target(FIELD)
@Constraint(validatedBy = {})
@PositiveContraint(message = "ignored message")
public @interface SansPositiveContraint {

  String message();

  Class<?>[] groups() default {};
}
