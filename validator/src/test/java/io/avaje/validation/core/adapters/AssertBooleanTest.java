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
    assertThat(trueAdapter.validate(null, request)).isTrue();
    assertThat(falseAdapter.validate(null, request)).isTrue();
  }

  @Test
  void testTrue() {
    assertThat(trueAdapter.validate(true, request)).isTrue();
    assertThat(falseAdapter.validate(true, request)).isFalse();
  }

  @Test
  void testFalse() {
    assertThat(trueAdapter.validate(false, request)).isFalse();
    assertThat(falseAdapter.validate(false, request)).isTrue();
  }
}
