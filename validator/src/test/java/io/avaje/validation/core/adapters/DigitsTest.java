package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.core.BasicTest;

class DigitsTest extends BasicTest {

  @interface Digits {}

  ValidationAdapter<Object> digitAdapter =
      ctx.adapter(Digits.class, Map.of("message", "digimon", "integer", 5, "fraction", 5));

  @Test
  void testNull() {
    assertThat(digitAdapter.validate(null, request)).isTrue();
  }

  @Test
  void testValid() {
    assertThat(digitAdapter.validate(0, request)).isTrue();
    assertThat(digitAdapter.validate(0f, request)).isTrue();
    assertThat(digitAdapter.validate(0D, request)).isTrue();
    assertThat(digitAdapter.validate(0L, request)).isTrue();
    assertThat(digitAdapter.validate((short) 0, request)).isTrue();
    assertThat(digitAdapter.validate((byte) 0, request)).isTrue();
    assertThat(digitAdapter.validate(BigInteger.ZERO, request)).isTrue();
    assertThat(digitAdapter.validate(BigDecimal.ZERO, request)).isTrue();
  }

  @Test
  void testInValid() {
    assertThat(digitAdapter.validate(01234, request)).isTrue();
    assertThat(digitAdapter.validate(01234f, request)).isTrue();
    assertThat(digitAdapter.validate(01234D, request)).isTrue();
    assertThat(digitAdapter.validate(01234L, request)).isTrue();
    assertThat(digitAdapter.validate((short) 01234, request)).isTrue();
    assertThat(digitAdapter.validate((byte) 01234, request)).isTrue();
    assertThat(digitAdapter.validate(BigInteger.valueOf(01234), request)).isTrue();
    assertThat(digitAdapter.validate(BigDecimal.valueOf(01234), request)).isTrue();
  }

  @Test
  void testInValidFraction() {
    assertThat(digitAdapter.validate(0.12345f, request)).isTrue();
    assertThat(digitAdapter.validate(0.12345D, request)).isTrue();
    assertThat(digitAdapter.validate(BigDecimal.valueOf(0.12345), request)).isTrue();
  }
}
