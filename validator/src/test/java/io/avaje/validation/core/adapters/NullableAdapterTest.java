package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Set;

import io.avaje.validation.groups.Default;
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
  void continueOnInvalid_expect_false() {
    assertThat(nulladapter.validate(0, request, "foo")).isFalse();
    assertThat(notNulladapter.validate(null, request, "foo")).isFalse();
  }

  @Test
  void andThenContinue_expect_false() {
    ValidationAdapter<Object> otherAdapter =
      ctx.adapter(SizeTest.Size.class, Map.of("message", "blank?", "min", 2, "max", 3));

    var combinedAndThen =
       ctx.<String>adapter(NotNull.class, Map.of("message", "myCustomNullMessage"))
      .andThen(otherAdapter);

    assertThat(combinedAndThen.validate(null, request, "foo")).isFalse();
  }

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

  @Test
  void defaultInstance() {
    var adapter0 = ctx.adapter(NotNull.class, Map.of("message", "{avaje.NotNull.message}"));
    var adapter1 = ctx.adapter(NotNull.class, Map.of("message", "{avaje.NotNull.message}"));
    var adapter2 = ctx.adapter(NotNull.class, Set.of(Default.class), "{avaje.NotNull.message}", Map.of("message", "{avaje.NotNull.message}"));
    assertThat(adapter1).isSameAs(adapter0).isSameAs(adapter2);

    // these are different instances
    var adapterDiff1 = ctx.adapter(NotNull.class, Map.of("message", "Other message"));
    var adapterDiff2 = ctx.adapter(NotNull.class, Set.of(NullableAdapterTest.class), "{avaje.NotNull.message}", Map.of("message", "{avaje.NotNull.message}"));
    assertThat(adapter0).isNotSameAs(adapterDiff1).isNotSameAs(adapterDiff2);
  }
}
