package io.avaje.validation.generator.models.valid;

import io.avaje.validation.constraints.Constraint;

@Constraint
public @interface CheckCase {

  String message() default "{io.avaje.validator.CheckCase}"; // default error message

  Class<?>[] groups() default {}; // groups

  CaseMode value(); // specify case mode

  public enum CaseMode {
    UPPER,
    LOWER;
  }
}
