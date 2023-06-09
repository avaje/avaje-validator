package io.avaje.validation.inject.aspect;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.inject.aop.Aspect;

@Aspect
@Target(METHOD)
@Retention(RUNTIME)
/** Place on a method to execute validations on the parameters and return types */
public @interface ValidateMethod {
  String locale() default "";

  boolean throwOnParamFailure() default false;
}
