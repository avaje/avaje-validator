package io.avaje.validation.core;

import java.util.Set;

import io.avaje.validation.AnnotationValidationAdapter;
import io.avaje.validation.ConstraintViolation;

public class NoopAnnotationValidator<T> implements AnnotationValidationAdapter<T> {

  @Override
  public void validate(T type, Set<ConstraintViolation> violations) {
    // NOOP
  }
}
