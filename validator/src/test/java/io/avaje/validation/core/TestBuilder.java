package io.avaje.validation.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class TestBuilder {

  @Test
  void defaultBuilderReturnSameInstance() {

    assertThat(Validator.builder().build()).isSameAs(Validator.builder().build());
  }

  @Test
  void changedBuilderNotSame() {

    assertThat(Validator.builder().failFast(true).build()).isNotSameAs(Validator.builder().build());
  }
}
