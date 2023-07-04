package io.avaje.validation.inject.spi;

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

          return Validator.builder().build();
        });
  }
}
