package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.core.BasicTest;

class NullableAdapterTest extends BasicTest {

  @interface Null {}

  @interface NotNull {}

  @interface NonNull {}

  ValidationAdapter<Object> nulladapter = ctx.adapter(Null.class, Map.of("message", "be null"));
  ValidationAdapter<Object> notNulladapter =
      ctx.adapter(NotNull.class, Map.of("message", "Not be null"));
  ValidationAdapter<Object> nonNulladapter =
      ctx.adapter(NonNull.class, Map.of("message", "Non be null"));

  @Test
  void testNull() {
    assertThat(isValid(nulladapter, null)).isTrue();
    assertThat(isValid(notNulladapter, null)).isFalse();
    assertThat(isValid(nonNulladapter, null)).isFalse();
  }

  @Test
  void testNotNull() {
    assertThat(isValid(nulladapter, 0)).isFalse();
    assertThat(isValid(notNulladapter, 0)).isTrue();
    assertThat(isValid(nonNulladapter, 0)).isTrue();
  }
}
