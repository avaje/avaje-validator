package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.core.BasicTest;

class NegativeTest extends BasicTest {

  @interface Negative {}

  @interface NegativeOrZero {}

  ValidationAdapter<Object> negativeAdapter =
    ctx.adapter(Negative.class, Map.of("message", "elim-", "_type", "Number"));
  ValidationAdapter<Object> negativeOrZeroAdapter =
    ctx.adapter(NegativeOrZero.class, Map.of("message", "-anate the negative", "_type", "Number"));

  ValidationAdapter<Object> negativeString =
    ctx.adapter(Negative.class, Map.of("message", "elim-", "_type", "String"));
  ValidationAdapter<Object> negativeOrZeroString =
    ctx.adapter(NegativeOrZero.class, Map.of("message", "-anate the negative", "_type", "String"));

  ValidationAdapter<Object> negativeBD =
    ctx.adapter(Negative.class, Map.of("message", "elim-", "_type", "BigDecimal"));
  ValidationAdapter<Object> negativeOrZeroBD =
    ctx.adapter(NegativeOrZero.class, Map.of("message", "-anate the negative", "_type", "BigDecimal"));
  ValidationAdapter<Object> negativeBI =
    ctx.adapter(Negative.class, Map.of("message", "elim-", "_type", "BigInteger"));
  ValidationAdapter<Object> negativeOrZeroBI =
    ctx.adapter(NegativeOrZero.class, Map.of("message", "-anate the negative", "_type", "BigInteger"));

  @Test
  void continueOnInvalid_expect_false() {
    assertThat(negativeAdapter.validate(1, request, "foo")).isTrue();
  }

  @Test
  void testNull() {
    assertThat(isValid(negativeAdapter, null)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, null)).isTrue();
  }

  @Test
  void testInfinity() {
    assertThat(isValid(negativeAdapter, Float.POSITIVE_INFINITY)).isFalse();
    assertThat(isValid(negativeAdapter, Double.POSITIVE_INFINITY)).isFalse();
    assertThat(isValid(negativeOrZeroAdapter, Float.POSITIVE_INFINITY)).isFalse();
    assertThat(isValid(negativeOrZeroAdapter, Double.POSITIVE_INFINITY)).isFalse();

    assertThat(isValid(negativeAdapter, Float.NEGATIVE_INFINITY)).isTrue();
    assertThat(isValid(negativeAdapter, Double.NEGATIVE_INFINITY)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, Float.NEGATIVE_INFINITY)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, Double.NEGATIVE_INFINITY)).isTrue();
  }

  @Test
  void testPositive() {
    assertThat(isValid(negativeString, "1")).isFalse();
    assertThat(isValid(negativeAdapter, 1)).isFalse();
    assertThat(isValid(negativeAdapter, 1f)).isFalse();
    assertThat(isValid(negativeAdapter, 1D)).isFalse();
    assertThat(isValid(negativeAdapter, 1L)).isFalse();
    assertThat(isValid(negativeAdapter, (short) 1)).isFalse();
    assertThat(isValid(negativeAdapter, (byte) 1)).isFalse();
    assertThat(isValid(negativeBI, BigInteger.ONE)).isFalse();
    assertThat(isValid(negativeBD, BigDecimal.ONE)).isFalse();

    assertThat(isValid(negativeOrZeroString, "1")).isFalse();
    assertThat(isValid(negativeOrZeroAdapter, 1)).isFalse();
    assertThat(isValid(negativeOrZeroAdapter, 1f)).isFalse();
    assertThat(isValid(negativeOrZeroAdapter, 1D)).isFalse();
    assertThat(isValid(negativeOrZeroAdapter, 1L)).isFalse();
    assertThat(isValid(negativeOrZeroAdapter, (short) 1)).isFalse();
    assertThat(isValid(negativeOrZeroAdapter, (byte) 1)).isFalse();
    assertThat(isValid(negativeOrZeroBI, BigInteger.ONE)).isFalse();
    assertThat(isValid(negativeOrZeroBD, BigDecimal.ONE)).isFalse();
  }

  @Test
  void testNegative() {
    assertThat(isValid(negativeString, "-1")).isTrue();
    assertThat(isValid(negativeAdapter, -1)).isTrue();
    assertThat(isValid(negativeAdapter, -1f)).isTrue();
    assertThat(isValid(negativeAdapter, -1D)).isTrue();
    assertThat(isValid(negativeAdapter, -1L)).isTrue();
    assertThat(isValid(negativeAdapter, (short) -1)).isTrue();
    assertThat(isValid(negativeAdapter, (byte) -1)).isTrue();
    assertThat(isValid(negativeBI, BigInteger.valueOf(-1))).isTrue();
    assertThat(isValid(negativeBD, BigDecimal.valueOf(-1))).isTrue();

    assertThat(isValid(negativeOrZeroString, "-1")).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, -1)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, -1f)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, -1D)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, -1L)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, (short) -1)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, (byte) -1)).isTrue();
    assertThat(isValid(negativeOrZeroBI, BigInteger.valueOf(-1))).isTrue();
    assertThat(isValid(negativeOrZeroBD, BigDecimal.valueOf(-1))).isTrue();
  }

  @Test
  void testZero() {
    assertThat(isValid(negativeString, "0")).isFalse();
    assertThat(isValid(negativeAdapter, 0)).isFalse();
    assertThat(isValid(negativeAdapter, 0f)).isFalse();
    assertThat(isValid(negativeAdapter, 0D)).isFalse();
    assertThat(isValid(negativeAdapter, 0L)).isFalse();
    assertThat(isValid(negativeAdapter, (short) 0)).isFalse();
    assertThat(isValid(negativeAdapter, (byte) 0)).isFalse();
    assertThat(isValid(negativeBI, BigInteger.ZERO)).isFalse();
    assertThat(isValid(negativeBD, BigDecimal.ZERO)).isFalse();

    assertThat(isValid(negativeOrZeroString, "0")).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, 0)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, 0f)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, 0D)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, 0L)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, (short) 0)).isTrue();
    assertThat(isValid(negativeOrZeroAdapter, (byte) 0)).isTrue();
    assertThat(isValid(negativeOrZeroBI, BigInteger.ZERO)).isTrue();
    assertThat(isValid(negativeOrZeroBD, BigDecimal.ZERO)).isTrue();
  }
}
