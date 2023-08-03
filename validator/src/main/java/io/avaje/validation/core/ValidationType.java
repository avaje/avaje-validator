package io.avaje.validation.core;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.avaje.lang.Nullable;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;

final class ValidationType<T> {

  private final ValidationContext ctx;
  private final ValidationAdapter<T> adapter;

  ValidationType(ValidationContext validator, ValidationAdapter<T> adapter) {
    this.ctx = validator;
    this.adapter = adapter;
  }

  void validate(T object, @Nullable Locale locale, List<Class<?>> groups)
      throws ConstraintViolationException {
    final var req = ctx.request(locale, groups);
    adapter.validate(object, req);
    req.throwWithViolations();
  }

  Set<ConstraintViolation> check(T object, @Nullable Locale locale, List<Class<?>> groups) {
    final var req = ctx.request(locale, groups);
    adapter.validate(object, req);
    return req.violations();
  }
}
