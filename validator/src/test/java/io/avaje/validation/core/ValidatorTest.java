package io.avaje.validation.core;

import java.time.LocalDate;
import java.util.Set;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

import static org.assertj.core.api.Assertions.*;

class ValidatorTest {

  private final Validator validator =
      Validator.builder().add(Pojo.class, AuthProvider$PojoAdapter::new).build();

  @Test
  void testAllFail() {
    try {
      validator.validate(new Pojo(false, null, LocalDate.now().plusDays(3)));
      fail("");
    } catch (ConstraintViolationException e) {
      Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(3);
    }
  }

  @Test
  void testStrDate() {
    try {
      validator.validate(new Pojo(true, "  ", LocalDate.now().plusDays(3)));
    } catch (ConstraintViolationException e) {
      Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(2);
    }
  }

  @Test
  void testStrOnly() {
    try {
    validator.validate(new Pojo(true, "  ", LocalDate.now().minusDays(3)));
    } catch (ConstraintViolationException e) {
      Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(1);
    }
  }

  @Test
  void testSuccess() {
    validator.validate(new Pojo(true, " success ", LocalDate.now().minusDays(3)));
  }
}
