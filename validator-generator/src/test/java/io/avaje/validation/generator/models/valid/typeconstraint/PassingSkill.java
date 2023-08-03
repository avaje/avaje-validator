package io.avaje.validation.generator.models.valid.typeconstraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Constraint;

@Target(TYPE)
@Retention(SOURCE)
@Constraint
public @interface PassingSkill {
  String message() default "put these foolish ambitions to rest"; // default error message

  Class<?>[] groups() default {}; // groups
}
