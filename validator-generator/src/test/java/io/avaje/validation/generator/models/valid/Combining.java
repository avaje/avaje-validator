package io.avaje.validation.generator.models.valid;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Constraint;
import io.avaje.validation.constraints.NotNull;

@Target({FIELD})
@Retention(SOURCE)
@Constraint
@Combining2(message = "sus")
@NotNull
public @interface Combining {

  String message();

  Class<?>[] groups() default {};
}
