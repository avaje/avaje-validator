package io.avaje.validation.generator.models.valid.typeconstraint;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Constraint;

@Constraint //uncomment to fail compilation
//(targets = String.class)
@Target(ElementType.TYPE_USE)
@Retention(SOURCE)
public @interface TypeConstrained {
  String message() default ""; // default error message

  Class<?>[] groups() default {}; // groups
}
