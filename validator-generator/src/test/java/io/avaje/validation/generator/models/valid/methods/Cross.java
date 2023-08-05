package io.avaje.validation.generator.models.valid.methods;

import io.avaje.validation.CrossParamConstraint;

@CrossParamConstraint
public @interface Cross {

  String message() default "{io.avaje.validator.Cross}";

  Class<?>[] groups() default {};
}
