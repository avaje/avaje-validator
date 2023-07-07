package io.avaje.validation.generator.models.valid;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Constraint;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.NotNull;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(SOURCE)
@Constraint
@NotEmpty
public @interface Combining2 {

  String message();

  Class<?>[] groups() default {};
}
