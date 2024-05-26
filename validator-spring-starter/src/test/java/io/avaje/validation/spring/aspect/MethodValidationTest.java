package io.avaje.validation.spring.aspect;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import io.avaje.validation.spring.validator.AvajeValidatorAutoConfiguration;
import jakarta.inject.Inject;

@SpringBootTest(
    classes = {
      AvajeValidatorAutoConfiguration.class,
      MethodValidationAutoConfiguration.class,
      SpringAOPMethodValidator.class,
      TestParamProvider.class,
      ValidMethodClass.class,
      Validator.class
    })
class MethodValidationTest {

  @Inject private ValidMethodClass test;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @Test
  void test() {
    assertThatNoException().isThrownBy(() -> test.test(List.of(""), 1, null));
  }

  @Test
  void invalid() {
    assertThatThrownBy(() -> test.test(null, 0, null))
        .isInstanceOf(ConstraintViolationException.class);
  }
}
