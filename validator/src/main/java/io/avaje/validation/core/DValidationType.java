package io.avaje.validation.core;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;

import java.lang.reflect.Type;

final class DValidationType<T> implements ValidationType<T> {

  private final ValidationAdapter<T> adapter;

  DValidationType(ValidationAdapter<T> adapter) {
    this.adapter = adapter;
  }

  @Override
  public void validate(T object) {
    final var req = ValidationRequest.create();
    adapter.validate(object, req);
    req.throwWithViolations();
  }

}
