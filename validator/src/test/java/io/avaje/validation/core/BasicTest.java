package io.avaje.validation.core;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BasicTest {

  protected static final Validator validator =
      Validator.builder()
          .add(Address.class, AddressValidationAdapter::new)
          .add(Contact.class, ContactValidationAdapter::new)
          .addLocales(Locale.GERMAN)
          .temporalTolerance(Duration.ofMillis(20000))
          .build();

  protected static final ValidationContext ctx = (ValidationContext) validator;

  protected static final DRequest request = new DRequest((DValidator) validator, null);

  protected ConstraintViolation one(Object pojo, Locale locale) {
    try {
      validator.validate(pojo, locale);
      throw new IllegalStateException("don't get here");
    } catch (final ConstraintViolationException e) {
      final var violations = new ArrayList<>(e.violations());
      assertThat(violations).hasSize(1);
      return violations.get(0);
    }
  }
}
