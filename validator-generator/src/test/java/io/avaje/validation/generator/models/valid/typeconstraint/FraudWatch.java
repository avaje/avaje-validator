package io.avaje.validation.generator.models.valid.typeconstraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Constraint;

@Constraint
@Target(TYPE)
@Retention(SOURCE)
public @interface FraudWatch {
  String message() default "Frauds are not allowed"; // default error message

  Class<?>[] groups() default {}; // groups
}
