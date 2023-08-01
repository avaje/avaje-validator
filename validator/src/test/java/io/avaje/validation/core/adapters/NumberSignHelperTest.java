package io.avaje.validation.core.adapters;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;

import static org.assertj.core.api.Assertions.assertThat;

class NumberSignHelperTest {

  @Test
  void signum() {
    assertThat(NumberSignHelper.signum("Long", -1L, OptionalInt.of(1))).isEqualTo(-1);
    assertThat(NumberSignHelper.signum("Long", 1L, OptionalInt.of(1))).isEqualTo(1);
    assertThat(NumberSignHelper.signum("Integer", -1, OptionalInt.of(1))).isEqualTo(-1);
    assertThat(NumberSignHelper.signum("Integer", 1, OptionalInt.of(1))).isEqualTo(1);
    assertThat(NumberSignHelper.signum("Float", -1F, OptionalInt.of(1))).isEqualTo(-1);
    assertThat(NumberSignHelper.signum("Float", 1F, OptionalInt.of(1))).isEqualTo(1);
    assertThat(NumberSignHelper.signum("Double", -0.1D, OptionalInt.of(1))).isEqualTo(-1);
    assertThat(NumberSignHelper.signum("Double", 0.1D, OptionalInt.of(1))).isEqualTo(1);
    assertThat(NumberSignHelper.signum("Short", (short)-1, OptionalInt.of(1))).isEqualTo(-1);
    assertThat(NumberSignHelper.signum("Short", (short)1, OptionalInt.of(1))).isEqualTo(1);
    assertThat(NumberSignHelper.signum("Byte", (byte)-1, OptionalInt.of(1))).isEqualTo(-1);
    assertThat(NumberSignHelper.signum("Byte", (byte)1, OptionalInt.of(1))).isEqualTo(1);
  }

  @Test
  void signum_BigDecimal() {
    assertThat(NumberSignHelper.signum("BigDecimal", BigDecimal.TEN, OptionalInt.of(1))).isEqualTo(1);
    assertThat(NumberSignHelper.signum("BigDecimal", BigDecimal.valueOf(-2), OptionalInt.of(1))).isEqualTo(-1);
  }

  @Test
  void signum_BigInteger() {
    assertThat(NumberSignHelper.signum("BigInteger", BigInteger.TEN, OptionalInt.of(1))).isEqualTo(1);
    assertThat(NumberSignHelper.signum("BigInteger", BigInteger.valueOf(-4), OptionalInt.of(1))).isEqualTo(-1);
  }

  @Test
  void signum_String() {
    assertThat(NumberSignHelper.signum("String", "5", OptionalInt.of(1))).isEqualTo(1);
    assertThat(NumberSignHelper.signum("String", "-2", OptionalInt.of(1))).isEqualTo(-1);
    assertThat(NumberSignHelper.signum("CharSequence", "5", OptionalInt.of(1))).isEqualTo(1);
    assertThat(NumberSignHelper.signum("CharSequence", "-2", OptionalInt.of(1))).isEqualTo(-1);
  }

  @Test
  void signum_Number() {
    assertThat(NumberSignHelper.signum("Number", new FooNum(5.8), OptionalInt.of(1))).isEqualTo(1);
    assertThat(NumberSignHelper.signum("Number", new FooNum(-5.8), OptionalInt.of(1))).isEqualTo(-1);
  }


  static final class FooNum extends Number {

    final double doubleValue;

    FooNum(double doubleValue) {
      this.doubleValue = doubleValue;
    }

    @Override
    public int intValue() {
      return 0;
    }

    @Override
    public long longValue() {
      return 0;
    }

    @Override
    public float floatValue() {
      return 0;
    }

    @Override
    public double doubleValue() {
      return doubleValue;
    }
  }
}
