package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.BasicTest;

class SizeTest extends BasicTest {

  static final ValidationContext ctx = (ValidationContext) validator;

  @interface Size {}

  ValidationAdapter<Object> sizeAdapter =
      ctx.adapter(Size.class, Map.of("message", "blank?", "min", 2, "max", 3));

  @Test
  void continueOnInvalid_expect_false_when_string() {
    assertThat(sizeAdapter.validate("a", request, "foo")).isTrue();
    assertThat(sizeAdapter.validate("abcde", request, "foo")).isTrue();
  }

  @Test
  void continueOnInvalid_expect_true_when_collection() {
    assertThat(sizeAdapter.validate(List.of(1), request, "foo")).isTrue();
    assertThat(sizeAdapter.validate(List.of(1, 2, 3, 4), request, "foo")).isTrue();
  }

  @Test
  void continueOnInvalid_expect_false_when_collectionEmpty() {
    assertThat(sizeAdapter.validate(List.of(), request, "foo")).isFalse();
    assertThat(sizeAdapter.validate(Map.of(), request, "foo")).isFalse();
    assertThat(sizeAdapter.validate(new int[]{}, request, "foo")).isFalse();
  }

  @Test
  void testNull() {
    // null elements are considered valid.
    assertThat(isValid(sizeAdapter, null)).isTrue();
  }

  @Test
  void testWithinSize() {
    assertThat(isValid(sizeAdapter, Map.of(1, 2,3,4))).isTrue();
    assertThat(isValid(sizeAdapter, List.of(1, 2))).isTrue();
    assertThat(isValid(sizeAdapter, Set.of(1, 2))).isTrue();
    assertThat(isValid(sizeAdapter, new int[] {1, 2,})).isTrue();
    assertThat(isValid(sizeAdapter, "12")).isTrue();
  }

  @Test
  void testSmallerSize() {
    assertThat(isValid(sizeAdapter, "1")).isFalse();
    // if greater than 0 continue validation
    assertThat(isValid(sizeAdapter, Map.of(1, 2))).isFalse();
    assertThat(isValid(sizeAdapter, List.of(1))).isFalse();
    assertThat(isValid(sizeAdapter, Set.of(1))).isFalse();
    assertThat(isValid(sizeAdapter, new int[] {1})).isFalse();
  }

  @Test
  void test0Size() {
    assertThat(isValid(sizeAdapter, Map.of())).isFalse();
    assertThat(isValid(sizeAdapter, List.of())).isFalse();
    assertThat(isValid(sizeAdapter, Set.of())).isFalse();
    assertThat(isValid(sizeAdapter, new int[] {})).isFalse();
    assertThat(isValid(sizeAdapter, "")).isFalse();
  }

  @Test
  void testBiggerSize() {
    assertThat(isValid(sizeAdapter, "it's too big")).isFalse();
    // if greater than 0 continue validation
    assertThat(isValid(sizeAdapter, Map.of(1, 2, 3, 4, 5, 6, 7, 8))).isFalse();
    assertThat(isValid(sizeAdapter, List.of(1, 2, 3, 4))).isFalse();
    assertThat(isValid(sizeAdapter, Set.of(1, 2, 3, 4))).isFalse();
    assertThat(isValid(sizeAdapter, new int[] {1, 2, 3, 4})).isFalse();
  }
}
