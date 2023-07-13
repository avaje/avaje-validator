package io.avaje.validation.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import io.avaje.http.api.Validator;
import io.avaje.inject.BeanScopeBuilder;

/** Plugin for avaje inject that provides a default Http Validator instance. */
public final class DefaultValidatorProvider implements io.avaje.inject.spi.Plugin {

  @Override
  public Class<?>[] provides() {
    return new Class<?>[] {Validator.class};
  }

  @Override
  public void apply(BeanScopeBuilder builder) {

    final var props = builder.propertyPlugin();

    final var locales = new ArrayList<Locale>();

    props.get("validation.locale.default").map(Locale::forLanguageTag).ifPresent(locales::add);

    props.get("validation.locale.addedLocales").stream()
        .flatMap(s -> Arrays.stream(s.split(",")))
        .map(Locale::forLanguageTag)
        .forEach(locales::add);

    final var beanValidator = new BeanValidator(locales);

    builder.addPostConstructConsumerHook(beanValidator::setValidator);

    builder.provideDefault(null, Validator.class, () -> beanValidator);
  }
}
