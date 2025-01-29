package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.RegexFlag;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.BasicTest;

class PatternTest extends BasicTest {

  static final ValidationContext ctx = (ValidationContext) validator;

  @interface Pattern {}

  ValidationAdapter<Object> patternAdapter =
      ctx.adapter(
          Pattern.class,
          Map.of(
              "message",
              "peppermint patty",
              "regexp",
              "¯\\\\_\\(ツ\\)_/¯",
              "flags",
              List.of(RegexFlag.CANON_EQ)));

  @Test
  void continueOnInvalid_expect_false() {
    //BUG: Should continue validation - return true!!
    assertThat(patternAdapter.validate("NotValid", request, "foo")).isFalse();
  }

  @Test
  void testNull() {
    assertThat(patternAdapter.validate(null, request)).isTrue();
  }

  @Test
  void testValid() {
    assertThat(patternAdapter.validate("¯\\_(ツ)_/¯", request)).isTrue();
  }

  @Test
  void testBlank() {
    assertThat(isValid(patternAdapter,"")).isFalse();
    assertThat(isValid(patternAdapter,"                    ")).isFalse();
  }

  @Test
  void testInvalid() {
    assertThat(isValid(patternAdapter,"notAnEmail")).isFalse();
  }
}
