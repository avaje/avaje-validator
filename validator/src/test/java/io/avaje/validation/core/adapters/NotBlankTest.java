package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.BasicTest;

class NotBlankTest extends BasicTest {

  static final ValidationContext ctx = (ValidationContext) validator;

  @interface NotBlank {}

  ValidationAdapter<Object> notBlankAdapter =
      ctx.adapter(NotBlank.class, Map.of("message", "blank?"));

  ValidationAdapter<Object> notBlankMaxAdapter =
    ctx.adapter(NotBlank.class, Map.of("message", "blank?", "max", 3));

  @Test
  void continueOnInvalid_expect_false() {
    assertThat(notBlankAdapter.validate(null, request, "foo")).isFalse();
    assertThat(notBlankAdapter.validate("", request, "foo")).isFalse();

    assertThat(notBlankMaxAdapter.validate(null, request, "foo")).isFalse();
    assertThat(notBlankMaxAdapter.validate("", request, "foo")).isFalse();
    assertThat(notBlankMaxAdapter.validate("01234", request, "foo")).isFalse();
  }

  @Test
  void testNull() {
    assertThat(notBlankAdapter.validate(null, request)).isFalse();
  }

  @Test
  void testNotBlank() {
    assertThat(notBlankAdapter.validate("something", request)).isTrue();
  }

  @Test
  void testBlank() {
    assertThat(notBlankAdapter.validate("", request)).isFalse();
    assertThat(notBlankAdapter.validate("                    ", request)).isFalse();
  }
}
