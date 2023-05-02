package io.avaje.validation.core;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;

public class NoopAnnotationValidator<T> implements ValidationAdapter<T> {

  @Override
  public boolean validate(T type, ValidationRequest req, String propertyName) {
    // NOOP
    return true;
  }
}
