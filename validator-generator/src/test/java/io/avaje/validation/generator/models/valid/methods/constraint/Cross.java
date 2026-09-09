package io.avaje.validation.generator.models.valid.methods.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import io.avaje.validation.CrossParamConstraint;

@Target(ElementType.METHOD)
@CrossParamConstraint
public @interface Cross {

  String message() default "{io.avaje.validator.Cross}";

  Class<?>[] groups() default {};
}
