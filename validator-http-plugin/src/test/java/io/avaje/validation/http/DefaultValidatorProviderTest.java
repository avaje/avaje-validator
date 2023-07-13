package io.avaje.validation.http;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.avaje.http.api.ValidationException;
import io.avaje.http.api.Validator;
import io.avaje.inject.BeanScope;
import io.avaje.inject.spi.Builder;
import io.avaje.inject.spi.Module;

class DefaultValidatorProviderTest {
  Module mod =
      new Module() {

        @Override
        public Class<?>[] classes() {
          return new Class<?>[] {};
        }

        @Override
        public void build(Builder builder) {}
      };
  private Validator validator;

  @BeforeAll
  static void setLocale() {
    System.setProperty("validation.locale.default", "en-us");
  }

  @BeforeEach
  void before() {
    System.setProperty("validation.locale.default", "en-us");
    final var v =
        io.avaje.validation.Validator.builder()
            .add(CrewMate.class, CrewMateValidationAdapter::new)
            .build();
    this.validator =
        BeanScope.builder()
            .modules(mod)
            .bean(io.avaje.validation.Validator.class, v)
            .build()
            .get(Validator.class);
  }

  @Test
  void test() {
    assertAll(() -> validator.validate(new CrewMate("hmm"), "en-GB,en;q=0.9,en-US;q=0.8,de;q=0.7"));
  }

  @Test
  void test2() {
    assertThrows(
        ValidationException.class,
        () -> validator.validate(new CrewMate(""), "en-GB,en;q=0.9,en-US;q=0.8,de;q=0.7"));
  }
}
