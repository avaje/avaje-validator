package example.avaje.customizer;

import java.time.Clock;

import io.avaje.validation.Validator.Builder;
import io.avaje.validation.spi.BuilderCustomizer;
import io.avaje.validation.spi.ValidatorCustomizer;

@BuilderCustomizer
public final class ClockCustomizer implements ValidatorCustomizer {

  @Override
  public void customize(Builder builder) {
    builder.clockProvider(Clock::systemDefaultZone);
  }
}
