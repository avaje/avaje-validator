package io.avaje.validation.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationContext;

public abstract class BasicTest {

  protected static final Validator validator =
      Validator.builder()
          .add(Address.class, AddressValidationAdapter::new)
          .add(Contact.class, ContactValidationAdapter::new)
          .addLocales(Locale.GERMAN)
          .temporalTolerance(Duration.ofSeconds(20))
          .build();

  protected static final ValidationContext ctx = (ValidationContext) validator;

  protected static final DRequest request =
      new DRequest((DValidator) validator, false, null, List.of());

  protected static final DRequest groupRequest =
      new DRequest((DValidator) validator, false, null, List.of(BasicTest.class));

  protected ConstraintViolation one(Object pojo, Locale locale, Class<?>... groups) {

    final var violations = new ArrayList<>(validator.validate(pojo, locale, groups));

    if (violations.isEmpty()) throw new IllegalStateException();

    assertThat(violations).hasSize(1);
    return violations.get(0);
  }
}
