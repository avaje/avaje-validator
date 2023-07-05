package io.avaje.validation.inject.spi;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Locale;

import io.avaje.inject.BeanScopeBuilder;
import io.avaje.validation.Validator;

/** Plugin for avaje inject that provides a default Jsonb instance. */
public final class DefaultValidatorProvider implements io.avaje.inject.spi.Plugin {

  @Override
  public Class<?>[] provides() {
    return new Class<?>[] {Validator.class};
  }

  @Override
  public void apply(BeanScopeBuilder builder) {

    builder.provideDefault(
        null,
        Validator.class,
        () -> {
          final var props = builder.propertyPlugin();
          final var validator =
              Validator.builder().failFast(props.equalTo("validation.failFast", "true"));

          props
              .get("validation.resourcebundle.names")
              .map(s -> s.split(","))
              .ifPresent(validator::addResourceBundles);

          props
              .get("validation.locale.default")
              .map(Locale::forLanguageTag)
              .ifPresent(validator::setDefaultLocale);

          props.get("validation.locale.addedLocales").stream()
              .flatMap(s -> Arrays.stream(s.split(",")))
              .map(Locale::forLanguageTag)
              .forEach(validator::addLocales);

          props
              .get("validation.temporal.value")
              .map(Long::valueOf)
              .ifPresent(
                  l -> {
                    final var unit =
                        props
                            .get("validation.temporal.chronoUnit")
                            .map(ChronoUnit::valueOf)
                            .orElse(ChronoUnit.MILLIS);
                    validator.temporalTolerance(Duration.of(l, unit));
                  });
          return validator.build();
        });
  }
}
