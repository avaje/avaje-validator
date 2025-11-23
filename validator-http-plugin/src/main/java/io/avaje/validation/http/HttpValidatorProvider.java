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

  private static final boolean WIRE_VALIDATOR = avajeHttpOnClasspath();

  private static boolean avajeHttpOnClasspath() {

    var modules = ModuleLayer.boot();
    return modules
        .findModule("io.avaje.validation.http")
        .map(m -> modules.findModule("io.avaje.http.api").isPresent())
        .orElseGet(
            () -> {
              try {
                return Validator.class != null;
              } catch (NoClassDefFoundError e) {
                return false;
              }
            });
  }

  @Override
  public Class<?>[] provides() {
    return WIRE_VALIDATOR ? new Class<?>[] {Validator.class} : new Class<?>[] {};
  }

  @Override
  public void apply(BeanScopeBuilder builder) {
    if (!WIRE_VALIDATOR) {
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
