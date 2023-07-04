package io.avaje.validation.inject.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

class TestMethodValidation {

  private static MethodTest proxy;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {

    final var val =
        new ParamValidator(Validator.builder().build(), List.of(new TestParamProvider()));
    proxy = new MethodTest$Proxy(val);
  }

  @Test
  void test() {
    assertThatNoException().isThrownBy(() -> proxy.test(List.of(""), 1, null));
  }

  @Test
  void invalid() {
    assertThatThrownBy(() -> proxy.test(null, 0, null))
        .isInstanceOf(ConstraintViolationException.class);
  }
}
