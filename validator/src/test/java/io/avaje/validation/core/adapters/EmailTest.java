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
    //BUG: This should really return true? - should continue validation !!
    assertThat(emailAdapter.validate("notAnEmail", request, "foo")).isFalse();
  }

  @Test
  void testNull() {
    assertThat(emailAdapter.validate(null, request)).isTrue();
  }

  @Test
  void testValid() {
    assertThat(emailAdapter.validate("someEmail@gmail.com", request)).isTrue();
  }

  @Test
  void testBlank() {
    assertThat(emailAdapter.validate("", request)).isTrue();
    assertThat(emailAdapter.validate("                    ", request)).isFalse();
  }

  @Test
  void testInvalid() {
    assertThat(emailAdapter.validate("notAnEmail", request)).isFalse();
  }
}
