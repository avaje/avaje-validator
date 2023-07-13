package io.avaje.validation.generator.models.valid;

import java.time.Clock;
import java.time.ZoneId;

import io.avaje.validation.Validator.Builder;
import io.avaje.validation.spi.BuilderCustomizer;
import io.avaje.validation.spi.ValidatorCustomizer;

@BuilderCustomizer
public final class ClockCustomizer implements ValidatorCustomizer {

  @Override
  public void customize(Builder builder) {
    builder.clockProvider(() -> Clock.system(ZoneId.of("Anor Londo")));
  }
}
