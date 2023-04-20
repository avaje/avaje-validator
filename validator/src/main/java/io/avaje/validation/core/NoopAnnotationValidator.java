package io.avaje.validation.core;

import java.util.Map;

import io.avaje.validation.stream.ConstraintViolation;
//TODO Create an avaje config interpolator
public class NoopAnnotationValidator<T> implements AnnotationValidationAdapter<T> {

  @Override
  public AnnotationValidationAdapter<T> init(Map<String, String> annotationValueMap) {

    return this;
  }

  @Override
  public ConstraintViolation validate(Object type) {
    return null;
  }
}
