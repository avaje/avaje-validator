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

  @Test
  void testNull() {
    assertThat(isValid(notBlankAdapter, null)).isFalse();
  }

  @Test
  void testNotBlank() {
    assertThat(isValid(notBlankAdapter, "something")).isTrue();
  }

  @Test
  void testBlank() {
    assertThat(isValid(notBlankAdapter, "")).isFalse();
    assertThat(isValid(notBlankAdapter, "                    ")).isFalse();
  }
}
