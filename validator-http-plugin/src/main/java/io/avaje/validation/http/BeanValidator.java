package io.avaje.validation.http;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.avaje.http.api.ValidationException;
import io.avaje.inject.BeanScope;
import io.avaje.inject.PostConstruct;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;

public class BeanValidator implements io.avaje.http.api.Validator {

  private Validator validator;
  private final Collection<Locale> locales;

  public BeanValidator(Collection<Locale> locales) {
    this.locales = locales;
  }

  @Override
  public void validate(Object bean, String acceptLanguage, Class<?>... groups)
      throws ValidationException {

    final Locale language = resolveLocale(acceptLanguage, locales);

    throwExceptionWith(validator.validate(bean, language, groups));
  }

  @PostConstruct
  void setValidator(BeanScope scope) {
    this.validator = scope.get(Validator.class);
  }

  private void throwExceptionWith(Set<ConstraintViolation> violations) {

    if (violations.isEmpty()) return;

    final Map<String, Object> errors = new LinkedHashMap<>();
    for (final var violation : violations) {
      final var path = violation.path();
      final var message = violation.message();
      errors.put(path, message);
    }

    throw new ValidationException(422, "Request failed validation", errors);
  }
}
