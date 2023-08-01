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
  void testNull() {
    assertThat(negativeAdapter.validate(null, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(null, request)).isTrue();
  }

  @Test
  void testInfinity() {
    assertThat(negativeAdapter.validate(Float.POSITIVE_INFINITY, request)).isFalse();
    assertThat(negativeAdapter.validate(Double.POSITIVE_INFINITY, request)).isFalse();
    assertThat(negativeOrZeroAdapter.validate(Float.POSITIVE_INFINITY, request)).isFalse();
    assertThat(negativeOrZeroAdapter.validate(Double.POSITIVE_INFINITY, request)).isFalse();

    assertThat(negativeAdapter.validate(Float.NEGATIVE_INFINITY, request)).isTrue();
    assertThat(negativeAdapter.validate(Double.NEGATIVE_INFINITY, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(Float.NEGATIVE_INFINITY, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(Double.NEGATIVE_INFINITY, request)).isTrue();
  }

  @Test
  void testPositive() {
    assertThat(negativeString.validate("1", request)).isFalse();
    assertThat(negativeAdapter.validate(1, request)).isFalse();
    assertThat(negativeAdapter.validate(1f, request)).isFalse();
    assertThat(negativeAdapter.validate(1D, request)).isFalse();
    assertThat(negativeAdapter.validate(1L, request)).isFalse();
    assertThat(negativeAdapter.validate((short) 1, request)).isFalse();
    assertThat(negativeAdapter.validate((byte) 1, request)).isFalse();
    assertThat(negativeBI.validate(BigInteger.ONE, request)).isFalse();
    assertThat(negativeBD.validate(BigDecimal.ONE, request)).isFalse();

    assertThat(negativeOrZeroString.validate("1", request)).isFalse();
    assertThat(negativeOrZeroAdapter.validate(1, request)).isFalse();
    assertThat(negativeOrZeroAdapter.validate(1f, request)).isFalse();
    assertThat(negativeOrZeroAdapter.validate(1D, request)).isFalse();
    assertThat(negativeOrZeroAdapter.validate(1L, request)).isFalse();
    assertThat(negativeOrZeroAdapter.validate((short) 1, request)).isFalse();
    assertThat(negativeOrZeroAdapter.validate((byte) 1, request)).isFalse();
    assertThat(negativeOrZeroBI.validate(BigInteger.ONE, request)).isFalse();
    assertThat(negativeOrZeroBD.validate(BigDecimal.ONE, request)).isFalse();
  }

  @Test
  void testNegative() {
    assertThat(negativeString.validate("-1", request)).isTrue();
    assertThat(negativeAdapter.validate(-1, request)).isTrue();
    assertThat(negativeAdapter.validate(-1f, request)).isTrue();
    assertThat(negativeAdapter.validate(-1D, request)).isTrue();
    assertThat(negativeAdapter.validate(-1L, request)).isTrue();
    assertThat(negativeAdapter.validate((short) -1, request)).isTrue();
    assertThat(negativeAdapter.validate((byte) -1, request)).isTrue();
    assertThat(negativeBI.validate(BigInteger.valueOf(-1), request)).isTrue();
    assertThat(negativeBD.validate(BigDecimal.valueOf(-1), request)).isTrue();

    assertThat(negativeOrZeroString.validate("-1", request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(-1, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(-1f, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(-1D, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(-1L, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate((short) -1, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate((byte) -1, request)).isTrue();
    assertThat(negativeOrZeroBI.validate(BigInteger.valueOf(-1), request)).isTrue();
    assertThat(negativeOrZeroBD.validate(BigDecimal.valueOf(-1), request)).isTrue();
  }

  @Test
  void testZero() {
    assertThat(negativeString.validate("0", request)).isFalse();
    assertThat(negativeAdapter.validate(0, request)).isFalse();
    assertThat(negativeAdapter.validate(0f, request)).isFalse();
    assertThat(negativeAdapter.validate(0D, request)).isFalse();
    assertThat(negativeAdapter.validate(0L, request)).isFalse();
    assertThat(negativeAdapter.validate((short) 0, request)).isFalse();
    assertThat(negativeAdapter.validate((byte) 0, request)).isFalse();
    assertThat(negativeBI.validate(BigInteger.ZERO, request)).isFalse();
    assertThat(negativeBD.validate(BigDecimal.ZERO, request)).isFalse();

    assertThat(negativeOrZeroString.validate("0", request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(0, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(0f, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(0D, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate(0L, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate((short) 0, request)).isTrue();
    assertThat(negativeOrZeroAdapter.validate((byte) 0, request)).isTrue();
    assertThat(negativeOrZeroBI.validate(BigInteger.ZERO, request)).isTrue();
    assertThat(negativeOrZeroBD.validate(BigDecimal.ZERO, request)).isTrue();
  }
}
