package io.avaje.validation.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import io.avaje.inject.BeanScopeBuilder;

/** Plugin for avaje inject that provides a default Http Validator instance. */
public final class HttpValidatorProvider implements io.avaje.inject.spi.Plugin {

  private static final Class<?> VALIDATOR_HTTP_CLASS = httpOnCp();

  private static Class<?> httpOnCp() {
    try {
      return Class.forName("io.avaje.http.api.Validator");
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  @Override
  public Class<?>[] provides() {
    return VALIDATOR_HTTP_CLASS == null ? new Class<?>[] {} : new Class<?>[] {VALIDATOR_HTTP_CLASS};
  }

  @Override
  public void apply(BeanScopeBuilder builder) {
    if (VALIDATOR_HTTP_CLASS == null) {
      return;
    }

    builder.provideDefault(
        null,
        VALIDATOR_HTTP_CLASS,
        () -> {
          final var props = builder.propertyPlugin();

          final var locales = new ArrayList<Locale>();

          props
              .get("validation.locale.default")
              .map(Locale::forLanguageTag)
              .ifPresent(locales::add);

          props.get("validation.locale.addedLocales").stream()
              .flatMap(s -> Arrays.stream(s.split(",")))
              .map(Locale::forLanguageTag)
              .forEach(locales::add);

          final var beanValidator = new BeanValidator(locales);

          builder.addPostConstruct(beanValidator::setValidator);

          return beanValidator;
        });
  }
}
