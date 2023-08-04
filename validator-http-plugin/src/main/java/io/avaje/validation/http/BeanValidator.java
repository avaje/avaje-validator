package io.avaje.validation.http;

import java.util.*;

import io.avaje.http.api.ValidationException;
import io.avaje.http.api.ValidationException.Violation;
import io.avaje.inject.BeanScope;
import io.avaje.inject.PostConstruct;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

public class BeanValidator implements io.avaje.http.api.Validator {

  private Validator validator;
  private final Collection<Locale> locales;

  public BeanValidator(Collection<Locale> locales) {
    this.locales = locales;
  }

  @Override
  public void validate(Object bean, String acceptLanguage, Class<?>... groups) throws ValidationException {
    final Locale language = resolveLocale(acceptLanguage, locales);
    try {
      validator.validate(bean, language, groups);
    } catch (final ConstraintViolationException e) {
      throwExceptionWith(e);
    }
  }

  @PostConstruct
  void setValidator(BeanScope scope) {
    this.validator = scope.get(Validator.class);
  }

  private void throwExceptionWith(ConstraintViolationException cause) {
    List<Violation> errors = new ArrayList<>();
    for (final var violation : cause.violations()) {
      final var path = violation.path();
      final var field = violation.field();
      final var message = violation.message();
      errors.add(new Violation(path, field, message));
    }

    throw new ValidationException(422, cause.getMessage(), cause, errors);
  }
}
