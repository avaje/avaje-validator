package io.avaje.validation.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class ValidatorTest {

  @Test
  void testSuccess() {

    final var result =
        Validator.builder()
            .add(Pojo.class, AuthProvider$PojoAdapter::new)
            .build()
            .validate(new Pojo());
    assertThat(result).isNotEmpty();
  }

  @Test
  void testFail() {
    final var pojo = new Pojo();
    pojo.bool = true;
    final var result =
        Validator.builder().add(Pojo.class, AuthProvider$PojoAdapter::new).build().validate(pojo);

    assertThat(result).isEmpty();
  }
}
