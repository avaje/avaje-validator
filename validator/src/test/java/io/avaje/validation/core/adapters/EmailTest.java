package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.BasicTest;

class EmailTest extends BasicTest {

  static final ValidationContext ctx = (ValidationContext) validator;

  @interface Email {}

  ValidationAdapter<Object> emailAdapter = ctx.adapter(Email.class, Map.of("message", "email"));

  @Test
  void continueOnInvalid_expect_false() {
    assertThat(emailAdapter.validate("notAnEmail", request, "foo")).isTrue();
  }

  @Test
  void testNull() {
    assertThat(isValid(emailAdapter, null)).isTrue();
  }

  @Test
  void testValid() {
    assertThat(isValid(emailAdapter, "someEmail@gmail.com")).isTrue();
  }

  @Test
  void testBlank() {
    assertThat(isValid(emailAdapter, "")).isTrue();
    assertThat(isValid(emailAdapter, "                    ")).isFalse();
  }

  @Test
  void testInvalid() {
    assertThat(isValid(emailAdapter, "notAnEmail")).isFalse();
  }
}
