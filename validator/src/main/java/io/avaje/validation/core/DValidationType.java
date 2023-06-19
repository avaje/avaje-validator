package io.avaje.validation.core;

import java.util.List;
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
  public void validate(T object, Locale locale, List<Class<?>> groups) {
    final var req = validator.request(locale, groups);
    adapter.validate(object, req);
    req.throwWithViolations();
  }

}
