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
  void continueOnInvalid_expect_false() {
    //BUG: This should really return true? - should continue validation !!
    assertThat(digitAdapter.validate(BigDecimal.valueOf(0.123456789), request, "foo")).isFalse();
  }

  @Test
  void testNull() {
    assertThat(isValid(digitAdapter, null)).isTrue();
  }

  @Test
  void testValid() {
    assertThat(isValid(digitAdapter, 0)).isTrue();
    assertThat(isValid(digitAdapter, 0f)).isTrue();
    assertThat(isValid(digitAdapter, 0D)).isTrue();
    assertThat(isValid(digitAdapter, 0L)).isTrue();
    assertThat(isValid(digitAdapter, (short) 0)).isTrue();
    assertThat(isValid(digitAdapter, (byte) 0)).isTrue();
    assertThat(isValid(digitAdapter, BigInteger.ZERO)).isTrue();
    assertThat(isValid(digitAdapter, BigDecimal.ZERO)).isTrue();
  }

  @Test
  void testInValid() {
    assertThat(isValid(digitAdapter, 01234)).isTrue();
    assertThat(isValid(digitAdapter, 01234f)).isTrue();
    assertThat(isValid(digitAdapter, 01234D)).isTrue();
    assertThat(isValid(digitAdapter, 01234L)).isTrue();
    assertThat(isValid(digitAdapter, (short) 01234)).isTrue();
    assertThat(isValid(digitAdapter, (byte) 01234)).isTrue();
    assertThat(isValid(digitAdapter, BigInteger.valueOf(01234))).isTrue();
    assertThat(isValid(digitAdapter, BigDecimal.valueOf(01234))).isTrue();
  }

  @Test
  void testInValidFraction() {
    assertThat(isValid(digitAdapter, 0.12345f)).isTrue();
    assertThat(isValid(digitAdapter, 0.12345D)).isTrue();
    assertThat(isValid(digitAdapter, BigDecimal.valueOf(0.12345))).isTrue();
  }
}
