package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.BasicTest;

class AssertBooleanTest extends BasicTest {

  static final ValidationContext ctx = (ValidationContext) validator;

  @interface AssertTrue {}

  @interface AssertFalse {}

  ValidationAdapter<Object> trueAdapter =
      ctx.adapter(AssertTrue.class, Map.of("message", "This sentence"));
  ValidationAdapter<Object> falseAdapter =
      ctx.adapter(AssertFalse.class, Map.of("message", "is false"));

  @Test
  void testNull() {
    assertThat(isValid(trueAdapter, null)).isTrue();
    assertThat(isValid(falseAdapter, null)).isTrue();
  }

  @Test
  void testTrue() {
    assertThat(isValid(trueAdapter, true)).isTrue();
    assertThat(isValid(falseAdapter, true)).isFalse();
  }

  @Test
  void testFalse() {
    assertThat(isValid(trueAdapter, false)).isFalse();
    assertThat(isValid(falseAdapter, false)).isTrue();
  }
}
