package io.avaje.validation.core;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

abstract class BasicTest {

  protected final Validator validator =
    Validator.builder()
      .add(Address.class, AddressValidationAdapter::new)
      .add(Contact.class, ContactValidationAdapter::new)
      .addLocals(Locale.GERMAN)
      .build();

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
