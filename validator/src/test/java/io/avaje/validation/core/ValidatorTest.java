package io.avaje.validation.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class ValidatorTest {

  private final Validator validator =
      Validator.builder().add(Pojo.class, AuthProvider$PojoAdapter::new).build();

  @Test
  void testAllFail() {

    final var result = validator.validate(new Pojo(false, null, LocalDate.now().plusDays(3)));
    assertThat(result).hasSize(3);
  }

  @Test
  void testStrDate() {

    final var result = validator.validate(new Pojo(true, "  ", LocalDate.now().plusDays(3)));
    assertThat(result).hasSize(2);
  }

  @Test
  void testStrOnly() {

    final var result = validator.validate(new Pojo(true, "  ", LocalDate.now().minusDays(3)));
    assertThat(result).hasSize(1);
  }

  @Test
  void testSuccess() {
    final var result =
        validator.validate(new Pojo(true, " success ", LocalDate.now().minusDays(3)));

    assertThat(result).isEmpty();
  }
}
