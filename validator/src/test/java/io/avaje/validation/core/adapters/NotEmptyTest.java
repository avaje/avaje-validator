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
  void testNull() {
    assertThat(notEmptyAdapter.validate(null, request)).isFalse();
  }

  @Test
  void testNotEmpty() {
    assertThat(notEmptyAdapter.validate("something", request)).isTrue();
    // length of characters, not whitespace
    assertThat(notEmptyAdapter.validate("                    ", request)).isTrue();
    assertThat(notEmptyAdapter.validate(Map.of(1, 2), request)).isTrue();
    assertThat(notEmptyAdapter.validate(List.of(1), request)).isTrue();
    assertThat(notEmptyAdapter.validate(Set.of(1), request)).isTrue();
    assertThat(notEmptyAdapter.validate(new int[] {1}, request)).isTrue();
  }

  @Test
  void testEmpty() {
    assertThat(notEmptyAdapter.validate(Map.of(), request)).isFalse();
    assertThat(notEmptyAdapter.validate(List.of(), request)).isFalse();
    assertThat(notEmptyAdapter.validate(Set.of(), request)).isFalse();
    assertThat(notEmptyAdapter.validate(new int[] {}, request)).isFalse();
    assertThat(notEmptyAdapter.validate("", request)).isFalse();
  }
}
