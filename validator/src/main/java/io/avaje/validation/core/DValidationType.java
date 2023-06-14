package io.avaje.validation.core;

import java.util.Locale;

import io.avaje.validation.adapter.ValidationAdapter;

final class DValidationType<T> implements ValidationType<T> {

  private final DValidator validator;
  private final ValidationAdapter<T> adapter;

  DValidationType(DValidator validator, ValidationAdapter<T> adapter) {
    this.validator = validator;
    this.adapter = adapter;
  }

  @Override
  public void validate(T object, Locale locale) {
    final var req = validator.request(locale);
    adapter.validate(object, req);
    req.throwWithViolations();
  }

}
