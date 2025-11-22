package io.avaje.validation.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import io.avaje.http.api.Validator;
import io.avaje.inject.BeanScopeBuilder;

/**
 * Plugin for avaje inject that provides a default Http Validator instance.
 */
public final class HttpValidatorProvider implements io.avaje.inject.spi.InjectPlugin {

  private static final boolean VALIDATOR_HTTP_CLASS = avajeHttpOnClasspath();

  private static boolean avajeHttpOnClasspath() {
    try {
      if (ModuleLayer.boot().findModule("io.avaje.http.api").isPresent()) {
        return true;
      }
      Class.forName("io.avaje.http.api.Validator");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
	  }

  @Override
  public Class<?>[] provides() {
    return !VALIDATOR_HTTP_CLASS ? new Class<?>[]{} : new Class<?>[]{Validator.class};
  }

  @Override
  public void apply(BeanScopeBuilder builder) {
    if (!VALIDATOR_HTTP_CLASS) {
      return;
    }

    builder.provideDefault(null, Validator.class, () -> {
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
