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
    //BUG?: Should it continue validation? Prevents further pattern validations
    assertThat(sizeAdapter.validate("a", request, "foo")).isFalse();
    assertThat(sizeAdapter.validate("abcde", request, "foo")).isFalse();
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
    assertThat(sizeAdapter.validate(null, request)).isTrue();
  }

  @Test
  void testWithinSize() {
    assertThat(sizeAdapter.validate(Map.of(1, 2,3,4), request)).isTrue();
    assertThat(sizeAdapter.validate(List.of(1, 2), request)).isTrue();
    assertThat(sizeAdapter.validate(Set.of(1, 2), request)).isTrue();
    assertThat(sizeAdapter.validate(new int[] {1, 2,}, request)).isTrue();
    assertThat(sizeAdapter.validate("12", request)).isTrue();
  }

  @Test
  void testSmallerSize() {
    assertThat(sizeAdapter.validate("1", request)).isFalse();
    // if greater than 0 continue validation
    assertThat(sizeAdapter.validate(Map.of(1, 2), request)).isTrue();
    assertThat(sizeAdapter.validate(List.of(1), request)).isTrue();
    assertThat(sizeAdapter.validate(Set.of(1), request)).isTrue();
    assertThat(sizeAdapter.validate(new int[] {1}, request)).isTrue();
  }

  @Test
  void test0Size() {
    assertThat(sizeAdapter.validate(Map.of(), request)).isFalse();
    assertThat(sizeAdapter.validate(List.of(), request)).isFalse();
    assertThat(sizeAdapter.validate(Set.of(), request)).isFalse();
    assertThat(sizeAdapter.validate(new int[] {}, request)).isFalse();
    assertThat(sizeAdapter.validate("", request)).isFalse();
  }

  @Test
  void testBiggerSize() {
    assertThat(sizeAdapter.validate("it's too big", request)).isFalse();
    // if greater than 0 continue validation
    assertThat(sizeAdapter.validate(Map.of(1, 2, 3, 4, 5, 6, 7, 8), request)).isTrue();
    assertThat(sizeAdapter.validate(List.of(1, 2, 3, 4), request)).isTrue();
    assertThat(sizeAdapter.validate(Set.of(1, 2, 3, 4), request)).isTrue();
    assertThat(sizeAdapter.validate(new int[] {1, 2, 3, 4}, request)).isTrue();
  }
}
