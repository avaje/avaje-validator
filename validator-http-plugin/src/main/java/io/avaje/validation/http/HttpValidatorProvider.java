package io.avaje.validation.http;

import io.avaje.inject.BeanScopeBuilder;
import io.avaje.spi.ServiceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Plugin for avaje inject that provides a default Http Validator instance.
 */
@ServiceProvider
public final class HttpValidatorProvider implements io.avaje.inject.spi.InjectPlugin {

  private static final Class<?> VALIDATOR_HTTP_CLASS = avajeHttpOnClasspath();

  private static Class<?> avajeHttpOnClasspath() {
    try {
      return Class.forName("io.avaje.http.api.Validator");
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  @Override
  public Class<?>[] provides() {
    return VALIDATOR_HTTP_CLASS == null ? new Class<?>[]{} : new Class<?>[]{VALIDATOR_HTTP_CLASS};
  }

  @Override
  public void apply(BeanScopeBuilder builder) {
    if (VALIDATOR_HTTP_CLASS == null) {
      return;
    }

    builder.provideDefault(null, VALIDATOR_HTTP_CLASS, () -> {
      final var props = builder.configPlugin();
      final var locales = new ArrayList<Locale>();

      props.get("validation.locale.default")
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
