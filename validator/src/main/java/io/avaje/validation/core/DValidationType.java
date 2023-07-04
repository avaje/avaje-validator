package io.avaje.validation.core;

import java.util.List;
import java.util.Locale;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;

final class DValidationType<T> implements ValidationType<T> {

  private final ValidationContext ctx;
  private final ValidationAdapter<T> adapter;

  DValidationType(ValidationContext validator, ValidationAdapter<T> adapter) {
    this.ctx = validator;
    this.adapter = adapter;
  }

  @Override
  public void validate(T object, Locale locale, List<Class<?>> groups) {
    final var req = ctx.request(locale, groups);
    adapter.validate(object, req);
    req.throwWithViolations();
  }

}
