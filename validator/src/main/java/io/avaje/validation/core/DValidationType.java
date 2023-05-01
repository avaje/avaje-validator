package io.avaje.validation.core;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;

import java.lang.reflect.Type;

class DValidationType<T> implements ValidationType<T> {

  protected final DValidator jsonb;
  protected final Type type;
  protected final ValidationAdapter<T> adapter;

  DValidationType(DValidator jsonb, Type type, ValidationAdapter<T> adapter) {
    this.jsonb = jsonb;
    this.type = type;
    this.adapter = adapter;
  }

  @Override
  public void validate(T object) {
    final var req = new ValidationRequest();
    adapter.validate(object, req);
    req.throwWithViolations();
  }

//  @Override
//  public void validateAll(Collection<T> collection) {
//    final var req = new ValidationRequest();
//    for (T element : collection) {
//      adapter.validate(element, req);
//    }
//    req.throwWithViolations();
//  }
}
