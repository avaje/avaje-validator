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

  public ValidationType(ValidationContext validator, ValidationAdapter<T> adapter) {
    this.ctx = validator;
    this.adapter = adapter;
  }

  public Set<ConstraintViolation> validate(T object, @Nullable Locale locale, List<Class<?>> groups)
      throws ConstraintViolationException {
    final var req = ctx.request(locale, groups);
    try {
      adapter.validate(object, req);
    } catch (final ConstraintViolationException e) {
      return e.violations();
    }
    return req.violations();
  }
}
