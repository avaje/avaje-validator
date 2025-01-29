package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.BasicTest;

class NotEmptyTest extends BasicTest {

  static final ValidationContext ctx = (ValidationContext) validator;

  @interface NotEmpty {}

  ValidationAdapter<Object> notEmptyAdapter =
      ctx.adapter(NotEmpty.class, Map.of("message", "this can empty. *chucks it*"));

  @Test
  void continueOnInvalid_expect_false() {
    assertThat(notEmptyAdapter.validate(null, request, "foo")).isFalse();
    assertThat(notEmptyAdapter.validate("", request, "foo")).isFalse();
    assertThat(notEmptyAdapter.validate(List.of(), request, "foo")).isFalse();
    assertThat(notEmptyAdapter.validate(Map.of(), request, "foo")).isFalse();
    assertThat(notEmptyAdapter.validate(new int[]{}, request, "foo")).isFalse();
  }

  @Test
  void testNull() {
    assertThat(isValid(notEmptyAdapter, null)).isFalse();
  }

  @Test
  void testNotEmpty() {
    assertThat(isValid(notEmptyAdapter, "something")).isTrue();
    // length of characters, not whitespace
    assertThat(isValid(notEmptyAdapter, "                    ")).isTrue();
    assertThat(isValid(notEmptyAdapter, Map.of(1, 2))).isTrue();
    assertThat(isValid(notEmptyAdapter, List.of(1))).isTrue();
    assertThat(isValid(notEmptyAdapter, Set.of(1))).isTrue();
    assertThat(isValid(notEmptyAdapter, new int[] {1})).isTrue();
  }

  @Test
  void testEmpty() {
    assertThat(isValid(notEmptyAdapter, Map.of())).isFalse();
    assertThat(isValid(notEmptyAdapter, List.of())).isFalse();
    assertThat(isValid(notEmptyAdapter, Set.of())).isFalse();
    assertThat(isValid(notEmptyAdapter, new int[] {})).isFalse();
    assertThat(isValid(notEmptyAdapter, "")).isFalse();
  }

  @Test
  void testArrays() {
    assertThat(isValid(notEmptyAdapter, new int[] {1})).isTrue();
    assertThat(isValid(notEmptyAdapter, new byte[] {1})).isTrue();
    assertThat(isValid(notEmptyAdapter, new boolean[] {true})).isTrue();
    assertThat(isValid(notEmptyAdapter, new char[] {'d'})).isTrue();
    assertThat(isValid(notEmptyAdapter, new float[] {1})).isTrue();
    assertThat(isValid(notEmptyAdapter, new short[] {1})).isTrue();
    assertThat(isValid(notEmptyAdapter, new double[] {1})).isTrue();
    assertThat(isValid(notEmptyAdapter, new long[] {1})).isTrue();
    assertThat(isValid(notEmptyAdapter, new String[] {""})).isTrue();
  }
}
