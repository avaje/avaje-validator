package io.avaje.validation.core;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;

import java.lang.reflect.Type;

class DValidationType<T> implements ValidationType<T> {

  protected final DValidator validator;
  protected final Type type;
  protected final ValidationAdapter<T> adapter;

  DValidationType(DValidator validator, Type type, ValidationAdapter<T> adapter) {
    this.validator = validator;
    this.type = type;
    this.adapter = adapter;
  }

  @Override
  public void validate(T object) {
    final var req = ValidationRequest.create();
    adapter.validate(object, req);
    req.throwWithViolations();
  }

}
