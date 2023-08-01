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
  void testNull() {
    assertThat(positiveAdapter.validate(null, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(null, request)).isTrue();
  }

  @Test
  void testInfinity() {
    assertThat(positiveAdapter.validate(Float.POSITIVE_INFINITY, request)).isTrue();
    assertThat(positiveAdapter.validate(Double.POSITIVE_INFINITY, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(Float.POSITIVE_INFINITY, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(Double.POSITIVE_INFINITY, request)).isTrue();

    assertThat(positiveAdapter.validate(Float.NEGATIVE_INFINITY, request)).isFalse();
    assertThat(positiveAdapter.validate(Double.NEGATIVE_INFINITY, request)).isFalse();
    assertThat(positiveOrZeroAdapter.validate(Float.NEGATIVE_INFINITY, request)).isFalse();
    assertThat(positiveOrZeroAdapter.validate(Double.NEGATIVE_INFINITY, request)).isFalse();
  }

  @Test
  void testPositive() {
    assertThat(positiveString.validate("1", request)).isTrue();
    assertThat(positiveAdapter.validate(1, request)).isTrue();
    assertThat(positiveAdapter.validate(1f, request)).isTrue();
    assertThat(positiveAdapter.validate(1D, request)).isTrue();
    assertThat(positiveAdapter.validate(1L, request)).isTrue();
    assertThat(positiveAdapter.validate((short) 1, request)).isTrue();
    assertThat(positiveAdapter.validate((byte) 1, request)).isTrue();
    assertThat(positiveAdapter.validate(BigInteger.ONE, request)).isTrue();
    assertThat(positiveAdapter.validate(BigDecimal.ONE, request)).isTrue();

    assertThat(positiveOrZeroString.validate("1", request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(1, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(1f, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(1D, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(1L, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate((short) 1, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate((byte) 1, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(BigInteger.ONE, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(BigDecimal.ONE, request)).isTrue();
  }

  @Test
  void testNegative() {

    assertThat(positiveString.validate("-1", request)).isFalse();
    assertThat(positiveAdapter.validate(-1, request)).isFalse();
    assertThat(positiveAdapter.validate(-1f, request)).isFalse();
    assertThat(positiveAdapter.validate(-1D, request)).isFalse();
    assertThat(positiveAdapter.validate(-1L, request)).isFalse();
    assertThat(positiveAdapter.validate((short) -1, request)).isFalse();
    assertThat(positiveAdapter.validate((byte) -1, request)).isFalse();
    assertThat(positiveAdapter.validate(BigInteger.valueOf(-1), request)).isFalse();
    assertThat(positiveAdapter.validate(BigDecimal.valueOf(-1), request)).isFalse();

    assertThat(positiveOrZeroString.validate("-1", request)).isFalse();
    assertThat(positiveOrZeroAdapter.validate(-1, request)).isFalse();
    assertThat(positiveOrZeroAdapter.validate(-1f, request)).isFalse();
    assertThat(positiveOrZeroAdapter.validate(-1D, request)).isFalse();
    assertThat(positiveOrZeroAdapter.validate(-1L, request)).isFalse();
    assertThat(positiveOrZeroAdapter.validate((short) -1, request)).isFalse();
    assertThat(positiveOrZeroAdapter.validate((byte) -1, request)).isFalse();
    assertThat(positiveOrZeroAdapter.validate(BigInteger.valueOf(-1), request)).isFalse();
    assertThat(positiveOrZeroAdapter.validate(BigDecimal.valueOf(-1), request)).isFalse();
  }

  @Test
  void testZero() {
    assertThat(positiveString.validate("0", request)).isFalse();
    assertThat(positiveAdapter.validate(0, request)).isFalse();
    assertThat(positiveAdapter.validate(0f, request)).isFalse();
    assertThat(positiveAdapter.validate(0D, request)).isFalse();
    assertThat(positiveAdapter.validate(0L, request)).isFalse();
    assertThat(positiveAdapter.validate((short) 0, request)).isFalse();
    assertThat(positiveAdapter.validate((byte) 0, request)).isFalse();
    assertThat(positiveAdapter.validate(BigInteger.ZERO, request)).isFalse();
    assertThat(positiveAdapter.validate(BigDecimal.ZERO, request)).isFalse();

    assertThat(positiveOrZeroString.validate("0", request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(0, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(0f, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(0D, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(0L, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate((short) 0, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate((byte) 0, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(BigInteger.ZERO, request)).isTrue();
    assertThat(positiveOrZeroAdapter.validate(BigDecimal.ZERO, request)).isTrue();
  }
}
