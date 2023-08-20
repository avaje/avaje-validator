package io.avaje.validation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.inject.aop.Aspect;

/** Place on a method to execute validations on the parameters and return types */
@Aspect(ordering = 10)
@Target(METHOD)
@Retention(RUNTIME)
public @interface ValidMethod {
  String locale() default "";

  boolean throwOnParamFailure() default true;
}
