package io.avaje.validation.core;

import io.avaje.validation.AnnotationValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;

public class NoopAnnotationValidator<T> implements AnnotationValidationAdapter<T> {

  @Override
  public void validate(T type, ValidationRequest req) {
    // NOOP
  }
}
