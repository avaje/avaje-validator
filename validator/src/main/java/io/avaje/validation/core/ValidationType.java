package io.avaje.validation.core;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;

final class ValidationType<T> {

  private final ValidationContext ctx;
  private final ValidationAdapter<T> adapter;

  ValidationType(ValidationContext validator, ValidationAdapter<T> adapter) {
    this.ctx = validator;
    this.adapter = adapter;
  }

  void validate(T object, @Nullable Locale locale, List<Class<?>> groups)
      throws ConstraintViolationException {
    executeValidations(object, locale, groups).throwWithViolations();
  }

  Set<ConstraintViolation> check(T object, @Nullable Locale locale, List<Class<?>> groups) {
    return executeValidations(object, locale, groups).violations();
  }

  private ValidationRequest executeValidations(T object, Locale locale, List<Class<?>> groups) {
    final var req = ctx.request(locale, groups);
    adapter.validate(object, req);
    return req;
  }
}
