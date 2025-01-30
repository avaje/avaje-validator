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
  }

  @Test
  void continueOnInvalid_expect_true_when_maxExceeded() {
    assertThat(notBlankMaxAdapter.validate("01234", request, "foo")).isTrue();
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

  @Test
  void defaultInstance() {
    var adapter0 = ctx.adapter(NotBlank.class, Map.of("message", "{avaje.NotBlank.message}"));
    var adapter1 = ctx.adapter(NotBlank.class, Map.of("message", "{avaje.NotBlank.message}"));
    var adapter2 = ctx.adapter(NotBlank.class, Map.of("message", "{avaje.NotBlank.message}", "max", 0));
    assertThat(adapter1).isSameAs(adapter0).isSameAs(adapter2);

    // these are different instances
    var adapterDiff1 = ctx.adapter(NotBlank.class, Map.of("message", "Other message"));
    var adapterDiff2 = ctx.adapter(NotBlank.class, Map.of("message", "{avaje.NotBlank.message}", "max", 4));
    assertThat(adapter0).isNotSameAs(adapterDiff1);
    assertThat(adapter0).isNotSameAs(adapterDiff2);
    assertThat(adapterDiff1).isNotSameAs(adapterDiff2);
  }
}
