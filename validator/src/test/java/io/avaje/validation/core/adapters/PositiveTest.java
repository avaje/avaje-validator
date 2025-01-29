package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.core.BasicTest;

class PositiveTest extends BasicTest {

  @interface Positive {}

  @interface PositiveOrZero {}

  ValidationAdapter<Object> positiveAdapter =
      ctx.adapter(Positive.class, Map.of("message", "you gotta accent-", "_type", "Number"));
  ValidationAdapter<Object> positiveOrZeroAdapter =
      ctx.adapter(PositiveOrZero.class, Map.of("message", "-tuate the positive", "_type", "Number"));

  ValidationAdapter<Object> positiveString =
    ctx.adapter(Positive.class, Map.of("message", "you gotta accent-", "_type", "String"));
  ValidationAdapter<Object> positiveOrZeroString =
    ctx.adapter(PositiveOrZero.class, Map.of("message", "-tuate the positive", "_type", "String"));

  @Test
  void continueOnInvalid_expect_false() {
    //BUG: Should continue validation? - return true!!
    assertThat(positiveAdapter.validate(-1, request, "foo")).isFalse();
  }

  @Test
  void testNull() {
    assertThat(isValid(positiveAdapter, null)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, null)).isTrue();
  }

  @Test
  void testInfinity() {
    assertThat(isValid(positiveAdapter, Float.POSITIVE_INFINITY)).isTrue();
    assertThat(isValid(positiveAdapter, Double.POSITIVE_INFINITY)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, Float.POSITIVE_INFINITY)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, Double.POSITIVE_INFINITY)).isTrue();

    assertThat(isValid(positiveAdapter, Float.NEGATIVE_INFINITY)).isFalse();
    assertThat(isValid(positiveAdapter, Double.NEGATIVE_INFINITY)).isFalse();
    assertThat(isValid(positiveOrZeroAdapter, Float.NEGATIVE_INFINITY)).isFalse();
    assertThat(isValid(positiveOrZeroAdapter, Double.NEGATIVE_INFINITY)).isFalse();
  }

  @Test
  void testPositive() {
    assertThat(isValid(positiveString, "1")).isTrue();
    assertThat(isValid(positiveAdapter, 1)).isTrue();
    assertThat(isValid(positiveAdapter, 1f)).isTrue();
    assertThat(isValid(positiveAdapter, 1D)).isTrue();
    assertThat(isValid(positiveAdapter, 1L)).isTrue();
    assertThat(isValid(positiveAdapter, (short) 1)).isTrue();
    assertThat(isValid(positiveAdapter, (byte) 1)).isTrue();
    assertThat(isValid(positiveAdapter, BigInteger.ONE)).isTrue();
    assertThat(isValid(positiveAdapter, BigDecimal.ONE)).isTrue();

    assertThat(isValid(positiveOrZeroString, "1")).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, 1)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, 1f)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, 1D)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, 1L)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, (short) 1)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, (byte) 1)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, BigInteger.ONE)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, BigDecimal.ONE)).isTrue();
  }

  @Test
  void testNegative() {
    assertThat(isValid(positiveString, "-1")).isFalse();
    assertThat(isValid(positiveAdapter, -1)).isFalse();
    assertThat(isValid(positiveAdapter, -1f)).isFalse();
    assertThat(isValid(positiveAdapter, -1D)).isFalse();
    assertThat(isValid(positiveAdapter, -1L)).isFalse();
    assertThat(isValid(positiveAdapter, (short) -1)).isFalse();
    assertThat(isValid(positiveAdapter, (byte) -1)).isFalse();
    assertThat(isValid(positiveAdapter, BigInteger.valueOf(-1))).isFalse();
    assertThat(isValid(positiveAdapter, BigDecimal.valueOf(-1))).isFalse();

    assertThat(isValid(positiveOrZeroString, "-1")).isFalse();
    assertThat(isValid(positiveOrZeroAdapter, -1)).isFalse();
    assertThat(isValid(positiveOrZeroAdapter, -1f)).isFalse();
    assertThat(isValid(positiveOrZeroAdapter, -1D)).isFalse();
    assertThat(isValid(positiveOrZeroAdapter, -1L)).isFalse();
    assertThat(isValid(positiveOrZeroAdapter, (short) -1)).isFalse();
    assertThat(isValid(positiveOrZeroAdapter, (byte) -1)).isFalse();
    assertThat(isValid(positiveOrZeroAdapter, BigInteger.valueOf(-1))).isFalse();
    assertThat(isValid(positiveOrZeroAdapter, BigDecimal.valueOf(-1))).isFalse();
  }

  @Test
  void testZero() {
    assertThat(isValid(positiveString, "0")).isFalse();
    assertThat(isValid(positiveAdapter, 0)).isFalse();
    assertThat(isValid(positiveAdapter, 0f)).isFalse();
    assertThat(isValid(positiveAdapter, 0D)).isFalse();
    assertThat(isValid(positiveAdapter, 0L)).isFalse();
    assertThat(isValid(positiveAdapter, (short) 0)).isFalse();
    assertThat(isValid(positiveAdapter, (byte) 0)).isFalse();
    assertThat(isValid(positiveAdapter, BigInteger.ZERO)).isFalse();
    assertThat(isValid(positiveAdapter, BigDecimal.ZERO)).isFalse();

    assertThat(isValid(positiveOrZeroString, "0")).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, 0)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, 0f)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, 0D)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, 0L)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, (short) 0)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, (byte) 0)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, BigInteger.ZERO)).isTrue();
    assertThat(isValid(positiveOrZeroAdapter, BigDecimal.ZERO)).isTrue();
  }
}
